package de.do9fse.winkey.lib.core.model;

import java.util.Objects;

import de.do9fse.winkey.lib.core.port.out.WinKeyJobQueue;
import de.do9fse.winkey.lib.core.port.out.WinKeyTransport;
import de.do9fse.winkey.lib.infrastructure.winkey.DefaultWinKeyJobQueue;

public class ApplicationContext {
    private final WinKeyJobQueue jobQueue;
    private final ApplicationStateMachine stateMachine;

    private WinKeyProtocolVersion version;
    private String portName;
    private WinKeyTransport transport;

    public ApplicationContext(final WinKeyProtocolVersion version) {
        this.version = Objects.requireNonNull(version, "Version must not be null");
        this.stateMachine = new ApplicationStateMachine();
        this.jobQueue = new DefaultWinKeyJobQueue();
    }

    public void transitionToState(final WinKeyState newState) {
        this.stateMachine.transitionToState(newState);
    }

    public WinKeyState getState() {
        return stateMachine.getState();
    }

    
    public WinKeyJobQueue getJobQueue() {
        return jobQueue;
    }

    public void setVersion(final WinKeyProtocolVersion version) {
        this.version = Objects.requireNonNull(version, "Version must not be null");
    }

    public WinKeyProtocolVersion getVersion() {
        return version;
    }
   
    public String getPortName() {
        return portName;
    }

    public void setPortName(String portName) {
        this.portName = portName;
    }

    public WinKeyTransport getTransport() {
        return transport;
    }

    public void setTransport(WinKeyTransport transport) {
        this.transport = transport;
    }
}
