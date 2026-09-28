package de.do9fse.cwterminal.core.model;

import de.do9fse.cwterminal.core.model.KeyerCommand.OpenHostCommand;

public class KeyerSession {
    private boolean isOpenPending;
    private boolean isOpen;

    public KeyerSession() {
        this.isOpenPending = false;
        this.isOpen = false;
    }

    public void handleCommand(final OpenHostCommand command) {
        if (isOpen) {
            throw new IllegalStateException("Host is already open");
        }

        if (isOpenPending) {
            throw new IllegalStateException("Open host command is already pending");
        }

        this.isOpenPending = true;
    }
}