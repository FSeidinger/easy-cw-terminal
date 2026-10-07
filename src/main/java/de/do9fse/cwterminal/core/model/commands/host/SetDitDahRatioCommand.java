package de.do9fse.cwterminal.core.model.commands.host;

import java.util.Objects;

import de.do9fse.cwterminal.core.model.WinKeyProtocolVersion;
import de.do9fse.cwterminal.core.model.commands.CommandConfiguration;
import de.do9fse.cwterminal.core.model.configuration.DitDahRatio;

@CommandConfiguration(allowedProtocols = WinKeyProtocolVersion.V2)
public record SetDitDahRatioCommand(DitDahRatio ratio) implements HostModeCommand {
    public SetDitDahRatioCommand {
        Objects.requireNonNull(ratio, "Dit/dah ratio must not be null");
    }

    @Override
    public byte[] getPayloadBytes() {
        return new byte[] { 0x17, (byte) ratio.toProtocolValue() };
    }
}
