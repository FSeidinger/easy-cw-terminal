package de.do9fse.cwterminal.core.model.commands.host;

import java.util.Objects;

import de.do9fse.cwterminal.core.model.WinKeyProtocolVersion;
import de.do9fse.cwterminal.core.model.commands.CommandConfiguration;
import de.do9fse.cwterminal.core.model.configuration.KeyCompensation;

@CommandConfiguration(allowedProtocols = WinKeyProtocolVersion.V2)
public record SetKeyCompensationCommand(KeyCompensation compensation) implements HostModeCommand {
    public SetKeyCompensationCommand {
        Objects.requireNonNull(compensation, "Key compensation must not be null");
    }

    @Override
    public byte[] getPayloadBytes() {
        return new byte[] { 0x11, (byte) compensation.duration().getValue().intValue() };
    }
}
