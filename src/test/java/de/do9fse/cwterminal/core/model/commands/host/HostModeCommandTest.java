package de.do9fse.cwterminal.core.model.commands.host;

import static de.do9fse.cwterminal.testtools.CommandTestTools.assertPayload;

import org.junit.jupiter.api.Test;

class HostModeCommandTest {
    @Test
    void sideToneControlEncodesEveryFrequencyAndPaddleOnlyFlag() {
        final SideToneControlCommand.SideToneFrequency[] frequencies =
            SideToneControlCommand.SideToneFrequency.values();

        for (int code = 1; code <= frequencies.length; code++) {
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
            assertPayload(
                new SideToneFrequencyCommand(frequencies[code - 1]),
                new byte[] { 0x01, (byte) code }
            );
        }
    }
}
