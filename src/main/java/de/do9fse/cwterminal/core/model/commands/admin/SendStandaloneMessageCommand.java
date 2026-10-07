package de.do9fse.cwterminal.core.model.commands.admin;

import de.do9fse.cwterminal.core.model.WinKeyProtocolVersion;
import de.do9fse.cwterminal.core.model.commands.CommandConfiguration;

@CommandConfiguration(
    allowedProtocols = { WinKeyProtocolVersion.V1, WinKeyProtocolVersion.V2 }
)
public record SendStandaloneMessageCommand(int messageId) implements AdminCommand {
    public SendStandaloneMessageCommand {
        if (messageId < 0 || messageId > 6) {
            throw new IllegalArgumentException("Message ID must be between 0 and 6");
        }
    }

    @Override
    public byte[] getPayloadBytes() {
        return new byte[]{ 0x00, 14, (byte) messageId };
    }
}
