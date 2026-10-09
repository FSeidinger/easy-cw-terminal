package de.do9fse.winkey.lib.core.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class ApplicationContextTest {
    @Test
    void retainsConfiguredProtocolVersionAndStartsClosed() {
        final ApplicationContext context = new ApplicationContext(WinKeyProtocolVersion.V2);

        assertEquals(WinKeyProtocolVersion.V2, context.getVersion());
        assertEquals(WinKeyState.CLOSED, context.getState());
    }

    @Test
    void delegatesStateTransitionsToItsStateMachine() {
        final ApplicationContext context = new ApplicationContext(WinKeyProtocolVersion.V1);

        context.transitionToState(WinKeyState.INITIALIZING);
        context.transitionToState(WinKeyState.READY);

        assertEquals(WinKeyState.READY, context.getState());
    }

    @Test
    void rejectsANullProtocolVersion() {
        assertThrows(NullPointerException.class, () -> new ApplicationContext(null));
    }
}
