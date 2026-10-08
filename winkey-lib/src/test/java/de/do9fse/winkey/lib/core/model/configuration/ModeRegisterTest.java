package de.do9fse.winkey.lib.core.model.configuration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ModeRegisterTest {
    @Test
    void parsesModeFlagsAndKeyMode() {
        final ModeRegister modeRegister = ModeRegister.fromProtocol(0xff);

        assertFalse(modeRegister.isPaddleWatchdogEnabled());
        assertTrue(modeRegister.isPaddleEchobackEnabled());
        assertEquals(KeyMode.BUG_MODE, modeRegister.keyMode());
        assertTrue(modeRegister.isPaddleSwapped());
        assertTrue(modeRegister.isSerialEchobackEnabled());
        assertTrue(modeRegister.isAutospaceEnabled());
        assertTrue(modeRegister.isCTSpacingEnabled());
    }

    @Test
    void treatsWatchdogBitAsDisableFlagAndSupportsSignedBytes() {
        assertFalse(ModeRegister.fromProtocol((byte) 0x80).isPaddleWatchdogEnabled());
        assertTrue(ModeRegister.fromProtocol(0).isPaddleWatchdogEnabled());
    }

    @Test
    void convertsModeFlagsAndKeyModeToProtocolBits() {
        final ModeRegister modeRegister = new ModeRegister(true, true, KeyMode.BUG_MODE, true, true, true, true);
        assertEquals(0x7f, modeRegister.toProtocolValue());
        assertEquals(0xff, ModeRegister.fromProtocol(0xff).toProtocolValue());
    }
}
