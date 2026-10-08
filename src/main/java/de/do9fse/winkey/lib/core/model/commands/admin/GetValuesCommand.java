package de.do9fse.winkey.lib.core.model.commands.admin;

import de.do9fse.winkey.lib.core.model.WinKeyProtocolVersion;
import de.do9fse.winkey.lib.core.model.commands.CommandConfiguration;
import de.do9fse.winkey.lib.core.model.responses.DefaultsResponse;

@CommandConfiguration(
    allowedProtocols = WinKeyProtocolVersion.V2,
    responseType = DefaultsResponse.class
)
public record GetValuesCommand() implements AdminCommand {
    @Override
    public byte[] getPayloadBytes() {
        return new byte[]{ 0x00, 7 };
    }
}
