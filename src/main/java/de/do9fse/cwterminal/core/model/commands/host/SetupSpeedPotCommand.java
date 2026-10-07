package de.do9fse.cwterminal.core.model.commands.host;

import java.util.Objects;

import de.do9fse.cwterminal.core.model.WinKeyProtocolVersion;
import de.do9fse.cwterminal.core.model.commands.CommandConfiguration;
import de.do9fse.cwterminal.core.model.configuration.WPMSpeed;
import de.do9fse.cwterminal.core.model.configuration.WPMSpeedRange;

@CommandConfiguration(allowedProtocols = WinKeyProtocolVersion.V2)
public record SetupSpeedPotCommand(WPMSpeed minWpm, WPMSpeedRange wpmRange, int unused) implements HostModeCommand {
    public SetupSpeedPotCommand {
        Objects.requireNonNull(minWpm, "Minimum WPM must not be null");
        Objects.requireNonNull(wpmRange, "WPM range must not be null");
        HostCommandSupport.requireRange("Unused speed pot byte", unused, 0, 255);

        if (minWpm.wpmSpeed().getValue().intValue() + wpmRange.range().getValue().intValue() > 99) {
            throw new IllegalArgumentException("Maximum speed pot WPM must not exceed 99");
        }
    }

    @Override
    public byte[] getPayloadBytes() {
        return new byte[] {
            0x05,
            (byte) minWpm.wpmSpeed().getValue().intValue(),
            (byte) wpmRange.range().getValue().intValue(),
            (byte) unused
        };
    }
}
