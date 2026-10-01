package de.do9fse.cwterminal.application;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.do9fse.cwterminal.core.model.KeyerEvent;
import de.do9fse.cwterminal.core.model.KeyerSession;
import de.do9fse.cwterminal.core.port.out.ApplicationContext;
import de.do9fse.cwterminal.core.port.out.WinKeyReceiver;

import de.do9fse.cwterminal.core.model.KeyerEvent.HostOpenedEvent;

public class KeyerEventReceiver implements WinKeyReceiver {
    private static final Logger LOGGER = LoggerFactory.getLogger(KeyerEventReceiver.class);

    private final ApplicationContext repository;

    public KeyerEventReceiver(final ApplicationContext repository) {
        this.repository = Objects.requireNonNull(repository, "Repository must not be null");
    }

    @Override
    public void on(final KeyerEvent event) {
        final KeyerSession session = repository.getSession();

        switch (event) {
            case HostOpenedEvent hostOpenedEvent -> session.on(hostOpenedEvent);
            default -> LOGGER.error("Unknown event type {}", event.getClass().getName());
        }
    }
}
