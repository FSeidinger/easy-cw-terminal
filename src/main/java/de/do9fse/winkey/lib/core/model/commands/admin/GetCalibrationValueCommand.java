package de.do9fse.winkey.lib.core.model.commands.admin;

import de.do9fse.winkey.lib.core.model.WinKeyProtocolVersion;
import de.do9fse.winkey.lib.core.model.commands.CommandConfiguration;
import de.do9fse.winkey.lib.core.model.responses.CalibrationValueResponse;

@CommandConfiguration(
    allowedProtocols = { WinKeyProtocolVersion.V1, WinKeyProtocolVersion.V2 },
    responseType = CalibrationValueResponse.class
)
public record GetCalibrationValueCommand() implements AdminCommand {
    @Override
    public byte[] getPayloadBytes() {
        return new byte[]{ 0x00, 9 };
    }
}
