package de.do9fse.winkey.lib.core.model;

import java.util.Objects;

public class ApplicationContext {
    private ApplicationStateMachine stateMachine;
    private WinKeyProtocolVersion version;

    public ApplicationContext(final WinKeyProtocolVersion version) {
        this.version = Objects.requireNonNull(version, "Version must not be null");
        this.stateMachine = new ApplicationStateMachine();
    }

    public void transitionToState(final WinKeyState newState) {
        this.stateMachine.transitionToState(newState);
    }

    public WinKeyState getState() {
        return stateMachine.getState();
    }

    public WinKeyProtocolVersion getVersion() {
        return version;
    }
}
