package de.do9fse.cwterminal.core.model.commands.host;

import java.util.Objects;

import de.do9fse.cwterminal.core.model.WinKeyProtocolVersion;
import de.do9fse.cwterminal.core.model.commands.CommandConfiguration;
import de.do9fse.cwterminal.core.model.configuration.PinConfiguration;

@CommandConfiguration(allowedProtocols = WinKeyProtocolVersion.V2)
public record SetPinConfigurationCommand(PinConfiguration configuration) implements HostModeCommand {
    public SetPinConfigurationCommand {
        Objects.requireNonNull(configuration, "Pin configuration must not be null");
        Objects.requireNonNull(configuration.ultimaticPriority(), "Ultimatic priority must not be null");
        Objects.requireNonNull(configuration.paddleHangTime(), "Paddle hang time must not be null");
    }

    @Override
    public byte[] getPayloadBytes() {
        final int value =
            (configuration.ultimaticPriority().ordinal() << 6)
                | (configuration.paddleHangTime().ordinal() << 4)
                | (configuration.isKeyOutput1Enabled() ? 0x08 : 0)
                | (configuration.isKeyOutput2Enabled() ? 0x04 : 0)
                | (configuration.isSidetoneEnabled() ? 0x02 : 0)
                | (configuration.isPttEnabled() ? 0x01 : 0);
        return new byte[] { 0x09, (byte) value };
    }
}
