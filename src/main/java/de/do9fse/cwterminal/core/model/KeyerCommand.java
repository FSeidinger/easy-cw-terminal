package de.do9fse.cwterminal.core.model;

public sealed interface KeyerCommand permits KeyerCommand.OpenHostCommand, KeyerCommand.TextCommand {
    record OpenHostCommand() implements KeyerCommand {}
    record TextCommand(String text) implements KeyerCommand {}
};