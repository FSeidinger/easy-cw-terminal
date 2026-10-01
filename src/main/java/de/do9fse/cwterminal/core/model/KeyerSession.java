package de.do9fse.cwterminal.core.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.do9fse.cwterminal.core.model.KeyerEvent.HostOpenedEvent;
import de.do9fse.cwterminal.core.model.commands.HostOpenCommand;

/**
 * The keyer session holds the state of the keyer device and its version
 *
 * <p>
 * The session is assumed to be closed after creation.
 * <p>
 * */
public class KeyerSession {
    private static final Logger LOGGER = LoggerFactory.getLogger(KeyerSession.class);

    public enum SessionState {
        CLOSED,
        PENDING,
        OPEN
    } 

    private SessionState state;

    public KeyerSession() {
        this.state = SessionState.CLOSED;

        LOGGER.info("Session created");
    }

    public SessionState getSessionState() {
        return this.state;
    }

    public void handleCommand(final HostOpenCommand command) {
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
        this.state = SessionState.OPEN;
    }

    @Override
    public String toString() {
        return "KeyerSession [state=" + state + "]";
    }
}