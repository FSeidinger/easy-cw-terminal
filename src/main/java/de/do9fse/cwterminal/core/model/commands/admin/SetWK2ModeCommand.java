package de.do9fse.cwterminal.core.model.commands.admin;

public record SetWK2ModeCommand() implements AdminCommand {
    @Override
    public byte[] getPayloadBytes() {
        return new byte[]{ 0x00, 11 };
    }
}
