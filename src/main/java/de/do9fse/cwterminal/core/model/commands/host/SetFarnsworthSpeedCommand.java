package de.do9fse.cwterminal.core.model.commands.host;

import java.util.Objects;

import de.do9fse.cwterminal.core.model.WinKeyProtocolVersion;
import de.do9fse.cwterminal.core.model.commands.CommandConfiguration;
import de.do9fse.cwterminal.core.model.configuration.FarnsworthSpeed;

@CommandConfiguration(allowedProtocols = WinKeyProtocolVersion.V2)
public record SetFarnsworthSpeedCommand(FarnsworthSpeed speed) implements HostModeCommand {
    public SetFarnsworthSpeedCommand {
        Objects.requireNonNull(speed, "Farnsworth speed must not be null");
    }

    @Override
    public byte[] getPayloadBytes() {
        return new byte[] { 0x0d, (byte) speed.wpmSpeed().getValue().intValue() };
    }
}
