package de.do9fse.cwterminal.core.model.commands.other;

import de.do9fse.cwterminal.core.model.commands.WinKeyCommand;

public record TextCommand(String text) implements WinKeyCommand {
    public TextCommand {
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException("Text must not be null or blank");
        }
    }
}
