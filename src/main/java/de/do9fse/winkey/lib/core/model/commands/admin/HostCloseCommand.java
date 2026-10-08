package de.do9fse.winkey.lib.core.model.commands.admin;

import de.do9fse.winkey.lib.core.model.WinKeyProtocolVersion;
import de.do9fse.winkey.lib.core.model.commands.CommandConfiguration;

@CommandConfiguration(
    allowedProtocols = { WinKeyProtocolVersion.V1, WinKeyProtocolVersion.V2 }
)
public record HostCloseCommand() implements AdminCommand {
    @Override
    public byte[] getPayloadBytes() {
        return new byte[] { 0x00, 3 };
    }
}
