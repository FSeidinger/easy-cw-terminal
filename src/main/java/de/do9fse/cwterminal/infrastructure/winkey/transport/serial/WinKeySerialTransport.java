package de.do9fse.cwterminal.infrastructure.winkey.transport.serial;

import java.text.MessageFormat;
import java.util.HexFormat;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.naming.OperationNotSupportedException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fazecast.jSerialComm.SerialPort;

import de.do9fse.cwterminal.core.model.WinKeyJob;
import de.do9fse.cwterminal.core.model.WinKeyVersion;
import de.do9fse.cwterminal.core.model.commands.WinKeyCommand;
import de.do9fse.cwterminal.core.model.commands.admin.HostOpenCommand;
import de.do9fse.cwterminal.core.model.error.WinKeyApplicationException;
import de.do9fse.cwterminal.core.model.error.WinKeyRuntimeException;
import de.do9fse.cwterminal.core.port.out.WinKeyTransport;
import de.do9fse.cwterminal.infrastructure.winkey.CommandFactory;

public class WinKeySerialTransport implements WinKeyTransport {
    private static final Logger LOGGER = LoggerFactory.getLogger(WinKeySerialTransport.class);
    
    private final SerialPort serialPort;
    private CommandFactory factory;

    private Thread serialPortReaderThread;
    private CompletableFuture<Void> serialPortReaderThreadResult;

    private Lock jobQueueLock;
    private Queue<WinKeyJob<?>> jobQueue;

    private final HexFormat formatter = HexFormat.of().withPrefix("0x").withSuffix(" ");

