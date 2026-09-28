package de.do9fse.cwterminal.infrastructure.winkey;

import java.io.IOException;
import java.util.Objects;

import de.do9fse.cwterminal.core.model.KeyerCommand;
import de.do9fse.cwterminal.core.port.out.WinKeyTransport;

public class WinKeyAdapter implements WinKeyTransport {
    private final WinKeyTransport delegate;

    public WinKeyAdapter(final WinKeyTransport delegate) {
        this.delegate = Objects.requireNonNull(delegate, "Delegate must not be null");
    }

    @Override
    public void open() throws IOException {
        delegate.open();
    }

    @Override
    public void sendCommand(final KeyerCommand command) throws IOException {
        Objects.requireNonNull(command, "Command must not be null");

        switch (command) {
            case KeyerCommand.OpenHostCommand openHostCommand -> delegate.sendCommand(command);
            case KeyerCommand.SendTextCommand sendTextCommand -> sendText(sendTextCommand);

            default -> throw new IllegalArgumentException("Unsupported command type: " + command.getClass().getName());
        }
    }

    private void sendText(final KeyerCommand.SendTextCommand command) throws IOException {
        CwTextValidator.sanitizeAndValidate(command.text());
        delegate.sendCommand(command);
    }

    @Override
    public void close() throws IOException {
        delegate.close();
    }
}
