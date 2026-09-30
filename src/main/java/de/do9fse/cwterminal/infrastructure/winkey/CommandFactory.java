package de.do9fse.cwterminal.infrastructure.winkey;

import de.do9fse.cwterminal.core.model.KeyerCommand;

public interface CommandFactory {
    static byte[] from(final KeyerCommand command) {
        return switch(command) {
            case KeyerCommand.OpenHostCommand openHostCommand -> new byte[] { 0x00, 0x02 };
            case KeyerCommand.TextCommand textCommand -> textCommand.text().getBytes();
            default ->  throw new IllegalArgumentException("Unsupported command type: " + command.getClass().getName());
        };
    }
}
