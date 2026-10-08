package de.do9fse.winkey.lib.testtools;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import de.do9fse.winkey.lib.core.model.commands.WinKeyCommand;

public final class CommandTestTools {
    private CommandTestTools() {}

    public static void assertPayload(final WinKeyCommand command, final byte[] expectedBytes) {
        assertArrayEquals(expectedBytes, command.getPayloadBytes());
        assertArrayEquals(expectedBytes, command.toProtocolBytes());
    }
}
