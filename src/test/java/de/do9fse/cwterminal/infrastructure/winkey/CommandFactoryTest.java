package de.do9fse.cwterminal.infrastructure.winkey;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import de.do9fse.cwterminal.core.model.KeyerVersion;
import de.do9fse.cwterminal.core.model.commands.CalibrateCommand;
import de.do9fse.cwterminal.core.model.commands.DumpEEPROMCommand;
import de.do9fse.cwterminal.core.model.commands.EchoTestCommand;
import de.do9fse.cwterminal.core.model.commands.GetCalibrationValueCommand;
import de.do9fse.cwterminal.core.model.commands.GetValuesCommand;
import de.do9fse.cwterminal.core.model.commands.HostCloseCommand;
import de.do9fse.cwterminal.core.model.commands.HostOpenCommand;
import de.do9fse.cwterminal.core.model.commands.KeyerCommand;
import de.do9fse.cwterminal.core.model.commands.LoadEEPROMCommand;
import de.do9fse.cwterminal.core.model.commands.ReadPaddleADCommand;
import de.do9fse.cwterminal.core.model.commands.ReadSpeedA2DCommand;
import de.do9fse.cwterminal.core.model.commands.ReservedCommand;
import de.do9fse.cwterminal.core.model.commands.ResetCommand;
import de.do9fse.cwterminal.core.model.commands.SendStandaloneMessageCommand;
import de.do9fse.cwterminal.core.model.commands.SetWK1ModeCommand;
import de.do9fse.cwterminal.core.model.commands.SetWK2ModeCommand;
import de.do9fse.cwterminal.core.model.commands.SideToneControlCommand;
import de.do9fse.cwterminal.core.model.commands.SideToneFrequencyCommand;
import de.do9fse.cwterminal.core.model.commands.SideToneControlCommand.SideToneFrequency;


@DisplayName("Command factory tests") 
public class CommandFactoryTest {
    @Nested 
    @DisplayName("Given a factory for WinKey V1 commands")
    class V1Tests {
        private static CommandFactory commandFactory;

        @BeforeAll
        static void setUp() {
            commandFactory = new CommandFactory(new KeyerVersion(1, 0));
        }

        @DisplayName("When creating a supported command")
        @ParameterizedTest(name = "{1} is created")
        @MethodSource("supportedCommands")
        void canCreateSupportedCommands(final byte[] expected, final KeyerCommand command) throws Exception {
            final byte[] actual = commandFactory.from(command);
            assertArrayEquals(expected, actual);
        }

        @DisplayName("When using an unsupported command")
        @ParameterizedTest(name = "{0} is rejected")
        @MethodSource("unsupportedCommands")
        void rejectsUnsupportedCommands(final KeyerCommand command) {
            final Exception exception = assertThrows(IllegalArgumentException.class, () -> commandFactory.from(command));
            assertEquals("Command " + command.getClass().getName() + " is not supported in V1", exception.getMessage());
        }

        static Stream<Arguments> supportedCommands() {
            return Stream.of(
                // Admin commands
                arguments(new byte[] { 0x00, 0x00, (byte) 0xFF },    new CalibrateCommand()),
                arguments(new byte[] { 0x00, 0x01 },                 new ResetCommand()),
                arguments(new byte[] { 0x00, 0x02 },                 new HostOpenCommand()),
                arguments(new byte[] { 0x00, 0x03 },                 new HostCloseCommand()),
                arguments(new byte[] { 0x00, 0x04, (byte) 'A' },     new EchoTestCommand('A')),
                arguments(new byte[] { 0x00, 0x05 },                 new ReadPaddleADCommand()),
                arguments(new byte[] { 0x00, 0x06 },                 new ReadSpeedA2DCommand()),
                arguments(new byte[] { 0x00, 0x07 },                 new GetValuesCommand()),
                arguments(new byte[] { 0x00, 0x08 },                 new ReservedCommand()),
                arguments(new byte[] { 0x00, 0x09 },                 new GetCalibrationValueCommand()),

                // Side tone frequency command
                arguments(new byte[] { 0x01, 0x01 },                 new SideToneFrequencyCommand(SideToneFrequencyCommand.SideToneFrequency.FREQUENCY_3759_HZ)),
                arguments(new byte[] { 0x01, 0x02 },                 new SideToneFrequencyCommand(SideToneFrequencyCommand.SideToneFrequency.FREQUENCY_1879_HZ)),
                arguments(new byte[] { 0x01, 0x03 },                 new SideToneFrequencyCommand(SideToneFrequencyCommand.SideToneFrequency.FREQUENCY_1252_HZ)),
                arguments(new byte[] { 0x01, 0x04 },                 new SideToneFrequencyCommand(SideToneFrequencyCommand.SideToneFrequency.FREQUENCY_940_HZ)),
                arguments(new byte[] { 0x01, 0x05 },                 new SideToneFrequencyCommand(SideToneFrequencyCommand.SideToneFrequency.FREQUENCY_752_HZ)),
                arguments(new byte[] { 0x01, 0x06 },                 new SideToneFrequencyCommand(SideToneFrequencyCommand.SideToneFrequency.FREQUENCY_625_HZ)),
                arguments(new byte[] { 0x01, 0x07 },                 new SideToneFrequencyCommand(SideToneFrequencyCommand.SideToneFrequency.FREQUENCY_535_HZ)),
                arguments(new byte[] { 0x01, 0x08 },                 new SideToneFrequencyCommand(SideToneFrequencyCommand.SideToneFrequency.FREQUENCY_469_HZ)),
                arguments(new byte[] { 0x01, 0x09 },                 new SideToneFrequencyCommand(SideToneFrequencyCommand.SideToneFrequency.FREQUENCY_417_HZ)),
                arguments(new byte[] { 0x01, 0x0A },                 new SideToneFrequencyCommand(SideToneFrequencyCommand.SideToneFrequency.FREQUENCY_375_HZ))
            );
        }

