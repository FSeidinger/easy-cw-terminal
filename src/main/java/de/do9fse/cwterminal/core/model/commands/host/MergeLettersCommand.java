package de.do9fse.cwterminal.core.model.commands.host;

import java.text.MessageFormat;

import de.do9fse.cwterminal.core.model.WinKeyProtocolVersion;
import de.do9fse.cwterminal.core.model.commands.CommandConfiguration;

@CommandConfiguration(allowedProtocols = WinKeyProtocolVersion.V2)
public record MergeLettersCommand(char first, char second) implements HostModeCommand {
    private static final char MAX_ASCII_CHARACTER = 0x7f;

    public MergeLettersCommand {
        if (first > MAX_ASCII_CHARACTER || second > MAX_ASCII_CHARACTER) {
            final String message = MessageFormat.format(
                "Merged letters must be ASCII characters (maximum code point {0})",
                (int) MAX_ASCII_CHARACTER
            );
            
            throw new IllegalArgumentException(message);
        }
    }

    @Override
    public byte[] getPayloadBytes() {
        return new byte[] { 0x1b, (byte) first, (byte) second };
    }
}
