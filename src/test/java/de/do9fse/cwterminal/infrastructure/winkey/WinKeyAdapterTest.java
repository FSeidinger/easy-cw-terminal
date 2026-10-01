package de.do9fse.cwterminal.infrastructure.winkey;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.concurrent.TimeoutException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import de.do9fse.cwterminal.core.model.KeyerSession;
import de.do9fse.cwterminal.core.model.KeyerVersion;
import de.do9fse.cwterminal.core.model.commands.HostOpenCommand;
import de.do9fse.cwterminal.core.port.out.ApplicationContext;
import de.do9fse.cwterminal.infrastructure.persistence.inmemory.InMemoryApplicationContext;

@DisplayName("WinKey adapter tests")
@ExtendWith(MockitoExtension.class)
class WinKeyAdapterTest {
    @Mock
    private ByteTransport transport;

    @Mock
    private KeyerCommandQueue queue;

    private ApplicationContext context;

    private WinKeySenderAdapter adapter;

    @BeforeEach
    void setUp() {
        final KeyerVersion version = new KeyerVersion(1, 0);
        final CommandFactory factory = new CommandFactory(version);
        final KeyerSession session = new KeyerSession();

        this.context = new InMemoryApplicationContext(factory, queue, session);

        this.adapter = new WinKeySenderAdapter(this.context, transport);
    }

    @Test
    @DisplayName("Test that the adapter rejects null commands")
    void rejectsInvalidCommand() {
        final Exception exception = assertThrows(NullPointerException.class, () -> adapter.sendCommand(null));
        assertEquals("Command must not be null",exception.getMessage());
        verifyNoInteractions(transport);
    }

    @Test
    @DisplayName("Test that the adapter rejects invalid context")
    void rejectsInvalidTransport() {
        final Exception exception = assertThrows(NullPointerException.class, () -> new WinKeySenderAdapter(null, transport));
        assertEquals("Application context must not be null",exception.getMessage());
        verifyNoInteractions(transport);
    }

    @Test
    @DisplayName("Test that the adapter rejects invalid command queue")
    void rejectsInvalidQueue() {
        final Exception exception = assertThrows(NullPointerException.class, () -> new WinKeySenderAdapter(context, null));
        assertEquals("Byte transport must not be null",exception.getMessage());
        verifyNoInteractions(transport);
    }

    @Test
    @DisplayName ("Test that the adapter can initialize the keyer device")
    void canInitializeKeyerDevice() throws Exception {
        givenTheDefaultInitialization();

        whenInitializing();

        thenKeyerDeviceIsInitializedOnce();

        andCommandIsQueued(new HostOpenCommand());
    }

    @Test
    @DisplayName ("Test that the adapter can initialize retry initialization")
    void canRetryInitializeKeyerDevice() throws Exception {
        givenTheRetryInitialization();

        whenInitializing();

        thenKeyerDeviceIsInitializedTwice();

        andCommandIsQueued(new HostOpenCommand());
    }

    @Test
    @DisplayName ("Test that initialization throws a time out exception if keyer never answers")
    void throwsExceptionOnTimeout() throws Exception {
        givenKeyerNeverResponds();

        thenInitializationTimesOut();
    }

    @Test
    @DisplayName("Test that the adapter can send an OpenHostCommand")
    void canSendCommand() throws Exception {
        final HostOpenCommand command = new HostOpenCommand();

        adapter.sendCommand(command);

        final CommandFactory factory = context.getFactory();
        final byte[] openHostBuffer = factory.from(command);
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
        final CommandFactory factory = context.getFactory();
        final byte[] openHostBuffer = factory.from(new HostOpenCommand());
        final InOrder verifications = Mockito.inOrder(transport);
        verifications.verify(transport).discardInput();
        verifications.verify(transport).send(openHostBuffer);
        verifications.verify(transport).bytesAvailable();

        return verifications;
    }

    private InOrder thenKeyerDeviceIsInitializedTwice() throws Exception {
        final CommandFactory factory = context.getFactory();
        final byte[] openHostBuffer = factory.from(new HostOpenCommand());
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

    private void andCommandIsQueued(final HostOpenCommand command) {
        verify(queue).offer(command);
    }
}
