package de.do9fse.cwterminal.core.model.commands.other;

import de.do9fse.cwterminal.core.model.WinKeyProtocolVersion;
import de.do9fse.cwterminal.core.model.commands.CommandConfiguration;
import de.do9fse.cwterminal.core.model.commands.WinKeyCommand;

@CommandConfiguration(
    allowedProtocols = { WinKeyProtocolVersion.V1, WinKeyProtocolVersion.V2, WinKeyProtocolVersion.V3 }
)
public record TextCommand(String text) implements WinKeyCommand {
    public TextCommand {
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException("Text must not be null or blank");
        }
    }

    @Override
    public byte[] getPayloadBytes() {
        return text.getBytes();
    }
}
