package de.do9fse.cwterminal.infrastructure.persistence.inmemory;

import java.util.Objects;

import de.do9fse.cwterminal.core.model.KeyerSession;
import de.do9fse.cwterminal.core.port.out.ApplicationContext;
import de.do9fse.cwterminal.infrastructure.winkey.CommandFactory;
import de.do9fse.cwterminal.infrastructure.winkey.KeyerCommandQueue;

public class InMemoryApplicationContext implements ApplicationContext {
    private CommandFactory factory;
    private KeyerCommandQueue  queue;
    private KeyerSession session;

    public InMemoryApplicationContext(final CommandFactory factory, final KeyerCommandQueue queue, final KeyerSession session) {
        this.factory = Objects.requireNonNull(factory, "CommandFactory must not be null");
        this.queue = Objects.requireNonNull(queue, "KeyerCommandQueue must not be null");
        this.session = Objects.requireNonNull(session, "KeyerSession must not be null");
    }

    @Override
    public void setFactory(final CommandFactory factory) {
        this.factory = Objects.requireNonNull(factory, "CommandFactory must not be null");
    }

    @Override
    public CommandFactory getFactory() {
        return this.factory;
    }

    @Override
    public KeyerCommandQueue getQueue() {
        return this.queue;
    }

    @Override
    public KeyerSession getSession() {
        return this.session;
    }
}