package de.do9fse.cwterminal.core.port.out;

import de.do9fse.cwterminal.core.model.KeyerSession;

public interface SessionRepository {
    KeyerSession loadSession();
}