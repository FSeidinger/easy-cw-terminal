package de.do9fse.cwterminal.core.model.commands.test.suite5;

import de.do9fse.cwterminal.core.model.WinKeyProtocolVersion;
import de.do9fse.cwterminal.core.model.commands.CommandConfiguration;
import de.do9fse.cwterminal.core.model.commands.admin.AdminCommand;
import de.do9fse.cwterminal.core.model.responses.WinKeyVersionResponse;

@CommandConfiguration(
    allowedProtocols = { WinKeyProtocolVersion.V1, WinKeyProtocolVersion.V2, WinKeyProtocolVersion.V3},
    responseType = WinKeyVersionResponse.class
)
public record AdminCommandWithResponse() implements AdminCommand {
    @Override
    public byte[] getPayloadBytes() {
        return null;
    }
}
