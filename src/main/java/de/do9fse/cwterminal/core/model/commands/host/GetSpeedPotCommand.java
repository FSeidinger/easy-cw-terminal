package de.do9fse.cwterminal.core.model.commands.host;

import de.do9fse.cwterminal.core.model.commands.CommandConfiguration;
import de.do9fse.cwterminal.core.model.responses.SpeedPotValueResponse;

@CommandConfiguration(
    allowedProtocols = de.do9fse.cwterminal.core.model.WinKeyProtocolVersion.V2,
    responseType = SpeedPotValueResponse.class
)
public record GetSpeedPotCommand() implements HostModeCommand {
    @Override
    public byte[] getPayloadBytes() {
        return new byte[] { 0x07 };
    }
}
