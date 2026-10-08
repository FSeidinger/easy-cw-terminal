package de.do9fse.winkey.lib.core.model.commands.host;

import de.do9fse.winkey.lib.core.model.commands.CommandConfiguration;
import de.do9fse.winkey.lib.core.model.responses.WinKeyStatusResponse;

@CommandConfiguration(
    allowedProtocols = de.do9fse.winkey.lib.core.model.WinKeyProtocolVersion.V2,
    responseType = WinKeyStatusResponse.class
)
public record RequestWinKeyStatusCommand() implements HostModeCommand {
    @Override
    public byte[] getPayloadBytes() {
        return new byte[] { 0x15 };
    }
}
