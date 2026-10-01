package de.do9fse.cwterminal.application;

import java.util.Objects;

import de.do9fse.cwterminal.core.model.KeyerSession;
import de.do9fse.cwterminal.core.model.commands.HostOpenCommand;
import de.do9fse.cwterminal.core.port.in.KeyerService;
import de.do9fse.cwterminal.core.port.out.ApplicationContext;
import de.do9fse.cwterminal.core.port.out.WinKeySender;

public class KeyerUseCase implements KeyerService {
    private final ApplicationContext keyerRepository;
    private final WinKeySender transport;

    public KeyerUseCase(final ApplicationContext keyerRepository, final WinKeySender transport) {
        this.keyerRepository = Objects.requireNonNull(keyerRepository, "Keyer repository must not be null");
        this.transport = Objects.requireNonNull(transport, "WinKey transport must not be null");
    }

    @Override
    public void handleCommand(final HostOpenCommand command) throws Exception {
        final KeyerSession session = keyerRepository.getSession();
        session.handleCommand(command);
        transport.sendCommand(command);
    }
}