    public WinKeySerialTransport(final SerialPort serialPort) {
        this.serialPort = serialPort;
        this.factory = new CommandFactory(new WinKeyVersion(1, 0));
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

        final CompletableFuture<WinKeyVersion> winKeyVersionFuture =  new CompletableFuture<WinKeyVersion>();
        final WinKeyJob<WinKeyVersion> job = this.submitJob(new HostOpenCommand(), winKeyVersionFuture, false);

        int maxRetries = 10;
        for (int retries = 0; retries < maxRetries; retries++) {
            this.sendCommand(job);

            try {
                final WinKeyVersion version = winKeyVersionFuture.get(1, TimeUnit.SECONDS);
                LOGGER.info("Received version {}", version);
                this.factory = new CommandFactory(version);

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
    public WinKeyJob<Void> submitJob(WinKeyCommand command) throws WinKeyApplicationException {
        return this.submitJob(command, new CompletableFuture<>());
    }

    @Override
    public <R> WinKeyJob<R> submitJob(WinKeyCommand command, CompletableFuture<R> result) throws WinKeyApplicationException {
        return this.submitJob(command, result, false);
    }

    public <R> WinKeyJob<R> submitJob(WinKeyCommand command, CompletableFuture<R> result, final boolean doSend) throws WinKeyApplicationException {
        this.jobQueueLock.lock();
        final WinKeyJob<R> job = WinKeyJob.of(command, result);
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

    private <R> void sendCommand(final WinKeyJob<R> job) throws WinKeyApplicationException {
        try {
            final byte[] serialBytes = this.factory.from(job.command());
            final int writtenBytes = serialPort.writeBytes(serialBytes, serialBytes.length);

            if (writtenBytes == -1) {
                throw new WinKeyRuntimeException("Failed to send command" + job.command());
            }

            if (writtenBytes < serialBytes.length) {
                throw new WinKeyApplicationException("Only could partially send command");
            }
        } catch(final OperationNotSupportedException e) {
            throw new WinKeyRuntimeException("Failed to convert to serial bytes", e);
        }

        LOGGER.debug("Successfully sent command " + job.command());
    }

    @SuppressWarnings("unchecked")
    private <R> R receiveResponse(final WinKeyJob<R> job) throws WinKeyApplicationException {
        while(true) {
            int expectedResponseBytes = switch (job.command()) {
                case HostOpenCommand c -> 1;
                default -> throw new WinKeyRuntimeException("Command " + job.command() + " not supported");
            };

            if (expectedResponseBytes > 0) {
                LOGGER.debug("Expecting {} bytes from device", expectedResponseBytes);

                final byte[] responseBuffer = new byte[expectedResponseBytes];
                final int receivedBytes = this.serialPort.readBytes(responseBuffer, expectedResponseBytes);

                if (receivedBytes == -1) {
                    final String message = MessageFormat.format("Failed to read {0} bytes from {1}", expectedResponseBytes, serialPort.getSystemPortPath());
                    throw new WinKeyRuntimeException(message);
                }

                if (receivedBytes < expectedResponseBytes) {
                    final String message = MessageFormat.format("Reading {0} bytes from {1} timed out", expectedResponseBytes, serialPort.getSystemPortPath());
                    throw new WinKeyApplicationException(message);
                }

                LOGGER.debug("Received response: {}", formatter.formatHex(responseBuffer));

                final R result = switch (job.command()) {
                    case HostOpenCommand c -> (R) new WinKeyVersion(responseBuffer[0] / 10, responseBuffer[0] % 10);
                    default -> throw new WinKeyRuntimeException("Command " + job.command() + " not supported");
                };

                return result;
            }

            final byte[] buffer = new byte[1];
            this.serialPort.readBytes(buffer, buffer.length);

            final byte receivedByte = buffer[0];

            // check for status bytes
            if (isStatusByte(receivedByte)) {
                LOGGER.info("Received status byte: {}", formatter.toHexDigits(receivedByte));
                
                // TODO Implement status bytes
                continue;
            }

            // check for status bytes
            if (isSpeedPotByte(receivedByte)) {
                LOGGER.info("Received speed pot byte: {}", formatter.toHexDigits(receivedByte));

                // TODO Implement speed pot bytes
                continue;
            }

            LOGGER.info("Received response: {}", formatter.toHexDigits(receivedByte));
            break;
        }

        return null;
    }

    private void serialPortReader(final CompletableFuture<Void> threadResult) {
        while(true) {
            if (Thread.currentThread().isInterrupted()) {
                threadResult.complete(null);
                return;
            }

            final byte[] buffer = new byte[1];
            final int receivedBytes = this.serialPort.readBytes(buffer, buffer.length);

            // Check for error on serial device
            if (receivedBytes == -1) {
                final String message = MessageFormat.format("Failed to read next byte from {0}", serialPort.getSystemPortPath());
                final WinKeyRuntimeException e = new WinKeyRuntimeException(message);

                // Signal error
                threadResult.completeExceptionally(e);
                return;
            }

            // Check for timeout
            if (receivedBytes == 0) {
                continue;
            }

            final byte receivedByte = buffer[0];

            // check for status bytes
            if (isStatusByte(receivedByte)) {
                LOGGER.info("Received status byte: {}", formatter.toHexDigits(receivedByte));
                
                // TODO Implement status bytes
                continue;
            }

            // check for status bytes
            if (isSpeedPotByte(receivedByte)) {
                LOGGER.info("Received speed pot byte: {}", formatter.toHexDigits(receivedByte));

                // TODO Implement speed pot bytes
                continue;
            }

            handleCommandWithResult(receivedByte);
        }
    }

    private void handleCommandWithResult(final byte receivedByte) {
        try {
            this.jobQueueLock.lock();
            final WinKeyJob<?> winKeyJob = jobQueue.poll();

            if (winKeyJob != null) {
                LOGGER.info("Handling result for command {}", winKeyJob.command());

                switch (winKeyJob.command()) {
                    case HostOpenCommand c ->  {
                            @SuppressWarnings("unchecked")
                            final CompletableFuture<WinKeyVersion> jobResult = (CompletableFuture<WinKeyVersion>) winKeyJob.result();
                            jobResult.complete(new WinKeyVersion(receivedByte / 10, receivedByte % 10));
                        }

                    default -> LOGGER.warn("Command {} not supported", winKeyJob.command());
                };
            }
        } finally {
            this.jobQueueLock.unlock();
        }
    }

    private boolean isStatusByte(final byte receivedByte) {
        return (receivedByte & 0xC0) == 0xC0;
    }

    private boolean isSpeedPotByte(final byte receivedByte) {
        return (receivedByte & 0xC0) == 0x80;
    }
}
