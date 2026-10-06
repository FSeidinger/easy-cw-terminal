package de.do9fse.cwterminal.core.model.commands.admin;

public record SendStandaloneMessageCommand(int messageId) implements AdminCommand<SendStandaloneMessageCommand> {
    public SendStandaloneMessageCommand {
        if (messageId < 0 || messageId > 6) {
            throw new IllegalArgumentException("Message ID must be between 0 and 6");
        }
    }
}
