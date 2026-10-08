package de.do9fse.winkey.lib.core.model.commands.host;

import java.util.Objects;

import de.do9fse.winkey.lib.core.model.WinKeyProtocolVersion;
import de.do9fse.winkey.lib.core.model.commands.CommandConfiguration;
import de.do9fse.winkey.lib.core.model.configuration.FarnsworthSpeed;

@CommandConfiguration(allowedProtocols = WinKeyProtocolVersion.V2)
public record SetFarnsworthSpeedCommand(FarnsworthSpeed speed) implements HostModeCommand {
    public SetFarnsworthSpeedCommand {
        Objects.requireNonNull(speed, "Farnsworth speed must not be null");
    }

    @Override
    public byte[] getPayloadBytes() {
        return new byte[] { 0x0d, (byte) speed.toProtocolValue() };
    }
}
