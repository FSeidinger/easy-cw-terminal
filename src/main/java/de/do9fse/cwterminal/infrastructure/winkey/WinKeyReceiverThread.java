package de.do9fse.cwterminal.infrastructure.winkey;

import java.io.IOException;
import java.time.Duration;
import java.util.HexFormat;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fazecast.jSerialComm.SerialPortTimeoutException;

import de.do9fse.cwterminal.core.model.KeyerCommand;

public class WinKeyReceiverThread {
    private static final Logger LOGGER = LoggerFactory.getLogger(WinKeyReceiverThread.class);

    private final ByteTransport transport;
    private final KeyerCommandQueue commandQueue;

    private final AtomicBoolean running = new AtomicBoolean(false);
    private volatile Thread receiverLoop;

    private final HexFormat formatter = HexFormat.of().withPrefix("0x");

    public WinKeyReceiverThread(final ByteTransport transport, final KeyerCommandQueue commandQueue) {
        this.transport = Objects.requireNonNull(transport, "Transport must not be null");
        this.commandQueue = Objects.requireNonNull(commandQueue, "Keyer command queue must not be null");
    }

    public synchronized void start() {
        if (isRunning()) {
            throw new IllegalStateException("Already started");
        }

        this.receiverLoop = Thread
            .ofVirtual()
            .name("receiver-loop")
            .start(this::run);
    }

    public synchronized void stop() {
        if (!isRunning()) {
            throw new IllegalStateException("Not started");
        }

        running.set(false);

        if (receiverLoop != null) {
            receiverLoop.interrupt();

            try {
                receiverLoop.join(Duration.ofMillis(2000));
            } catch (final InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public boolean isRunning() {
        return running.get();
    }

    public void run() {
        running.set(true);
        
        while (isRunning() && !Thread.currentThread().isInterrupted()) {
            try {
                try {
                    processInput();
                } catch (final SerialPortTimeoutException e) {
                    // Ignore timeouts. They are expected parts of the implementation
                    LOGGER.trace("Currently nothing to do");
                }
            } catch (final IOException e) {
                if (!isRunning()) {
                    break;
                }

                LOGGER.error("Failed to process input", e);
            }
        }

        if (isRunning()) {
            running.set(false);
        }
    }

    private void processInput() throws IOException {
        int receivedByte = this.transport.receive();

        // check for status bytes
        if (isStatusByte(receivedByte)) {
            LOGGER.info("Received status byte: {}", formatter.toHexDigits(receivedByte));

            // TODO Handling of status bytes
            return;
        }

        // check for status bytes
        if (isSpeedPotByte(receivedByte)) {
            LOGGER.info("Received speed pot byte: {}", formatter.toHexDigits(receivedByte));

            // TODO Handling of speed pot bytes
            return;
        }

        final KeyerCommand command = this.commandQueue.poll();
        
        if (command == null) {
            LOGGER.warn("Received out of band byte: {}", formatter.toHexDigits(receivedByte));
            return;
        }

        switch (command) {
            case KeyerCommand.OpenHostCommand openHostCommand -> LOGGER.info("Received version {}", formatter.toHexDigits(receivedByte));
            default -> LOGGER.warn("Unknown command {}", command);
        }
    }

    final boolean isStatusByte(final int receivedByte) {
        return (receivedByte & 0xC0) == 0xC0;
    }

    final boolean isSpeedPotByte(final int receivedByte) {
        return (receivedByte & 0xC0) == 0x80;
    }
}
