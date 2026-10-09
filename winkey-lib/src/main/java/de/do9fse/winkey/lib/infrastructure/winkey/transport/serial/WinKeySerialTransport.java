package de.do9fse.winkey.lib.infrastructure.winkey.transport.serial;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PushbackInputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.time.Instant;
import java.util.HexFormat;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortTimeoutException;

import de.do9fse.winkey.lib.core.model.ApplicationContext;
import de.do9fse.winkey.lib.core.model.WinKeyJob;
import de.do9fse.winkey.lib.core.model.WinKeyState;
import de.do9fse.winkey.lib.core.model.commands.CommandInfo;
import de.do9fse.winkey.lib.core.model.commands.WinKeyCommand;
import de.do9fse.winkey.lib.core.model.commands.admin.EchoTestCommand;
import de.do9fse.winkey.lib.core.model.commands.admin.SetWK1ModeCommand;
import de.do9fse.winkey.lib.core.model.commands.admin.SetWK2ModeCommand;
import de.do9fse.winkey.lib.core.model.error.WinKeyApplicationException;
import de.do9fse.winkey.lib.core.model.error.WinKeyRuntimeException;
import de.do9fse.winkey.lib.core.model.responses.EmptyResponse;
import de.do9fse.winkey.lib.core.model.responses.ResponseConfiguration;
import de.do9fse.winkey.lib.core.model.responses.SpeedPotValueResponse;
import de.do9fse.winkey.lib.core.model.responses.WinKeyResponse;
import de.do9fse.winkey.lib.core.model.responses.WinKeyStatusResponse;
import de.do9fse.winkey.lib.core.port.in.WinKeyUnsolicitedResponseListener;
import de.do9fse.winkey.lib.core.port.out.WinKeyJobQueue;
import de.do9fse.winkey.lib.core.port.out.WinKeyTransport;

public class WinKeySerialTransport implements WinKeyTransport {
    private static final Logger LOGGER = LoggerFactory.getLogger(WinKeySerialTransport.class);
    
    private final SerialPort serialPort;

    private final CopyOnWriteArrayList<WinKeyUnsolicitedResponseListener> unsolicitedResponseListeners = new CopyOnWriteArrayList<>();

    private Thread serialPortReaderThread;
    private CompletableFuture<Void> serialPortReaderThreadResult;

    private final WinKeyJobQueue jobQueue;

    private PushbackInputStream inputStream;
    private OutputStream outputStream;

    private volatile ApplicationContext context;

    private volatile boolean wk2StatusMode;

    private final HexFormat formatter = HexFormat.of().withPrefix("0x").withSuffix(" ");

    public WinKeySerialTransport(final ApplicationContext context, final WinKeyJobQueue jobQueue, final SerialPort serialPort) {
        this.context = Objects.requireNonNull(context, "Application context must not be null");
        this.jobQueue = Objects.requireNonNull(jobQueue, "Job queue must not be null");
        this.serialPort = Objects.requireNonNull(serialPort, "Serial port must not be null");
    }

    @Override
    public void open() throws WinKeyApplicationException {
        if (context.getState() != WinKeyState.CLOSED) {
            throw new IllegalStateException("Transport is already initializing or ready");
        }
        
        this.serialPort.openPort();
        LOGGER.info("Successfully opened device {}", serialPort.getSystemPortPath());

        this.inputStream = new PushbackInputStream(serialPort.getInputStream());
        this.outputStream = serialPort.getOutputStream();

        context.transitionToState(WinKeyState.INITIALIZING);
    }

    @Override
    public void close() throws WinKeyApplicationException {
        if (context.getState() != WinKeyState.READY && context.getState() != WinKeyState.INITIALIZING) {
            throw new IllegalStateException("Transport is already closed");
        }

        if (context.getState() == WinKeyState.READY) {
            stopSerialReaderThread();
        }

        serialPort.closePort();
        context.transitionToState(WinKeyState.CLOSED);
    }

