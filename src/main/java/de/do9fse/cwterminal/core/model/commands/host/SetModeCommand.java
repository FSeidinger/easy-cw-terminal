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
        final int value =
            (mode.isPaddleWatchdogEnabled() ? 0 : 0x80)
                | (mode.isPaddleEchobackEnabled() ? 0x40 : 0)
                | (mode.keyMode().ordinal() << 4)
                | (mode.isPaddleSwapped() ? 0x08 : 0)
                | (mode.isSerialEchobackEnabled() ? 0x04 : 0)
                | (mode.isAutospaceEnabled() ? 0x02 : 0)
                | (mode.isCTSpacingEnabled() ? 0x01 : 0);
        return new byte[] { 0x0e, (byte) value };
    }
}
