package de.do9fse.cwterminal.infrastructure.winkey;

import java.io.IOException;
import java.util.Objects;

import de.do9fse.cwterminal.infrastructure.winkey.v2.WinKeyCommand;

public class WinKeyAdapter implements AutoCloseable {
    private final WinKeyTransport transport;

    public WinKeyAdapter(final WinKeyTransport transport) {
        this.transport = Objects.requireNonNull(transport, "Transport must not be null");
    }

    public void open() throws IOException {
        transport.open();
    }

    @Override
    public void close() throws IOException {
        transport.close();
    }

    public void sendCommand(final WinKeyCommand command) throws IOException {
        Objects.requireNonNull(command, "Command must not be null");
        transport.write(command.getCommandBytes());
    }
}