    @Override
    public void initialize(final long timeout, final TimeUnit unit) throws TimeoutException, WinKeyRuntimeException {
        if (context.getState() != WinKeyState.INITIALIZING) {
            throw new IllegalStateException("Transport is ready or closed");
        }

        Objects.requireNonNull(unit, "Time unit must not be null");

        if (timeout < 1) {
            throw new IllegalArgumentException(formatError("The timeout must be greater than zero, but was {0}", timeout));
        }

        final Instant now = Instant.now();
        final Instant responseDeadline = now.plus(timeout, unit.toChronoUnit());

        try {
            final char echoChar = 'R';
            final WinKeyCommand echoTestCommand = new EchoTestCommand(echoChar);

            for (int retry = 1; Instant.now().isBefore(responseDeadline); retry++) {
                this.outputStream.write(echoTestCommand.toProtocolBytes());

                try {
                    final int data = readData();
                    final char response = (char) data;

                    if (response == echoChar) {
                        startSerialPortReaderThread();
                        context.transitionToState(WinKeyState.READY);
                        return;
                    }
                } catch(final SerialPortTimeoutException e) {
                    LOGGER.debug("Waiting for response timed out on attempt #{}", retry);
                }
            }
        } catch(final IOException e) {
            throw new WinKeyRuntimeException("Sending and waiting on response failed", e);
        }

        throw new TimeoutException("Failed to initialize transport");
    }

    @Override
    public void addUnsolicitedResponseListener(final WinKeyUnsolicitedResponseListener listener) {
        this.unsolicitedResponseListeners.addIfAbsent(Objects.requireNonNull(listener, "listener must not be null"));
    }

    @Override
    public void removeUnsolicitedResponseListener(final WinKeyUnsolicitedResponseListener listener) {
        this.unsolicitedResponseListeners.remove(Objects.requireNonNull(listener, "listener must not be null"));
    }

    private void notifyUnsolicitedResponseListeners(final WinKeyResponse response) {
        for (final WinKeyUnsolicitedResponseListener listener : this.unsolicitedResponseListeners) {
            try {
                listener.onUnsolicitedResponse(response);
            } catch (final RuntimeException exception) {
                LOGGER.error("Unsolicited response listener failed for response {}", response, exception);
            }
        }
    }

    private void startSerialPortReaderThread() {
        this.serialPortReaderThreadResult = new CompletableFuture<>();
        this.serialPortReaderThread = Thread
            .ofVirtual()
            .name("SerialPortReader")
            .start(() -> serialPortReader(serialPortReaderThreadResult));
    }

    private void stopSerialReaderThread() {
        if (this.serialPortReaderThread != null) {
            this.serialPortReaderThread.interrupt();

            try {
                this.serialPortReaderThreadResult.get(5, TimeUnit.SECONDS);
            } catch (final InterruptedException e) {
                LOGGER.error("Stopping port reader thread failed", e);
            } catch (final ExecutionException e) {
                LOGGER.error("Error stopping port reader thread", e.getCause());
            } catch (final TimeoutException e) {
                LOGGER.warn("Stopping serial port reader thread timed out");
            }

            LOGGER.info("Successfully stopped serial port reader thread");
        }
    }
   
    @Override
    public WinKeyJob submitJob(final WinKeyCommand command, final CompletableFuture<WinKeyResponse> jobResult) throws WinKeyApplicationException {
        return submitJob(command, jobResult, true);
    }

    public WinKeyJob submitJob(final WinKeyCommand command, final CompletableFuture<WinKeyResponse> jobResult, final boolean doSend) throws WinKeyApplicationException {
        if (context.getState() != WinKeyState.READY) {
            throw new IllegalStateException("Transport is not ready");
        }

        final WinKeyJob job = new WinKeyJob(command, jobResult);

        try {
            this.jobQueue.lock();

            if (!this.jobQueue.offer(job)) {
                throw new WinKeyRuntimeException(formatError("Failed to queue job {0}", job));
            }

            if (doSend) {
                try {
                    this.sendCommand(job);
                } catch (final WinKeyApplicationException | RuntimeException exception) {
                    this.jobQueue.pollLast();
                    throw exception;
                }
            }
        } finally {
            this.jobQueue.unlock();
        }

        return job;
    }

    private void sendCommand(final WinKeyJob job) throws WinKeyApplicationException {
        final byte[] serialBytes = job.command().toProtocolBytes();

        try {
            outputStream.write(serialBytes);
        } catch (final IOException e) {
            throw new WinKeyRuntimeException("Failed to send command", e);
        }

        LOGGER.debug("Successfully sent command " + job.command());
    }

    private void serialPortReader(final CompletableFuture<Void> threadResult) {
        while (true) {
            try {
                handleStatusBytes();
                processActiveJob();

                if (Thread.currentThread().isInterrupted()) {
                    threadResult.complete(null);
                    break;
                }
            } catch (final RuntimeException | IOException  e) {
                this.jobQueue.failPendingJobs(e);
                threadResult.completeExceptionally(e);
                break;
            }
        }
    }

    private void handleStatusBytes() throws IOException {
        final int data;
        try {
            data = readData();
        } catch (final SerialPortTimeoutException e) {
            return;
        }

        if (isStatusByteHandled(data) || isSpeedPotByteHandled(data) || isOutOfBandHandled(data)) {
            return;
        }

        LOGGER.debug("Unreading {}", this.formatter.formatHex(new byte[] { (byte) data }));

        // Push unused data
        this.inputStream.unread(data);
    }