        static Stream<KeyerCommand> unsupportedCommands() {
            return Stream.of(
                new SetWK1ModeCommand(),
                new SetWK2ModeCommand(),
                new DumpEEPROMCommand(),
                new LoadEEPROMCommand(),
                new SendStandaloneMessageCommand(0),
                new SideToneControlCommand(true, SideToneControlCommand.SideToneFrequency.FREQUENCY_400_HZ)
            );
        }
    }

    @Nested
    @DisplayName("When using WinKey V2")
    class V2Tests {
        private static CommandFactory commandFactory;

        @BeforeAll
        static void setUp() {
            commandFactory = new CommandFactory(new KeyerVersion(2, 0));
        }

        @DisplayName("When creating a supported command")
        @ParameterizedTest(name = "{1} is created")
        @MethodSource("supportedCommands")
        void canCreateSupportedCommands(final byte[] expected, final KeyerCommand command) throws Exception {
            final byte[] actual = commandFactory.from(command);
            assertArrayEquals(expected, actual);
        }

        @ParameterizedTest(name = "reject unsupported v2 command {0}")
        @MethodSource("unsupportedCommands")
        void rejectsUnsupportedCommands(final KeyerCommand command) {
            final Exception exception = assertThrows(IllegalArgumentException.class, () -> commandFactory.from(command));
            assertEquals("Command " + command.getClass().getName() + " is not supported in V2", exception.getMessage());
        }

        static Stream<Arguments> supportedCommands() {
            return Stream.of(
                // Admin commands
                arguments(new byte[] { 0x00, 0x01 },                 new ResetCommand()),
                arguments(new byte[] { 0x00, 0x02 },                 new HostOpenCommand()),
                arguments(new byte[] { 0x00, 0x03 },                 new HostCloseCommand()),
                arguments(new byte[] { 0x00, 0x04, (byte) 'A' },     new EchoTestCommand('A')),
                arguments(new byte[] { 0x00, 0x07 },                 new GetValuesCommand()),
                arguments(new byte[] { 0x00, 0x08 },                 new ReservedCommand()),
                arguments(new byte[] { 0x00, 0x0A },                 new SetWK1ModeCommand()),
                arguments(new byte[] { 0x00, 0x0B },                 new SetWK2ModeCommand()),
                arguments(new byte[] { 0x00, 0x0C },                 new DumpEEPROMCommand()),
                arguments(new byte[] { 0x00, 0x0D },                 new LoadEEPROMCommand()),

                arguments(new byte[] { 0x00, 0x0E, 0x00 },            new SendStandaloneMessageCommand(0)),
                arguments(new byte[] { 0x00, 0x0E, 0x01 },            new SendStandaloneMessageCommand(1)),
                arguments(new byte[] { 0x00, 0x0E, 0x02 },            new SendStandaloneMessageCommand(2)),
                arguments(new byte[] { 0x00, 0x0E, 0x03 },            new SendStandaloneMessageCommand(3)),
                arguments(new byte[] { 0x00, 0x0E, 0x04 },            new SendStandaloneMessageCommand(4)),
                arguments(new byte[] { 0x00, 0x0E, 0x05 },            new SendStandaloneMessageCommand(5)),
                arguments(new byte[] { 0x00, 0x0E, 0x06 },            new SendStandaloneMessageCommand(6)),

                // Side tone frequency command
                arguments(new byte[] { 0x01, (byte) 0x81 },           new SideToneControlCommand(true, SideToneFrequency.FREQUENCY_4000_HZ)),
                arguments(new byte[] { 0x01, (byte) 0x82 },           new SideToneControlCommand(true, SideToneFrequency.FREQUENCY_2000_HZ)),
                arguments(new byte[] { 0x01, (byte) 0x83 },           new SideToneControlCommand(true, SideToneFrequency.FREQUENCY_1333_HZ)),
                arguments(new byte[] { 0x01, (byte) 0x84 },           new SideToneControlCommand(true, SideToneFrequency.FREQUENCY_1000_HZ)),
                arguments(new byte[] { 0x01, (byte) 0x85 },           new SideToneControlCommand(true, SideToneFrequency.FREQUENCY_800_HZ)),
                arguments(new byte[] { 0x01, (byte) 0x86 },           new SideToneControlCommand(true, SideToneFrequency.FREQUENCY_666_HZ)),
                arguments(new byte[] { 0x01, (byte) 0x87 },           new SideToneControlCommand(true, SideToneFrequency.FREQUENCY_571_HZ)),
                arguments(new byte[] { 0x01, (byte) 0x88 },           new SideToneControlCommand(true, SideToneFrequency.FREQUENCY_500_HZ)),
                arguments(new byte[] { 0x01, (byte) 0x89 },           new SideToneControlCommand(true, SideToneFrequency.FREQUENCY_444_HZ)),
                arguments(new byte[] { 0x01, (byte) 0x8A },           new SideToneControlCommand(true, SideToneFrequency.FREQUENCY_400_HZ)),

                arguments(new byte[] { 0x01, (byte) 0x01 },           new SideToneControlCommand(false, SideToneFrequency.FREQUENCY_4000_HZ))
            );
        }

        static Stream<KeyerCommand> unsupportedCommands() {
            return Stream.of(
                new CalibrateCommand(),
                new ReadPaddleADCommand(),
                new ReadSpeedA2DCommand()
            );
        }
    }
}
