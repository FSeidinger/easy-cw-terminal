package de.do9fse.cwterminal.core.model.commands.host;

import static de.do9fse.cwterminal.testtools.CommandTestTools.assertPayload;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static javax.measure.MetricPrefix.MILLI;

import org.junit.jupiter.api.Test;

import tech.units.indriya.quantity.Quantities;
import tech.units.indriya.unit.Units;

import de.do9fse.cwterminal.core.model.WinKeyProtocolVersion;
import de.do9fse.cwterminal.core.model.commands.WinKeyCommand;
import de.do9fse.cwterminal.core.model.configuration.DitDahRatio;
import de.do9fse.cwterminal.core.model.configuration.FarnsworthSpeed;
import de.do9fse.cwterminal.core.model.configuration.FirstExtensionDelay;
import de.do9fse.cwterminal.core.model.configuration.KeyCompensation;
import de.do9fse.cwterminal.core.model.configuration.KeyMode;
import de.do9fse.cwterminal.core.model.configuration.ModeRegister;
import de.do9fse.cwterminal.core.model.configuration.PaddleSetpoint;
import de.do9fse.cwterminal.core.model.configuration.PinConfiguration;
import de.do9fse.cwterminal.core.model.configuration.Weighting;
import de.do9fse.cwterminal.core.model.configuration.WPMSpeed;
import de.do9fse.cwterminal.core.model.configuration.WPMSpeedRange;
import de.do9fse.cwterminal.core.model.configuration.WinKeyUnits;
import de.do9fse.cwterminal.core.model.responses.SpeedPotValueResponse;
import de.do9fse.cwterminal.core.model.responses.WinKeyStatusResponse;

class HostModeCommandTest {
    @Test
    void sideToneControlEncodesEveryFrequencyAndPaddleOnlyFlag() {
        final SideToneControlCommand.SideToneFrequency[] frequencies =
            SideToneControlCommand.SideToneFrequency.values();

        for (int code = 1; code <= frequencies.length; code++) {
            assertTrue(frequencies[code - 1].frequency().getValue().doubleValue() > 0);
            assertPayload(
                new SideToneControlCommand(false, frequencies[code - 1]),
                new byte[] { 0x01, (byte) code }
            );
            assertPayload(
                new SideToneControlCommand(true, frequencies[code - 1]),
                new byte[] { 0x01, (byte) (0x80 | code) }
            );
        }
    }

    @Test
    void sideToneFrequencyEncodesEveryFrequency() {
        final SideToneFrequencyCommand.SideToneFrequency[] frequencies =
            SideToneFrequencyCommand.SideToneFrequency.values();

        for (int code = 1; code <= frequencies.length; code++) {
            assertTrue(frequencies[code - 1].frequency().getValue().doubleValue() > 0);
            assertPayload(
                new SideToneFrequencyCommand(frequencies[code - 1]),
                new byte[] { 0x01, (byte) code }
            );
        }
    }

    @Test
    void encodesImmediateHostCommands() {
        assertPayload(SetSpeedCommand.useSpeedPot(), new byte[] { 0x02, 0x00 });
        assertPayload(new SetSpeedCommand(Quantities.getQuantity(99, WinKeyUnits.WPM)), new byte[] { 0x02, 0x63 });
        assertPayload(
            new SetWeightingCommand(Weighting.parseResponseByte(50)),
            new byte[] { 0x03, 0x32 }
        );
        assertPayload(
            new SetPttLeadTailCommand(
                Quantities.getQuantity(10, MILLI(Units.SECOND)),
                Quantities.getQuantity(2500, MILLI(Units.SECOND))
            ),
            new byte[] { 0x04, 0x01, (byte) 0xfa }
        );
        assertPayload(
            new SetupSpeedPotCommand(WPMSpeed.parseResponseByte(10), WPMSpeedRange.parseResponseByte(15), 0),
            new byte[] { 0x05, 0x0a, 0x0f, 0x00 }
        );
        assertPayload(new PauseCommand(true), new byte[] { 0x06, 0x01 });
        assertPayload(new GetSpeedPotCommand(), new byte[] { 0x07 });
        assertPayload(new BackspaceCommand(), new byte[] { 0x08 });
        assertPayload(
            new SetPinConfigurationCommand(new PinConfiguration(
                true,
                true,
                true,
                true,
                PinConfiguration.UltimaticPriority.DIT,
                PinConfiguration.PaddleHangTime.ONE_AND_TWO_THIRDS_LETTERSPACES
            )),
            new byte[] { 0x09, (byte) 0xaf }
        );
        assertPayload(new ClearBufferCommand(), new byte[] { 0x0a });
        assertPayload(new KeyImmediateCommand(true), new byte[] { 0x0b, 0x01 });
        assertPayload(
            new SetHscwSpeedCommand(Quantities.getQuantity(2000, WinKeyUnits.LPM)),
            new byte[] { 0x0c, 0x14 }
        );
        assertPayload(
            new SetFarnsworthSpeedCommand(FarnsworthSpeed.parseResponseByte(18)),
            new byte[] { 0x0d, 0x12 }
        );
        assertPayload(
            new SetModeCommand(new ModeRegister(true, true, KeyMode.BUG_MODE, true, true, true, true)),
            new byte[] { 0x0e, 0x7f }
        );

        final byte[] defaults = new byte[15];
        defaults[0] = 0x31;
        defaults[14] = (byte) 0xff;
        final byte[] loadPayload = new byte[16];
        loadPayload[0] = 0x0f;
        System.arraycopy(defaults, 0, loadPayload, 1, defaults.length);
        assertPayload(new LoadDefaultsCommand(defaults), loadPayload);

        assertPayload(
            new SetFirstExtensionCommand(FirstExtensionDelay.parseResponseByte(80)),
            new byte[] { 0x10, 0x50 }
        );
        assertPayload(
            new SetKeyCompensationCommand(KeyCompensation.parseResponseByte(180)),
            new byte[] { 0x11, (byte) 0xb4 }
        );
        assertPayload(
            new SetPaddleSwitchpointCommand(PaddleSetpoint.parseResponseByte(55)),
            new byte[] { 0x12, 0x37 }
        );
        assertPayload(new NullCommand(), new byte[] { 0x13 });
        assertPayload(
            new SoftwarePaddleCommand(SoftwarePaddleCommand.PaddleSelection.BOTH),
            new byte[] { 0x14, 0x03 }
        );
        assertPayload(new RequestWinKeyStatusCommand(), new byte[] { 0x15 });
        assertPayload(SetBufferPointerCommand.reset(), new byte[] { 0x16, 0x00 });
        assertPayload(
            new SetBufferPointerCommand(SetBufferPointerCommand.Operation.OVERWRITE, 8),
            new byte[] { 0x16, 0x01, 0x08 }
        );
        assertPayload(
            new SetBufferPointerCommand(SetBufferPointerCommand.Operation.INSERT, 9),
            new byte[] { 0x16, 0x02, 0x09 }
        );
        assertPayload(
            new SetBufferPointerCommand(SetBufferPointerCommand.Operation.ADD_NULLS, 10),
            new byte[] { 0x16, 0x03, 0x0a }
        );
        assertPayload(
            new SetDitDahRatioCommand(DitDahRatio.parseResponseByte(50)),
            new byte[] { 0x17, 0x32 }
        );
    }

