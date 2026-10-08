package de.do9fse.winkey.lib.core.model.commands.host;

import java.util.Objects;

import de.do9fse.winkey.lib.core.model.WinKeyProtocolVersion;
import de.do9fse.winkey.lib.core.model.commands.CommandConfiguration;
import de.do9fse.winkey.lib.core.model.configuration.PaddleSetpoint;

@CommandConfiguration(allowedProtocols = WinKeyProtocolVersion.V2)
public record SetPaddleSwitchPointCommand(PaddleSetpoint setpoint) implements HostModeCommand {
    public SetPaddleSwitchPointCommand {
        Objects.requireNonNull(setpoint, "Paddle switch point must not be null");
    }

    @Override
    public byte[] getPayloadBytes() {
        return new byte[] { 0x12, (byte) setpoint.toProtocolValue() };
    }
}
