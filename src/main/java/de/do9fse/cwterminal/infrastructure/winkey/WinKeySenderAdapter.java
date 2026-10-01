package de.do9fse.cwterminal.infrastructure.winkey;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.concurrent.TimeoutException;

import javax.naming.OperationNotSupportedException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.do9fse.cwterminal.core.model.commands.HostOpenCommand;
import de.do9fse.cwterminal.core.model.commands.KeyerCommand;
import de.do9fse.cwterminal.core.port.out.ApplicationContext;
import de.do9fse.cwterminal.core.port.out.WinKeySender;

public class WinKeySenderAdapter implements WinKeySender {
    private static final Logger LOGGER = LoggerFactory.getLogger(WinKeySenderAdapter.class);

    private final CommandFactory factory;
    private final KeyerCommandQueue queue;

    private final ByteTransport transport;


    public WinKeySenderAdapter(final ApplicationContext context, final ByteTransport transport) {
        Objects.requireNonNull(context, "Application context must not be null");
        this.factory = context.getFactory();
        this.queue = context.getQueue();

        this.transport = Objects.requireNonNull(transport, "Byte transport must not be null");
    }

    @Override 
    public void initialize() throws TimeoutException, OperationNotSupportedException {
        final Duration totalTimeout = Duration.ofSeconds(5);
        final Duration retryInterval = Duration.ofMillis(1000);
        final Instant deadLine = Instant.now().plus(totalTimeout);

        LOGGER.info("Initiating open host sequence");

        while (Instant.now().isBefore(deadLine)) {
            final HostOpenCommand command = new HostOpenCommand();
            final byte[] buffer = factory.from(command);

            try {
                this.transport.discardInput();
                this.transport.send(buffer);
                LOGGER.debug("Open host command sent.");

                LOGGER.debug("Waiting {} ms for response", retryInterval.toMillis());
                final Instant responseDeadline = Instant.now().plus(retryInterval);
                while (Instant.now().isBefore(responseDeadline)) {
                    if (this.transport.bytesAvailable() > 0) {
                        // Storing open host command for later processing
                        this.queue.beginSendTransaction();
                        this.queue.offer(command);
                        this.queue.commitSendTransaction();

                        LOGGER.info("Open host sequence successfully initiated");

                        return;
                    }

                    Thread.sleep(100);
                }
            } catch (final InterruptedException e) {
                LOGGER.warn("Interrupted while waiting on response", e);
            } catch (final IOException e) {
                LOGGER.warn("Open host sequence failed", e);
            }
        }

        throw new TimeoutException(
            "Keyer did not respond to Open Host command within " + totalTimeout.toSeconds() + " seconds."
        );
    }

    @Override
    public void sendCommand(final KeyerCommand command) throws IOException, OperationNotSupportedException {
        Objects.requireNonNull(command, "Command must not be null");

        boolean success = false;
        this.queue.beginSendTransaction();

        try {
            this.queue.offer(command);

            final byte[] buffer = factory.from(command);
            this.transport.send(buffer);

            LOGGER.info("Successfully sent command {}", command);

            success = true;
        } finally {
            if (success) {
                this.queue.commitSendTransaction();
            } else {
                LOGGER.warn("Failed to send command: {}", command);
                this.queue.rollbackSendTransaction();
            }
        }
    }
}
