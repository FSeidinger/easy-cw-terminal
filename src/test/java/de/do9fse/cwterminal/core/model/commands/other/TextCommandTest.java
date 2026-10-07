package de.do9fse.cwterminal.core.model.commands.other;

import static de.do9fse.cwterminal.testtools.CommandTestTools.assertPayload;

import org.junit.jupiter.api.Test;

class TextCommandTest {
    @Test
    void encodesTextAsProtocolBytes() {
        final TextCommand command = new TextCommand("CQ TEST");
        final byte[] expectedBytes = "CQ TEST".getBytes();

        assertPayload(command, expectedBytes);
    }
}
