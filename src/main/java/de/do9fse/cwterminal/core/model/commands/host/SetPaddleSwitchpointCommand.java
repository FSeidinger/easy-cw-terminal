package de.do9fse.cwterminal.core.model.commands.host;

import java.util.Objects;

import de.do9fse.cwterminal.core.model.WinKeyProtocolVersion;
import de.do9fse.cwterminal.core.model.commands.CommandConfiguration;
import de.do9fse.cwterminal.core.model.configuration.PaddleSetpoint;

@CommandConfiguration(allowedProtocols = WinKeyProtocolVersion.V2)
public record SetPaddleSwitchpointCommand(PaddleSetpoint setpoint) implements HostModeCommand {
    public SetPaddleSwitchpointCommand {
        Objects.requireNonNull(setpoint, "Paddle switchpoint must not be null");
    }

    @Override
    public byte[] getPayloadBytes() {
        return new byte[] { 0x12, (byte) setpoint.percentage().getValue().intValue() };
    }
}
