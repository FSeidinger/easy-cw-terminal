package de.do9fse.cwterminal.core.port.out;

import de.do9fse.cwterminal.core.model.KeyerSession;
import de.do9fse.cwterminal.infrastructure.winkey.CommandFactory;
import de.do9fse.cwterminal.infrastructure.winkey.KeyerCommandQueue;

public interface ApplicationContext {
    void setFactory(final CommandFactory factory);
    CommandFactory getFactory();

    KeyerCommandQueue getQueue();
    KeyerSession getSession();
}