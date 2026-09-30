package de.do9fse.cwterminal.infrastructure.winkey;

import java.io.IOException;

public interface ByteTransport extends AutoCloseable {
    void discardInput();

    void send(final byte[] buffer) throws IOException;
    int receive() throws IOException;

    int bytesAvailable();
}
