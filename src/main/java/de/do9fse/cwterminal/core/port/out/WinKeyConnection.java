package de.do9fse.cwterminal.core.port.out;

import java.io.IOException;

public interface WinKeyConnection extends AutoCloseable {
    void open() throws IOException;

    @Override
    void close() throws IOException;
}
