package de.do9fse.winkey.lib.core.model.commands.host;

import java.util.Objects;

import de.do9fse.winkey.lib.core.model.WinKeyProtocolVersion;
import de.do9fse.winkey.lib.core.model.commands.CommandConfiguration;
import de.do9fse.winkey.lib.core.model.configuration.Weighting;

@CommandConfiguration(allowedProtocols = WinKeyProtocolVersion.V2)
public record SetWeightingCommand(Weighting weighting) implements HostModeCommand {
    public SetWeightingCommand {
        Objects.requireNonNull(weighting, "Weighting must not be null");
    }

    @Override
    public byte[] getPayloadBytes() {
        return new byte[] { 0x03, (byte) weighting.toProtocolValue() };
    }
}
