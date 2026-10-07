package de.do9fse.cwterminal.core.model.configuration;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class KeyModeTest {
    @Test
    void parsesAllModesFromProtocolBits() {
        assertEquals(KeyMode.IAMBIC_B, KeyMode.parseResponseByte(0x00));
        assertEquals(KeyMode.IAMBIC_A, KeyMode.parseResponseByte(0x10));
        assertEquals(KeyMode.ULTIMATIC, KeyMode.parseResponseByte(0x20));
        assertEquals(KeyMode.BUG_MODE, KeyMode.parseResponseByte(0x30));
    }
}
