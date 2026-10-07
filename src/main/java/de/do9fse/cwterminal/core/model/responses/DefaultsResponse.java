package de.do9fse.cwterminal.core.model.responses;

import de.do9fse.cwterminal.core.model.commands.host.SideToneControlCommand.SideToneFrequency;
import de.do9fse.cwterminal.core.model.configuration.DitDahRatio;
import de.do9fse.cwterminal.core.model.configuration.FarnsworthSpeed;
import de.do9fse.cwterminal.core.model.configuration.FirstExtensionDelay;
import de.do9fse.cwterminal.core.model.configuration.KeyCompensation;
import de.do9fse.cwterminal.core.model.configuration.LeadInDelay;
import de.do9fse.cwterminal.core.model.configuration.ModeRegister;
import de.do9fse.cwterminal.core.model.configuration.PaddleSetpoint;
import de.do9fse.cwterminal.core.model.configuration.PinConfiguration;
import de.do9fse.cwterminal.core.model.configuration.TailDelay;
import de.do9fse.cwterminal.core.model.configuration.WPMSpeed;
import de.do9fse.cwterminal.core.model.configuration.WPMSpeedRange;
import de.do9fse.cwterminal.core.model.configuration.Weighting;

@ResponseConfiguration(
    expectedResponseBytes = 15
)
public record DefaultsResponse(
    ModeRegister mode,
    WPMSpeed wpmSpeed,
    SideToneFrequency sideToneFrequency,
    Weighting weighting,
    LeadInDelay leadInDelay,
    TailDelay tailDelay,
    WPMSpeed wpmMinimum,
    WPMSpeedRange wpmRange,
    FirstExtensionDelay firstExtensionDelay,
    KeyCompensation keyCompensation,
    FarnsworthSpeed farnsworthSpeed,
    PaddleSetpoint paddleSetpoint,
    DitDahRatio ditDahRatio,
    PinConfiguration pinConfiguration,
    int reservedValue
) implements WinKeyResponse {
    public static DefaultsResponse parseResponse(final byte[] responseBytes) {
        return new DefaultsResponse(
            ModeRegister.parseResponseByte(responseBytes[0]),
            WPMSpeed.parseResponseByte(responseBytes[1]),
            SideToneFrequency.parseResponseByte(responseBytes[2]),
            Weighting.parseResponseByte(responseBytes[3]),
            LeadInDelay.parseResponseByte(responseBytes[4]),
            TailDelay.parseResponseByte(responseBytes[5]),
            WPMSpeed.parseResponseByte(responseBytes[6]),
            WPMSpeedRange.parseResponseByte(responseBytes[7]),
            FirstExtensionDelay.parseResponseByte(responseBytes[8]),
            KeyCompensation.parseResponseByte(responseBytes[9]),
            FarnsworthSpeed.parseResponseByte(responseBytes[10]),
            PaddleSetpoint.parseResponseByte(responseBytes[11]),
            DitDahRatio.parseResponseByte(responseBytes[12]),
            PinConfiguration.parseResponseByte(responseBytes[13]),
            responseBytes[14] & 0xff
        );
    }
}
