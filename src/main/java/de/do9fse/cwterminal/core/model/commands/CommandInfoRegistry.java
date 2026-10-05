package de.do9fse.cwterminal.core.model.commands;

import java.util.Optional;

public interface CommandInfoRegistry { 
    int getRegisteredCommandCount();
    Optional<CommandInfo> getCommandInfo(final Class<? extends WinKeyCommand> commandType);
}
