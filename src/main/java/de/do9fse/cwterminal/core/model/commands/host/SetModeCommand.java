package de.do9fse.cwterminal.core.model.commands.host;

import java.util.Objects;

import de.do9fse.cwterminal.core.model.WinKeyProtocolVersion;
import de.do9fse.cwterminal.core.model.commands.CommandConfiguration;
import de.do9fse.cwterminal.core.model.configuration.ModeRegister;

@CommandConfiguration(allowedProtocols = WinKeyProtocolVersion.V2)
public record SetModeCommand(ModeRegister mode) implements HostModeCommand {
    public SetModeCommand {
        Objects.requireNonNull(mode, "Mode register must not be null");
        Objects.requireNonNull(mode.keyMode(), "Key mode must not be null");
    }

    @Override
    public byte[] getPayloadBytes() {
        return new byte[] { 0x0e, (byte) mode.toProtocolValue() };
    }
}
