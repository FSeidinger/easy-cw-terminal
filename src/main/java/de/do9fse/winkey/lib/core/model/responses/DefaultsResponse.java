package de.do9fse.winkey.lib.core.model.responses;

import de.do9fse.winkey.lib.core.model.commands.host.SideToneControlCommand.SideToneFrequency;
import de.do9fse.winkey.lib.core.model.configuration.DitDahRatio;
import de.do9fse.winkey.lib.core.model.configuration.FarnsworthSpeed;
import de.do9fse.winkey.lib.core.model.configuration.FirstExtensionDelay;
import de.do9fse.winkey.lib.core.model.configuration.KeyCompensation;
import de.do9fse.winkey.lib.core.model.configuration.LeadInDelay;
import de.do9fse.winkey.lib.core.model.configuration.ModeRegister;
import de.do9fse.winkey.lib.core.model.configuration.PaddleSetpoint;
import de.do9fse.winkey.lib.core.model.configuration.PinConfiguration;
import de.do9fse.winkey.lib.core.model.configuration.TailDelay;
import de.do9fse.winkey.lib.core.model.configuration.WPMSpeed;
import de.do9fse.winkey.lib.core.model.configuration.WPMSpeedRange;
import de.do9fse.winkey.lib.core.model.configuration.Weighting;

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
    public static DefaultsResponse fromProtocol(final byte[] responseBytes) {
        return new DefaultsResponse(
            ModeRegister.fromProtocol(responseBytes[0]),
            WPMSpeed.fromProtocol(responseBytes[1]),
            SideToneFrequency.parseResponseByte(responseBytes[2]),
            Weighting.fromProtocol(responseBytes[3]),
            LeadInDelay.fromProtocol(responseBytes[4]),
            TailDelay.fromProtocol(responseBytes[5]),
            WPMSpeed.fromProtocol(responseBytes[6]),
            WPMSpeedRange.fromProtocol(responseBytes[7]),
            FirstExtensionDelay.fromProtocol(responseBytes[8]),
            KeyCompensation.fromProtocol(responseBytes[9]),
            FarnsworthSpeed.fromProtocol(responseBytes[10]),
            PaddleSetpoint.fromProtocol(responseBytes[11]),
            DitDahRatio.fromProtocol(responseBytes[12]),
            PinConfiguration.fromProtocol(responseBytes[13]),
            responseBytes[14] & 0xff
        );
    }
}
