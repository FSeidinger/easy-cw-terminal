package de.do9fse.winkey.lib.core.model.commands.test.suite5;

import de.do9fse.winkey.lib.core.model.WinKeyProtocolVersion;
import de.do9fse.winkey.lib.core.model.commands.CommandConfiguration;
import de.do9fse.winkey.lib.core.model.commands.admin.AdminCommand;

@CommandConfiguration(
    allowedProtocols = { WinKeyProtocolVersion.V1, WinKeyProtocolVersion.V2, WinKeyProtocolVersion.V3 }
)
public record AdminCommandWithoutResponse() implements AdminCommand {
    public byte[] getPayloadBytes() {
        return null;
    }
}
