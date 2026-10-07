package de.do9fse.cwterminal.core.model.commands.admin;

public record ResetCommand() implements AdminCommand {
    @Override
    public byte[] getPayloadBytes() {
        return new byte[]{ 0x00, 1 };
    }
}