    @Test
    void encodesBufferedHostCommands() {
        assertPayload(new BufferedPttCommand(true), new byte[] { 0x18, 0x01 });
        assertPayload(
            new TimedKeyDownCommand(Quantities.getQuantity(99, Units.SECOND)),
            new byte[] { 0x19, 0x63 }
        );
        assertPayload(
            new BufferedWaitCommand(Quantities.getQuantity(15, Units.SECOND)),
            new byte[] { 0x1a, 0x0f }
        );
        assertPayload(new MergeLettersCommand('R', 'R'), new byte[] { 0x1b, 'R', 'R' });
        assertPayload(
            new BufferedSpeedChangeCommand(Quantities.getQuantity(25, WinKeyUnits.WPM)),
            new byte[] { 0x1c, 0x19 }
        );
        assertPayload(
            new BufferedHscwSpeedCommand(Quantities.getQuantity(4000, WinKeyUnits.LPM)),
            new byte[] { 0x1d, 0x28 }
        );
        assertPayload(new CancelBufferedSpeedCommand(), new byte[] { 0x1e });
        assertPayload(new BufferedNopCommand(), new byte[] { 0x1f });
    }

    @Test
    void hostCommandsDeclareProtocolAndResponseMetadata() {
        final WinKeyCommand speedPotCommand = new GetSpeedPotCommand();
        assertTrue(speedPotCommand.getCommandInfo().supportsProtocol(WinKeyProtocolVersion.V2));
        assertEquals(SpeedPotValueResponse.class, speedPotCommand.getCommandInfo().responseType());

        final WinKeyCommand statusCommand = new RequestWinKeyStatusCommand();
        assertTrue(statusCommand.getCommandInfo().supportsProtocol(WinKeyProtocolVersion.V2));
        assertEquals(WinKeyStatusResponse.class, statusCommand.getCommandInfo().responseType());
        assertFalse(statusCommand.getCommandInfo().supportsProtocol(WinKeyProtocolVersion.V1));
        assertEquals(
            32,
            WinKeyCommand.validateCommands("de.do9fse.cwterminal.core.model.commands.host").size()
        );
    }

    @Test
    void validatesProtocolRangesAndCopiesLoadDefaults() {
        assertThrows(
            IllegalArgumentException.class,
            () -> new SetSpeedCommand(Quantities.getQuantity(4, WinKeyUnits.WPM))
        );
        assertThrows(
            IllegalArgumentException.class,
            () -> new SetSpeedCommand(Quantities.getQuantity(100, WinKeyUnits.WPM))
        );
        assertThrows(
            IllegalArgumentException.class,
            () -> new SetPttLeadTailCommand(
                Quantities.getQuantity(2510, MILLI(Units.SECOND)),
                Quantities.getQuantity(0, Units.SECOND)
            )
        );
        assertThrows(
            IllegalArgumentException.class,
            () -> new SetupSpeedPotCommand(
                WPMSpeed.parseResponseByte(90),
                WPMSpeedRange.parseResponseByte(10),
                0
            )
        );
        assertThrows(
            IllegalArgumentException.class,
            () -> new SetHscwSpeedCommand(Quantities.getQuantity(900, WinKeyUnits.LPM))
        );
        assertThrows(
            IllegalArgumentException.class,
            () -> new TimedKeyDownCommand(Quantities.getQuantity(100, Units.SECOND))
        );
        assertThrows(IllegalArgumentException.class, () -> new MergeLettersCommand('A', '\u0080'));
        assertThrows(IllegalArgumentException.class, () -> new LoadDefaultsCommand(new byte[14]));

        final byte[] values = new byte[15];
        final LoadDefaultsCommand command = new LoadDefaultsCommand(values);
        values[0] = 0x01;
        assertArrayEquals(new byte[15], command.values());

        final byte[] returnedValues = command.values();
        returnedValues[0] = 0x02;
        assertArrayEquals(new byte[15], command.values());
    }

}
