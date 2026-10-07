package de.do9fse.cwterminal.core.model.commands.admin;

public record ReservedCommand() implements AdminCommand{
    @Override
    public byte[] getPayloadBytes() {
        return new byte[]{ 0x00, 8 };
    }
}
