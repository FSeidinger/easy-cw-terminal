package de.do9fse.cwterminal.infrastructure.winkey;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import de.do9fse.cwterminal.core.model.KeyerCommand;

@DisplayName("WinKey adapter tests")
@ExtendWith(MockitoExtension.class)
class WinKeyAdapterTest {
    @Mock
    private ByteTransport transport;

    @Mock
    private KeyerCommandQueue queue;

    private WinKeySenderAdapter adapter;

    @BeforeEach
    void setUp() {
        this.adapter = new WinKeySenderAdapter(transport, queue);
    }

    @Test
    @DisplayName("Test that the adapter rejects null commands")
    void rejectsInvalidCommand() {
        final Exception exception = assertThrows(NullPointerException.class, () -> adapter.sendCommand(null));
        assertEquals("Command must not be null",exception.getMessage());
        verifyNoInteractions(transport);
    }

    @Test
    @DisplayName("Test that the adapter rejects invalid transport")
    void rejectsInvalidTransport() {
        final Exception exception = assertThrows(NullPointerException.class, () -> new WinKeySenderAdapter(null, queue));
        assertEquals("Transport must not be null",exception.getMessage());
        verifyNoInteractions(transport);
    }

    @Test
    @DisplayName("Test that the adapter rejects invalid command queue")
    void rejectsInvalidQueue() {
        final Exception exception = assertThrows(NullPointerException.class, () -> new WinKeySenderAdapter(transport, null));
        assertEquals("Keyer command queue must not be null",exception.getMessage());
        verifyNoInteractions(transport);
    }

    @Test
    @DisplayName ("Test that the adapter can initialize the keyer device")
    void canInitializeKeyerDevice() throws Exception {
        givenTheDefaultInitialization();

        whenInitializing();

        thenKeyerDeviceIsInitializedOnce();

        andCommandIsQueued(new KeyerCommand.OpenHostCommand());
    }

    @Test
    @DisplayName ("Test that the adapter can initialize retry initialization")
    void canRetryInitializeKeyerDevice() throws Exception {
        givenTheRetryInitialization();

        whenInitializing();

        thenKeyerDeviceIsInitializedTwice();

        andCommandIsQueued(new KeyerCommand.OpenHostCommand());
    }

    @Test
    @DisplayName ("Test that initialization throws a time out exception if keyer never answers")
    void throwsExceptionOnTimeout() throws Exception {
        givenKeyerNeverResponds();

        thenInitializationTimesOut();
    }

    @Test
    @DisplayName("Test that the adapter can send an OpenHostCommand")
    void canSendCommand() throws IOException {
        final KeyerCommand command = new KeyerCommand.OpenHostCommand();

        adapter.sendCommand(command);

        final byte[] openHostBuffer = CommandFactory.from(new KeyerCommand.OpenHostCommand());
        final InOrder verifications = Mockito.inOrder(queue, transport);
        verifications.verify(queue).beginSendTransaction();
        verifications.verify(transport).send(openHostBuffer);
        verifications.verify(queue).commitSendTransaction();

        andCommandIsQueued(command);
    }

    private void givenTheDefaultInitialization() {
        when(transport.bytesAvailable()).thenReturn(1);
    }

    private void givenTheRetryInitialization() {
        when(transport.bytesAvailable()).thenReturn(0).thenReturn(1);
    }

    private void givenKeyerNeverResponds() {
        when(transport.bytesAvailable()).thenReturn(0);
    }

    private void whenInitializing() throws Exception {
        adapter.initialize();
    }

    private InOrder thenKeyerDeviceIsInitializedOnce() throws Exception {
        final byte[] openHostBuffer = CommandFactory.from(new KeyerCommand.OpenHostCommand());
        final InOrder verifications = Mockito.inOrder(transport);
        verifications.verify(transport).discardInput();
        verifications.verify(transport).send(openHostBuffer);
        verifications.verify(transport).bytesAvailable();

        return verifications;
    }

    private InOrder thenKeyerDeviceIsInitializedTwice() throws Exception {
        final byte[] openHostBuffer = CommandFactory.from(new KeyerCommand.OpenHostCommand());
        final InOrder verifications = Mockito.inOrder(transport);
        
        verifications.verify(transport).discardInput();
        verifications.verify(transport).send(openHostBuffer);
        verifications.verify(transport, times(2)).bytesAvailable();

        return verifications;
    }

    private void thenInitializationTimesOut() {
        final TimeoutException exception = assertThrows(TimeoutException.class, () -> whenInitializing());
        assertEquals("Keyer did not respond to Open Host command within 5 seconds.", exception.getMessage());
    }

    private void andCommandIsQueued(final KeyerCommand command) {
        verify(queue).offer(command);
    }
}
