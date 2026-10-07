package de.do9fse.cwterminal.core.model.commands.host;

import de.do9fse.cwterminal.core.model.WinKeyProtocolVersion;
import de.do9fse.cwterminal.core.model.commands.CommandConfiguration;

@CommandConfiguration(allowedProtocols = WinKeyProtocolVersion.V2)
public record KeyImmediateCommand(boolean keyDown) implements HostModeCommand {
    @Override
    public byte[] getPayloadBytes() {
        return new byte[] { 0x0b, (byte) (keyDown ? 1 : 0) };
    }
}
