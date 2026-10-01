package de.do9fse.cwterminal.core.port.in;

import de.do9fse.cwterminal.core.model.commands.HostOpenCommand;

public interface KeyerService {
    void handleCommand(final HostOpenCommand command) throws Exception;
}
