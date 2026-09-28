package de.do9fse.cwterminal.infrastructure.winkey;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import de.do9fse.cwterminal.infrastructure.winkey.v2.AdminCommand;

@DisplayName("WinKey adapter tests")
class WinKeyAdapterTest {
    private FakeTransport transport = new FakeTransport();
    private WinKeyAdapter adapter;

    @BeforeEach
    void setUp() throws Exception {
        this.transport = new FakeTransport();
        this.adapter = new WinKeyAdapter(transport);
        this.adapter.open();
    }

    @Test
    @DisplayName ("Test that the adapter can open the transport")
    void canOpen() throws Exception {
        assertTrue(transport.isOpen);
    }

    @Test
    @DisplayName ("Test that the adapter can close the transport")
    void canClose() throws Exception {
        adapter.close();
        assertFalse(transport.isOpen);
    }

    @Test
    @DisplayName("Test that the adapter can send an AdminCommand")
    void canSendCommand() throws IOException {
        adapter.sendCommand(AdminCommand.hostOpen());    
        assertArrayEquals(new byte[] { 0x00, 0x02 }, transport.writtenData);
    }

    @Test
    @DisplayName("Test that the adapter rejects null commands")
    void rejectsInvalidCommand() {
        final Exception exception = assertThrows(NullPointerException.class, () -> adapter.sendCommand(null));
        assertTrue(exception.getMessage().contains("Command must not be null"));
        assertNull(transport.writtenData);
    }

    @Test
    @DisplayName("Test that the adapter rejects invalid transport")
    void rejectsInvalidTransport() {
        final Exception exception = assertThrows(NullPointerException.class, () -> new WinKeyAdapter(null));
        assertTrue(exception.getMessage().contains("Transport must not be null"));
    }
}
