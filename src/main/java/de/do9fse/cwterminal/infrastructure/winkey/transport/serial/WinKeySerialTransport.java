package de.do9fse.cwterminal.infrastructure.winkey.transport.serial;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.HexFormat;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fazecast.jSerialComm.SerialPort;

import de.do9fse.cwterminal.core.model.WinKeyJob;
import de.do9fse.cwterminal.core.model.commands.CommandInfo;
import de.do9fse.cwterminal.core.model.commands.WinKeyCommand;
import de.do9fse.cwterminal.core.model.commands.admin.HostCloseCommand;
import de.do9fse.cwterminal.core.model.commands.admin.HostOpenCommand;
import de.do9fse.cwterminal.core.model.error.WinKeyApplicationException;
import de.do9fse.cwterminal.core.model.error.WinKeyRuntimeException;
import de.do9fse.cwterminal.core.model.responses.ResponseConfiguration;
import de.do9fse.cwterminal.core.model.responses.WinKeyResponse;
import de.do9fse.cwterminal.core.port.out.WinKeyTransport;

public class WinKeySerialTransport implements WinKeyTransport {
    private static final Logger LOGGER = LoggerFactory.getLogger(WinKeySerialTransport.class);
    
    private final SerialPort serialPort;

    private Thread serialPortReaderThread;
    private CompletableFuture<Void> serialPortReaderThreadResult;

    private Lock jobQueueLock;
    private Queue<WinKeyJob> jobQueue;

    private final HexFormat formatter = HexFormat.of().withPrefix("0x").withSuffix(" ");

    public WinKeySerialTransport(final SerialPort serialPort) {
        this.serialPort = serialPort;
        this.jobQueueLock = new ReentrantLock();
        this.jobQueue = new LinkedBlockingQueue<>();
    }

    @Override
    public void open() throws WinKeyApplicationException {
        if (this.serialPort.isOpen()) {
            throw new WinKeyApplicationException("Serial port is already open");
        }
        
        this.serialPort.openPort();
        LOGGER.info("Successfully opened device {}", serialPort.getSystemPortPath());

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

        final CompletableFuture<WinKeyResponse> jobResult = new CompletableFuture<WinKeyResponse>();
        this.submitJob(new HostCloseCommand(), jobResult);

        try {
            jobResult.get(5000, TimeUnit.MILLISECONDS);
        } catch (final InterruptedException | ExecutionException | TimeoutException e) {
            LOGGER.warn("Failed to close device");
        }

        stopSerialReaderThread();
        serialPort.closePort();

        LOGGER.info("Successfully closed transport");
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
        this.jobQueueLock.lock();
        final WinKeyJob job = new WinKeyJob(command, jobResult);
        this.jobQueue.offer(job);

        try {
            if (doSend) {
                this.sendCommand(job);
            }
        } catch (final WinKeyRuntimeException | WinKeyApplicationException e) {
            this.jobQueue.poll();
            throw e;
        } finally {
            this.jobQueueLock.unlock();
        }

        return job;
    }

    private void sendCommand(final WinKeyJob job) throws WinKeyApplicationException {
        final byte[] serialBytes = job.command().toProtocolBytes();
        final int writtenBytes = serialPort.writeBytes(serialBytes, serialBytes.length);

        if (writtenBytes == -1) {
            throw new WinKeyRuntimeException("Failed to send command" + job.command());
        }

        if (writtenBytes < serialBytes.length) {
            throw new WinKeyApplicationException("Only could partially send command");
        }

        LOGGER.debug("Successfully sent command " + job.command());
    }

    private void serialPortReader(final CompletableFuture<Void> threadResult) {
        while(true) {
            if (Thread.currentThread().isInterrupted()) {
                threadResult.complete(null);
                return;
            }

            final Optional<byte []> unconsumedResponseBytesHolder = processUnsolicitedStatusTransmission();
            processActiveCommand(unconsumedResponseBytesHolder);
        }
    }

