package de.do9fse.cwterminal.infrastructure.winkey.v2;

public abstract class WinKeyCommand {
    private final byte[] commandBytes;

    protected WinKeyCommand(final byte[] commandBytes) {
        this.commandBytes = commandBytes;
    }

    public byte[] getCommandBytes() {
        return commandBytes;
    }
}
