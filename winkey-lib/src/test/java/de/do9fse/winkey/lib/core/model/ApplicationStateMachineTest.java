package de.do9fse.winkey.lib.core.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class ApplicationStateMachineTest {
    @Test
    void startsClosed() {
        final ApplicationStateMachine stateMachine = new ApplicationStateMachine();

        assertEquals(WinKeyState.CLOSED, stateMachine.getState());
    }

    @Test
    void supportsNormalInitializationAndCloseTransitions() {
        final ApplicationStateMachine stateMachine = new ApplicationStateMachine();

        stateMachine.transitionToState(WinKeyState.INITIALIZING);
        assertEquals(WinKeyState.INITIALIZING, stateMachine.getState());

        stateMachine.transitionToState(WinKeyState.READY);
        assertEquals(WinKeyState.READY, stateMachine.getState());

        stateMachine.transitionToState(WinKeyState.CLOSED);
        assertEquals(WinKeyState.CLOSED, stateMachine.getState());
    }

    @Test
    void allowsClosingWhenInitializationDoesNotComplete() {
        final ApplicationStateMachine stateMachine = new ApplicationStateMachine();
        stateMachine.transitionToState(WinKeyState.INITIALIZING);

        stateMachine.transitionToState(WinKeyState.CLOSED);

        assertEquals(WinKeyState.CLOSED, stateMachine.getState());
    }

    @Test
    void rejectsTransitionsNotAllowedFromClosed() {
        final ApplicationStateMachine stateMachine = new ApplicationStateMachine();

        assertThrows(IllegalStateException.class, () -> stateMachine.transitionToState(WinKeyState.CLOSED));
        assertThrows(IllegalStateException.class, () -> stateMachine.transitionToState(WinKeyState.READY));
        assertEquals(WinKeyState.CLOSED, stateMachine.getState());
    }

    @Test
    void rejectsTransitionsNotAllowedFromInitializing() {
        final ApplicationStateMachine stateMachine = new ApplicationStateMachine();
        stateMachine.transitionToState(WinKeyState.INITIALIZING);

        assertThrows(IllegalStateException.class, () -> stateMachine.transitionToState(WinKeyState.INITIALIZING));
        assertEquals(WinKeyState.INITIALIZING, stateMachine.getState());
    }

    @Test
    void rejectsTransitionsNotAllowedFromReady() {
        final ApplicationStateMachine stateMachine = new ApplicationStateMachine();
        stateMachine.transitionToState(WinKeyState.INITIALIZING);
        stateMachine.transitionToState(WinKeyState.READY);

        assertThrows(IllegalStateException.class, () -> stateMachine.transitionToState(WinKeyState.READY));
        assertThrows(IllegalStateException.class, () -> stateMachine.transitionToState(WinKeyState.INITIALIZING));
        assertEquals(WinKeyState.READY, stateMachine.getState());
    }
}
