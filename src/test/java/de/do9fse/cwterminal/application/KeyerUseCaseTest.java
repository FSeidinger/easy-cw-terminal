package de.do9fse.cwterminal.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import de.do9fse.cwterminal.core.model.KeyerCommand.OpenHostCommand;
import de.do9fse.cwterminal.core.model.KeyerSession;
import de.do9fse.cwterminal.core.port.out.SessionRepository;
import de.do9fse.cwterminal.core.port.out.WinKeySender;

@ExtendWith(MockitoExtension.class)
@DisplayName("Keyer use case tests")
public class KeyerUseCaseTest {
    @Mock
    private SessionRepository sessionRepository;

    @Mock
    private WinKeySender transport;

    private KeyerSession session;
    
    // Subject under test
    private KeyerUseCase keyerUseCase;

    @BeforeEach
    void setUp() {
        this.session = new KeyerSession();
        this.keyerUseCase = new KeyerUseCase(sessionRepository, transport);
    }

    @Test
    @DisplayName ("Test that the use case can handle an OpenHostCommand")
    void canHandleHostOpenCommand() throws Exception {
        when(sessionRepository.loadSession()).thenReturn(session);

        // Send OpenHostCommand
        final OpenHostCommand command = new OpenHostCommand();
        keyerUseCase.handleHostOpenCommand(command);

        // Verify interactions
        final InOrder inOrder = inOrder(sessionRepository, transport);
        inOrder.verify(sessionRepository).loadSession();
        inOrder.verify(transport).sendCommand(command);
    }

    @Test
    @DisplayName ("Test that the use case handles a pending OpenHostCommand rejection")
    void canHandlePendingOpenHostCommand() throws Exception {
        when(sessionRepository.loadSession()).thenReturn(session);

        // Send first OpenHostCommand -> session is now pending
        final OpenHostCommand command = new OpenHostCommand();
        keyerUseCase.handleHostOpenCommand(command);

        // Send second OpenHostCommand -> should throw IllegalStateException due to pending state
        final Exception exception = assertThrows(IllegalStateException.class, () -> keyerUseCase.handleHostOpenCommand(command));
        assertEquals("Open host command is already pending", exception.getMessage());

        // Verify interactions
        final InOrder inOrder = inOrder(sessionRepository, transport);
        inOrder.verify(sessionRepository).loadSession();
        inOrder.verify(transport).sendCommand(command);
        inOrder.verify(sessionRepository).loadSession();
    }
}
