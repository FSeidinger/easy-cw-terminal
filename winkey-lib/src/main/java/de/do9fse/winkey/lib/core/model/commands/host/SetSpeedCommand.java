package de.do9fse.winkey.lib.core.model.commands.host;

import java.util.Objects;

import de.do9fse.winkey.lib.core.model.WinKeyProtocolVersion;
import de.do9fse.winkey.lib.core.model.commands.CommandConfiguration;
import de.do9fse.winkey.lib.core.model.configuration.WPMSpeedWithReset;

@CommandConfiguration(allowedProtocols = WinKeyProtocolVersion.V2)
public record SetSpeedCommand(WPMSpeedWithReset wpm) implements HostModeCommand {
    public SetSpeedCommand {
        Objects.requireNonNull(wpm, "WPM speed must not be null");
    }

    public static SetSpeedCommand useSpeedPot() {
        return new SetSpeedCommand(WPMSpeedWithReset.useSpeedPot());
    }

    @Override
    public byte[] getPayloadBytes() {
        return new byte[] { 0x02, (byte) wpm.toProtocolValue() };
    }
}
