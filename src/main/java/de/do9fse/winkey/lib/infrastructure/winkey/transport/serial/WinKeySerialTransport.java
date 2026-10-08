package de.do9fse.winkey.lib.infrastructure.winkey.transport.serial;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PushbackInputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.MessageFormat;
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

import de.do9fse.winkey.lib.core.model.WinKeyJob;
import de.do9fse.winkey.lib.core.model.commands.CommandInfo;
import de.do9fse.winkey.lib.core.model.commands.WinKeyCommand;
import de.do9fse.winkey.lib.core.model.commands.admin.HostOpenCommand;
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

    private volatile boolean wk2StatusMode;

    private final HexFormat formatter = HexFormat.of().withPrefix("0x").withSuffix(" ");

    public WinKeySerialTransport(final SerialPort serialPort) {
        this.serialPort = Objects.requireNonNull(serialPort, "Serial port must not be null");
        this.jobQueue = new WinKeyJobQueue();
    }

    @Override
    public void open() throws WinKeyApplicationException {
        if (this.serialPort.isOpen()) {
            throw new WinKeyApplicationException("Serial port is already open");
        }
        
        this.serialPort.openPort();
        LOGGER.info("Successfully opened device {}", serialPort.getSystemPortPath());

        this.inputStream = new PushbackInputStream(serialPort.getInputStream());
        this.outputStream = serialPort.getOutputStream();

        startSerialPortReaderThread();

        final CompletableFuture<WinKeyResponse> jobResult = new CompletableFuture<WinKeyResponse>();
        final WinKeyCommand command = new HostOpenCommand();
        final WinKeyJob job = this.submitJob(command, jobResult, false);

        int maxRetries = 10;
        for (int retries = 0; retries < maxRetries; retries++) {
            this.sendCommand(job);

            try {
                final WinKeyResponse version = jobResult.get(1, TimeUnit.SECONDS);
                LOGGER.info("Received version {}", version);

                // Signal that initializing attempts were successful
                break;
            } catch (final ExecutionException e) {
                throw new WinKeyRuntimeException("Failed to initialize device", e.getCause());
            } catch (final TimeoutException | InterruptedException e) {
                final int retriesLeft = maxRetries - retries - 1;
                if (retriesLeft < 1) {
                    throw new WinKeyRuntimeException("Failed to initialize device after " + maxRetries + " attempts");
                }

                LOGGER.debug("#{} initializing attempts left", retriesLeft);
            }
        }

        LOGGER.info("Successfully opened and initialized device");
    }

    @Override
    public void close() throws WinKeyApplicationException {
        if (!serialPort.isOpen()) {
            throw new WinKeyApplicationException("Serial port is not open");
        }

        stopSerialReaderThread();
        serialPort.closePort();

        LOGGER.info("Successfully closed transport");
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
        return this.submitJob(command, jobResult, true);
    }

    private WinKeyJob submitJob(final WinKeyCommand command, final CompletableFuture<WinKeyResponse> jobResult, final boolean doSend) throws WinKeyApplicationException {
        final WinKeyJob job = new WinKeyJob(command, jobResult);
        this.jobQueue.submit(job, queuedJob -> {
            if (doSend) {
                this.sendCommand(queuedJob);
            }
        });
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
        final WinKeyJob activeJob = peekActiveJob();

        if (activeJob == null) {
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
        final WinKeyJob activeJob = peekActiveJob();

        if (activeJob == null) {
            return;
        }

        final CommandInfo commandInfo = activeJob.command().getCommandInfo();
        final Class<WinKeyResponse> responseType = commandInfo.responseType();

        if (responseType.equals(EmptyResponse.class)) {
            final EmptyResponse emptyResponse = new EmptyResponse();
            consumeActiveJob(emptyResponse);
            LOGGER.debug("Completed job {} with response {}", activeJob, emptyResponse);
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
                consumeActiveJob(response);
                LOGGER.debug("Completed job {} with response {}", activeJob, response);
            } catch (final NoSuchMethodException | SecurityException | IllegalAccessException | InvocationTargetException e) {
                final String message = MessageFormat.format(
                    "Failed to create response for active job {0}",
                    activeJob
                );

                throw new WinKeyRuntimeException(message, e);
            }
        }
    }

    private WinKeyJob peekActiveJob() {
        return this.jobQueue.peekActiveJob();
    }

    private void consumeActiveJob(final WinKeyResponse response) {
        this.jobQueue.consumeActiveJob(response, this::updateStatusMode);
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
}
