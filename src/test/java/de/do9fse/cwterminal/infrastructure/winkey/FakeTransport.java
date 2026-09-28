package de.do9fse.cwterminal.infrastructure.winkey;

class FakeTransport implements WinKeyTransport {
    boolean isOpen;
    byte[] writtenData;

    @Override
    public void open() {
        isOpen = true;
    }

    @Override
    public void write(final byte[] data) {
        writtenData = data.clone();
    }

    @Override
    public void close() {
        isOpen = false;
    }
}