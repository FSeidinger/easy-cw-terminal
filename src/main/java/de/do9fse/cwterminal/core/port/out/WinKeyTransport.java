package de.do9fse.cwterminal.core.port.out;

import java.io.IOException;

import de.do9fse.cwterminal.core.model.KeyerCommand;

public interface WinKeyTransport extends AutoCloseable {
    void sendCommand(final KeyerCommand command) throws IOException;

    void open() throws IOException;
    
    @Override
    void close() throws IOException;
}