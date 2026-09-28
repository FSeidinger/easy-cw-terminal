package de.do9fse.cwterminal.core.model;

public sealed interface KeyerCommand permits KeyerCommand.OpenHostCommand, KeyerCommand.SendTextCommand {
    record OpenHostCommand() implements KeyerCommand {}
    record SendTextCommand(String text) implements KeyerCommand {}
};