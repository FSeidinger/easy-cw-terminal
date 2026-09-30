package de.do9fse.cwterminal.core.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.do9fse.cwterminal.core.model.KeyerCommand.OpenHostCommand;
import de.do9fse.cwterminal.core.model.KeyerEvent.HostOpenedEvent;

/**
 * The keyer session holds the state of the keyer device and its version
 *
 * <p>
 * The session is assumed to be closed after creation.
 * <p>
 *
 * <p>
 * Before opening the keyer device we do not know the version number of the
 * device and therefore the protocol version to use. We assume version 1.0 after
 * creation.
 * </p>
 *
 * <p>
 * When opening the device it will tell us its actual version number and we will
 * store that as information and to select the protocol version, which is tied
 * to the major number.
 * </p>
 */
public class KeyerSession {
    private static final Logger LOGGER = LoggerFactory.getLogger(KeyerSession.class);

    public enum SessionState {
        CLOSED,
        PENDING,
        OPEN
    } 

    private SessionState state;
    private KeyerVersion version;

    public KeyerSession() {
        this.state = SessionState.CLOSED;
        this.version = new KeyerVersion(1, 0);

        LOGGER.info("Session created");
    }

    public SessionState getSessionState() {
        return this.state;
    }

    public KeyerVersion getVersion() {
        return this.version;
    }

    public void handleCommand(final OpenHostCommand command) {
        if (state != SessionState.CLOSED) {
            throw new IllegalStateException("Session already started");
        }

        LOGGER.info("Handling command: {}", command);

        this.state = SessionState.PENDING;
    }

    public void on(final HostOpenedEvent event) {
        if (state != SessionState.PENDING) {
            throw new IllegalStateException("Session is not pending");
            
        }
        LOGGER.info("Received event: {}", event);

        this.version = event.version();
        this.state = SessionState.OPEN;
    }

    @Override
    public String toString() {
        return "KeyerSession [state=" + state + ", version=" + version + "]";
    }
}