package de.do9fse.cwterminal.core.model.commands.host;

import de.do9fse.cwterminal.core.model.WinKeyProtocolVersion;
import de.do9fse.cwterminal.core.model.commands.CommandConfiguration;

@CommandConfiguration(allowedProtocols = WinKeyProtocolVersion.V2)
public record MergeLettersCommand(char first, char second) implements HostModeCommand {
    public MergeLettersCommand {
        if (first > 0x7f || second > 0x7f) {
            throw new IllegalArgumentException("Merged letters must be ASCII characters");
        }
    }

    @Override
    public byte[] getPayloadBytes() {
        return new byte[] { 0x1b, (byte) first, (byte) second };
    }
}
