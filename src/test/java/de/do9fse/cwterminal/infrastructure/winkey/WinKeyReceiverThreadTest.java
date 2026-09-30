package de.do9fse.cwterminal.infrastructure.winkey;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.time.Instant;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fazecast.jSerialComm.SerialPortTimeoutException;

@DisplayName("WinKey receiver thread tests")
@ExtendWith(MockitoExtension.class)
class WinKeyReceiverThreadTest {
    @Mock
    private ByteTransport transport;

    @Mock
    private KeyerCommandQueue queue;

    private WinKeyReceiverThread receiver;

    @BeforeEach
    void setUp() throws Exception {
        this.receiver = new WinKeyReceiverThread(transport, queue);
    }

    @Test
    @DisplayName("Tests that the receiver thread can be started")
    void canStartReceiver() throws Exception {
        when(transport.receive()).thenThrow(SerialPortTimeoutException.class);
        receiver.start();
        assertTrue(waitOnRunning());
    }

    @Test
    @DisplayName("Tests that the receiver thread can be stopped")
    void stopInterruptsRunningThread() throws Exception {
        when(transport.receive()).thenThrow(SerialPortTimeoutException.class);
        receiver.start();
        waitOnRunning();
        receiver.stop();

        assertFalse(receiver.isRunning());
    }

    @Test
    @DisplayName("Tests that the receiver rejects invalid dependencies")
    void rejectsNullConstructorDependencies() {
        final ByteTransport transport = Mockito.mock(ByteTransport.class);
        final KeyerCommandQueue commandQueue = Mockito.mock(KeyerCommandQueue.class);

        assertThrows(NullPointerException.class, () -> new WinKeyReceiverThread(null, commandQueue));
        assertThrows(NullPointerException.class, () -> new WinKeyReceiverThread(transport, null));
    }

    private boolean waitOnRunning() throws Exception {
        final Duration waitTime = Duration.ofSeconds(5);
        final Instant deadLine = Instant.now().plus(waitTime);

        while (!receiver.isRunning() && Instant.now().isBefore(deadLine)) {
            Thread.sleep(100);
        }

        return receiver.isRunning();

    }
}