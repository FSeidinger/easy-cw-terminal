package de.do9fse.cwterminal.core.model.commands.admin;

public record CalibrateCommand() implements AdminCommand {
    @Override
    public byte[] getPayloadBytes() {
        return new byte[]{ 0x00, 0 };
    }
}