    private boolean isStatusByteHandled(final int data) {
        if (!isStatusByte(data)) {
            return false;
        }

        try {
            final WinKeyStatusResponse statusResponse = WinKeyStatusResponse.fromProtocol(new byte[] {(byte) data}, wk2StatusMode);
            notifyUnsolicitedResponseListeners(statusResponse);
            LOGGER.debug("Notified unsolicited status response {}", statusResponse);
        } catch (final IllegalArgumentException exception) {
            throw new WinKeyRuntimeException("Invalid unsolicited status response", exception);
        }

        return true;
    }

    private boolean isSpeedPotByteHandled(final int data) {
        if (!isSpeedPotByte(data)) {
            return false;
        }

        try {
            final SpeedPotValueResponse speedPotResponse = SpeedPotValueResponse.fromProtocol(new byte[] {(byte) data});
            notifyUnsolicitedResponseListeners(speedPotResponse);
            LOGGER.debug("Notified unsolicited speed pot response {}", formatter.toHexDigits(data));

        } catch (final IllegalArgumentException exception) {
            throw new WinKeyRuntimeException("Invalid unsolicited speed-pot response", exception);
        }

        return true;
    }

    private boolean isOutOfBandHandled(final int data) {
        final WinKeyJob job = this.jobQueue.peek();

        if (job == null) {
            LOGGER.debug("Out of band data handled {}", (char) data);
            return true;
        }

        return false;
    }

    private boolean isStatusByte(final int receivedByte) {
        return (receivedByte & 0xE0) == 0xC0;
    }

    private boolean isSpeedPotByte(final int receivedByte) {
        return (receivedByte & 0xC0) == 0x80;
    }

    private void processActiveJob() throws IOException {
        final WinKeyJob job = this.jobQueue.peek();

        if (job == null) {
            return;
        }

        final CommandInfo commandInfo = job.command().getCommandInfo();
        final Class<WinKeyResponse> responseType = commandInfo.responseType();

        if (responseType.equals(EmptyResponse.class)) {
            final EmptyResponse emptyResponse = new EmptyResponse();
            completeActiveJob(emptyResponse);
        } else {
            final ResponseConfiguration responseConfiguration = responseType.getAnnotation(ResponseConfiguration.class);
            final int expectedResponseByte = responseConfiguration.expectedResponseBytes();

            final byte[] buffer = new byte[expectedResponseByte];
            try {
                readData(buffer);
            } catch (final SerialPortTimeoutException e) {
                return;
            }

            try {
                final Method factory = responseType.getMethod("fromProtocol", byte[].class);
                final WinKeyResponse response = (WinKeyResponse) factory.invoke(null, buffer);
                completeActiveJob(response);
            } catch (final NoSuchMethodException | SecurityException | IllegalAccessException | InvocationTargetException e) {
                final String message = MessageFormat.format(
                    "Failed to create response for active job {0}",
                    job
                );

                throw new WinKeyRuntimeException(message, e);
            }
        }
    }

    private void completeActiveJob(final WinKeyResponse response) {
        try {
            this.jobQueue.lock();

            final WinKeyJob job = this.jobQueue.poll();
            final CompletableFuture<WinKeyResponse> responseHolder = job.response();

            responseHolder.complete(response);

            updateStatusMode(job.command());
        } finally {
            this.jobQueue.unlock();
        }
    }

    private void updateStatusMode(final WinKeyCommand command) {
        if (command instanceof SetWK2ModeCommand) {
            this.wk2StatusMode = true;
        } else if (command instanceof SetWK1ModeCommand) {
            this.wk2StatusMode = false;
        }
    }

    private int readData() throws IOException {
        final int data = this.inputStream.read();

        if (data < 0) {
            throw new WinKeyRuntimeException("Connection to device broken");
        }

        return data;
    }

    private int readData(final byte[] buffer) throws IOException {
        final int receivedBytes = this.inputStream.read(buffer);

        if (receivedBytes < 0) {
            throw new WinKeyRuntimeException("Connection to device broken");
        }

        if (receivedBytes < buffer.length) {
            final String message = MessageFormat.format(
                "Got only partial response. Expected {0}, received {1}",
                buffer.length,
                receivedBytes
            );

            throw new WinKeyRuntimeException(message);
        }

        return receivedBytes;
    }

    private String formatError(final String pattern, final Object ... args) {
        return MessageFormat.format(pattern, args);
    }
}
