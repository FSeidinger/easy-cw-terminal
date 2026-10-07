package de.do9fse.cwterminal.core.model.commands.admin;

import static de.do9fse.cwterminal.testtools.CommandTestTools.assertPayload;

import org.junit.jupiter.api.Test;

class AdminCommandTest {
    @Test
    void producesExpectedAdminCommandBytes() {
        assertPayload(new CalibrateCommand(), new byte[] { 0x00, 0 });
        assertPayload(new ResetCommand(), new byte[] { 0x00, 1 });
        assertPayload(new HostOpenCommand(), new byte[] { 0x00, 2 });
        assertPayload(new HostCloseCommand(), new byte[] { 0x00, 3 });
        assertPayload(new EchoTestCommand('A'), new byte[] { 0x00, 4, 'A' });
        assertPayload(new ReadPaddleA2DCommand(), new byte[] { 0x00, 5 });
        assertPayload(new ReadSpeedA2DCommand(), new byte[] { 0x00, 6 });
        assertPayload(new GetValuesCommand(), new byte[] { 0x00, 7 });
        assertPayload(new ReservedCommand(), new byte[] { 0x00, 8 });
        assertPayload(new GetCalibrationValueCommand(), new byte[] { 0x00, 9 });
        assertPayload(new SetWK1ModeCommand(), new byte[] { 0x00, 10 });
        assertPayload(new SetWK2ModeCommand(), new byte[] { 0x00, 11 });
        assertPayload(new DumpEEPROMCommand(), new byte[] { 0x00, 12 });
        assertPayload(new LoadEEPROMCommand(), new byte[] { 0x00, 13 });
        assertPayload(new SendStandaloneMessageCommand(6), new byte[] { 0x00, 14, 6 });
    }

}
