package de.do9fse.winkey.lib.core.model.commands.host;

import java.util.Objects;

import de.do9fse.winkey.lib.core.model.WinKeyProtocolVersion;
import de.do9fse.winkey.lib.core.model.commands.CommandConfiguration;

@CommandConfiguration(allowedProtocols = WinKeyProtocolVersion.V2)
public record SoftwarePaddleCommand(PaddleSelection selection) implements HostModeCommand {
    public enum PaddleSelection {
        UP,
        DIT,
        DAH,
        BOTH
    }

    public SoftwarePaddleCommand {
        Objects.requireNonNull(selection, "Paddle selection must not be null");
    }

    @Override
    public byte[] getPayloadBytes() {
        return new byte[] { 0x14, (byte) selection.ordinal() };
    }
}
