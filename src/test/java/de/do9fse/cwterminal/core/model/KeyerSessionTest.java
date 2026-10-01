package de.do9fse.cwterminal.core.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import de.do9fse.cwterminal.core.model.KeyerEvent.HostOpenedEvent;
import de.do9fse.cwterminal.core.model.KeyerSession.SessionState;
import de.do9fse.cwterminal.core.model.commands.HostOpenCommand;

@DisplayName("Keyer session tests")
class KeyerSessionTest {
    private KeyerSession session;

    @Test
    @DisplayName("Tests that a keyer session can be created")
    void canCreate() {
        givenTheSessionIsCreated();
        thenSessionIsClosed();
    }

    @Test
    @DisplayName("Tests that session can handle open host command")
    void canHandleOpenHostCommand() {
        givenTheSessionIsCreated();
        whenSendingOpenHostCommand();
        thenSessionIsPending();
    }

    @Test
    @DisplayName("Tests that session rejects open host command if pending")
    void rejectsOpenHostCommandIfPending() {
        givenTheSessionIsPending();
        final Exception exception = assertThrows(IllegalStateException.class, () -> whenSendingOpenHostCommand());
        assertEquals("Session already started", exception.getMessage());
    }

    @Test
    @DisplayName("Tests that session rejects open host command if open")
    void rejectsOpenHostCommandIfOpen() {
        givenTheSessionIsOpen();
        final Exception exception = assertThrows(IllegalStateException.class, () -> whenSendingOpenHostCommand());
        assertEquals("Session already started", exception.getMessage());
    }
    
    void givenTheSessionIsCreated() {
        this.session = new KeyerSession();
    }

    void givenTheSessionIsPending() {
        this.session = new KeyerSession();
        final HostOpenCommand command = new HostOpenCommand();
        session.handleCommand(command);      
    }

    void givenTheSessionIsOpen() {
        this.session = new KeyerSession();
        final HostOpenCommand command = new HostOpenCommand();
        session.handleCommand(command);      

        final KeyerVersion version = new KeyerVersion(2, 1);
        final HostOpenedEvent event = new HostOpenedEvent(version);
        session.on(event);
    }

    void whenSendingOpenHostCommand() {
        final HostOpenCommand command = new HostOpenCommand();
        session.handleCommand(command);      
    }

    void thenSessionIsClosed() {
        assertEquals(SessionState.CLOSED, session.getSessionState());
    }

    void thenSessionIsPending() {
        assertEquals(SessionState.PENDING, session.getSessionState());
    }
}