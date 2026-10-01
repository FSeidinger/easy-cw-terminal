package de.do9fse.cwterminal.core.model.commands;

public final class EchoTestCommand extends AdminCommand {
    private final char echoChar;

    public EchoTestCommand(final char echoChar) {
        if (echoChar < 0x20 || echoChar > 0x7E) {
            throw new IllegalArgumentException("Echo character must be a printable ASCII character (0x20-0x7E)");
        }
        
        this.echoChar = echoChar;
    }

    public char getEchoChar() {
        return echoChar;
    }

    @Override
    protected String stringifyFields() {
        return "echoChar=" + echoChar;
    }
}