    private Optional<byte[]> processUnsolicitedStatusTransmission() {
        final byte[] response = new byte[1];
        final int receivedResponseByte = this.serialPort.readBytes(response, 1);

        // Check for timeout
        if (receivedResponseByte == 0) {
            return Optional.empty();
        }

        final byte receivedByte = response[0];

        // check for status bytes
        if (isStatusByte(receivedByte)) {
            LOGGER.info("Received status byte: {}", formatter.toHexDigits(receivedByte));
            
            // TODO Implement status bytes
            return Optional.empty();
        }

        // check for status bytes
        if (isSpeedPotByte(receivedByte)) {
            LOGGER.info("Received speed pot byte: {}", formatter.toHexDigits(receivedByte));

            // TODO Implement speed pot bytes
            return Optional.empty();
        }

        return Optional.of(response);
    }

    private void processActiveCommand(final Optional<byte[]> unconsumedResponseBytesHolder) {
        try {
            this.jobQueueLock.lock();
            final WinKeyJob activeJob = jobQueue.poll();

            final byte[] unconsumedResponseBytes = unconsumedResponseBytesHolder.isPresent() ? unconsumedResponseBytesHolder.get() : new byte[0];

            if (activeJob == null) {
                if (unconsumedResponseBytes.length > 0) {
                    LOGGER.warn("Received response bytes {} without active command", unconsumedResponseBytes);
                }

                return;
            }

            final CommandInfo commandInfo = activeJob.command().getCommandInfo();
            final Class<WinKeyResponse> responseType = commandInfo.responseType();
            final ResponseConfiguration responseConfiguration = responseType.getAnnotation(ResponseConfiguration.class);

            if (responseConfiguration == null) {
                final String message = MessageFormat.format(
                    "Response type {0} lacks required annotation {1}",
                    responseType.getSimpleName(),
                    ResponseConfiguration.class.getSimpleName()
                );

                throw new WinKeyRuntimeException(message);
            }

            final WinKeyResponse response = createResponse(unconsumedResponseBytes, responseType, responseConfiguration.expectedResponseBytes());
            LOGGER.debug("Received response {}", response);
            activeJob.response().complete(response);
        } finally {
            this.jobQueueLock.unlock();
        }
    }

    private WinKeyResponse createResponse(final byte[] unconsumedResponseBytes, final Class<WinKeyResponse> resultType, final int expectedResponseBytes) {
        final byte[] responseBytes = new byte[expectedResponseBytes];

        final int openResponseBytes = expectedResponseBytes - unconsumedResponseBytes.length;
        System.arraycopy(unconsumedResponseBytes, 0, responseBytes, 0, unconsumedResponseBytes.length);
        readUntilResponseComplete(responseBytes, unconsumedResponseBytes, openResponseBytes);

        try {
            final Method factory = resultType.getMethod("parseResponse", byte[].class);
            return (WinKeyResponse) factory.invoke(null, responseBytes);
        } catch (
            final IllegalAccessException
                | IllegalArgumentException
                | InvocationTargetException
                | NoSuchMethodException
                | SecurityException e
            ) {
                final String message = MessageFormat.format(
                    "Failed to create result type {0}",
                    resultType.getSimpleName()
                );

                throw new WinKeyRuntimeException(message);
        }
    }

    private void readUntilResponseComplete(final byte[] buffer, final byte[] unconsumedResponseBytes, final int expectedResponseBytes) {
        int receivedBytes = unconsumedResponseBytes.length;
        while (receivedBytes < expectedResponseBytes) {
            // Read next chunk of data
            final int nextBytes = this.serialPort.readBytes(buffer, expectedResponseBytes - receivedBytes, receivedBytes);

            // Calculate already received bytes
            receivedBytes = receivedBytes + nextBytes;
        }
    }

    private boolean isStatusByte(final byte receivedByte) {
        return (receivedByte & 0xC0) == 0xC0;
    }

    private boolean isSpeedPotByte(final byte receivedByte) {
        return (receivedByte & 0xC0) == 0x80;
    }
}
