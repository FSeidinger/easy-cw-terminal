package de.do9fse.winkey.lib.core.model.commands.host;

import java.util.Objects;

import de.do9fse.winkey.lib.core.model.WinKeyProtocolVersion;
import de.do9fse.winkey.lib.core.model.commands.CommandConfiguration;
import de.do9fse.winkey.lib.core.model.configuration.ModeRegister;

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
