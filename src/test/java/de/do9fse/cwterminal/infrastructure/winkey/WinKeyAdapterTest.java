package de.do9fse.cwterminal.infrastructure.winkey;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import de.do9fse.cwterminal.core.model.KeyerCommand;
import de.do9fse.cwterminal.core.port.out.WinKeyTransport;

@DisplayName("WinKey adapter tests")
@ExtendWith(MockitoExtension.class)
class WinKeyAdapterTest {
    @Mock
    private WinKeyTransport delegate;

    private WinKeyAdapter adapter;

    @BeforeEach
    void setUp() {
        this.adapter = new WinKeyAdapter(delegate);
    }

    @Test
    @DisplayName ("Test that the adapter can open the transport")
    void canOpen() throws IOException {
        adapter.open();

        verify(delegate).open();
    }

    @Test
    @DisplayName ("Test that the adapter can close the transport")
    void canClose() throws IOException {
        adapter.close();

        verify(delegate).close();
    }

    @Test
    @DisplayName("Test that the adapter can send an OpenHostCommand")
    void canSendCommand() throws IOException {
        final KeyerCommand command = new KeyerCommand.OpenHostCommand();

        adapter.sendCommand(command);

        verify(delegate).sendCommand(command);
    }

    @Test
    @DisplayName("Test that the adapter can send a SendTextCommand")
    void canSendTextCommand() throws IOException {
        final KeyerCommand command = new KeyerCommand.SendTextCommand("Hello, World!");

        adapter.sendCommand(command);

        verify(delegate).sendCommand(command);
    }

    @Test
    @DisplayName("Test that the adapter can send a sanitized SendTextCommand")
    void canSendSanitizedTextCommand() throws IOException {
        final KeyerCommand command = new KeyerCommand.SendTextCommand("Hello, World!\u007F");
        final Exception exception = assertThrows(IllegalArgumentException.class, () -> adapter.sendCommand(command));
        assertEquals("CW text contains invalid characters or WinKey control bytes: Hello, World!\u007F", exception.getMessage());
        verifyNoInteractions(delegate);
    }

    @Test
    @DisplayName("Test that the adapter rejects null commands")
    void rejectsInvalidCommand() {
        final Exception exception = assertThrows(NullPointerException.class, () -> adapter.sendCommand(null));
        assertEquals("Command must not be null",exception.getMessage());
        verifyNoInteractions(delegate);
    }

    @Test
    @DisplayName("Test that the adapter rejects invalid transport")
    void rejectsInvalidTransport() {
        final Exception exception = assertThrows(NullPointerException.class, () -> new WinKeyAdapter(null));
        assertEquals("Delegate must not be null",exception.getMessage());
        verifyNoInteractions(delegate);
    }
}
