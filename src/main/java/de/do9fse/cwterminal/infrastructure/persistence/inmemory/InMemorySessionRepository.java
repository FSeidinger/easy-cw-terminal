package de.do9fse.cwterminal.infrastructure.persistence.inmemory;

import de.do9fse.cwterminal.core.model.KeyerSession;
import de.do9fse.cwterminal.core.port.out.SessionRepository;

public class InMemorySessionRepository implements SessionRepository {
    private KeyerSession session;

    public InMemorySessionRepository() {
        this.session = new KeyerSession();
    }

    @Override
    public KeyerSession loadSession() {
        return session;
    }
}