package de.do9fse.cwterminal.infrastructure.winkey;

import java.io.IOException;

public interface WinKeyTransport extends AutoCloseable {
    void open() throws IOException;
    void write(byte[] data) throws IOException;

    @Override
    void close() throws IOException;
}