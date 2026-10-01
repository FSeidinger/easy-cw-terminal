package de.do9fse.cwterminal.core.model.commands;

public final class SendStandaloneMessageCommand extends AdminCommand {
    private final int messageId;

    public SendStandaloneMessageCommand(final int messageId) {
        if (messageId < 0 || messageId > 6) {
            throw new IllegalArgumentException("Message ID must be between 0 and 6");
        }

        this.messageId = messageId;
    }

    public int getMessageId() {
        return messageId;
    }

    @Override
    protected String stringifyFields() {
        return "messageId=" + messageId;
    }
}
