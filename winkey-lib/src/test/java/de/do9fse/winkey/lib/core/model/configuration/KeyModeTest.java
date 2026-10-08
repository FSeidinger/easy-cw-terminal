package de.do9fse.winkey.lib.core.model.configuration;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class KeyModeTest {
    @Test
    void parsesAllModesFromProtocolBits() {
        assertEquals(KeyMode.IAMBIC_B, KeyMode.fromProtocol(0x00));
        assertEquals(KeyMode.IAMBIC_A, KeyMode.fromProtocol(0x10));
        assertEquals(KeyMode.ULTIMATIC, KeyMode.fromProtocol(0x20));
        assertEquals(KeyMode.BUG_MODE, KeyMode.fromProtocol(0x30));
    }

    @Test
    void convertsModesToProtocolBits() {
        assertEquals(0x00, KeyMode.IAMBIC_B.toProtocolValue());
        assertEquals(0x10, KeyMode.IAMBIC_A.toProtocolValue());
        assertEquals(0x20, KeyMode.ULTIMATIC.toProtocolValue());
        assertEquals(0x30, KeyMode.BUG_MODE.toProtocolValue());
    }
}
