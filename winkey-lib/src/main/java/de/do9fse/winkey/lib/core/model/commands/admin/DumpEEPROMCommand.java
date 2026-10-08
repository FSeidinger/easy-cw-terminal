package de.do9fse.winkey.lib.core.model.commands.admin;

import de.do9fse.winkey.lib.core.model.WinKeyProtocolVersion;
import de.do9fse.winkey.lib.core.model.commands.CommandConfiguration;
import de.do9fse.winkey.lib.core.model.responses.EEPROMDumpResponse;

@CommandConfiguration(
    allowedProtocols = WinKeyProtocolVersion.V2,
    responseType = EEPROMDumpResponse.class
)
public record  DumpEEPROMCommand() implements AdminCommand {
    @Override
    public byte[] getPayloadBytes() {
        return new byte[]{ 0x00, 12 };
    }
}
