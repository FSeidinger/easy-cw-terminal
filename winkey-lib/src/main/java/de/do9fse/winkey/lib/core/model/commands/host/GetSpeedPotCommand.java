package de.do9fse.winkey.lib.core.model.commands.host;

import de.do9fse.winkey.lib.core.model.commands.CommandConfiguration;
import de.do9fse.winkey.lib.core.model.responses.SpeedPotValueResponse;

@CommandConfiguration(
    allowedProtocols = de.do9fse.winkey.lib.core.model.WinKeyProtocolVersion.V2,
    responseType = SpeedPotValueResponse.class
)
public record GetSpeedPotCommand() implements HostModeCommand {
    @Override
    public byte[] getPayloadBytes() {
        return new byte[] { 0x07 };
    }
}
