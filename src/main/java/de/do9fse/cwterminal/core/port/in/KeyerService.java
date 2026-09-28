package de.do9fse.cwterminal.core.port.in;

import de.do9fse.cwterminal.core.model.KeyerCommand.OpenHostCommand;

public interface KeyerService {
    void handleHostOpenCommand(final OpenHostCommand command) throws Exception;
}
