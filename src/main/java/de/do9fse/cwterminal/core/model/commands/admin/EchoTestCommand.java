package de.do9fse.cwterminal.core.model.commands.admin;

public record EchoTestCommand(char echoChar) implements AdminCommand {
    public EchoTestCommand {
        if (echoChar < 0x20 || echoChar > 0x7E) {
            throw new IllegalArgumentException("Echo character must be a printable ASCII character (0x20-0x7E)");
        }
    }

    @Override
    public byte[] getPayloadBytes() {
        return new byte[]{ 0x00, 4 };
    }
}
