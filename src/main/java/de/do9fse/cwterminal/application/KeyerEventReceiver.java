package de.do9fse.cwterminal.application;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.do9fse.cwterminal.core.model.KeyerEvent;
import de.do9fse.cwterminal.core.model.KeyerSession;
import de.do9fse.cwterminal.core.port.out.SessionRepository;
import de.do9fse.cwterminal.core.port.out.WinKeyReceiver;

import de.do9fse.cwterminal.core.model.KeyerEvent.HostOpenedEvent;

public class KeyerEventReceiver implements WinKeyReceiver {
    private static final Logger LOGGER = LoggerFactory.getLogger(KeyerEventReceiver.class);

    private final SessionRepository repository;

    public KeyerEventReceiver(final SessionRepository repository) {
        this.repository = Objects.requireNonNull(repository, "Repository must not be null");
    }

    @Override
    public void on(final KeyerEvent event) {
        final KeyerSession session = repository.loadSession();

        switch (event) {
            case HostOpenedEvent hostOpenedEvent -> session.on(hostOpenedEvent);
            default -> LOGGER.error("Unknown event type {}", event.getClass().getName());
        }
    }
}
