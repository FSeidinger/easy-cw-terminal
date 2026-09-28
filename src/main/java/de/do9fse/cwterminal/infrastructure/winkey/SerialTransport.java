package de.do9fse.cwterminal.infrastructure.winkey;

import java.io.IOException;
import java.util.Objects;

import com.fazecast.jSerialComm.SerialPort;

public class SerialTransport implements WinKeyTransport {
    private final SerialPort serialPort;

    public SerialTransport(final SerialTransportConfig config) throws IOException {
        Objects.requireNonNull(config, "Config must not be null");
        serialPort = SerialPort.getCommPort(config.portName());

        if (!serialPort.setComPortParameters(config.baudRate(), config.dataBits(), config.stopBits(), config.parity())) {
            throw new IOException("Could not configure serial port: " + config.portName());
        }
    }

    @Override
    public void open() throws IOException {
        if (!serialPort.isOpen() && !serialPort.openPort()) {
            throw new IOException("Could not open serial port: " + serialPort.getSystemPortName());
        }
    }

    @Override
    public void write(final byte[] data) throws IOException {
        Objects.requireNonNull(data, "Data must not be null");
        if (!serialPort.isOpen()) {
            throw new IOException("Serial port is not connected");
        }

        final long bytesWritten = serialPort.writeBytes(data, data.length);
        if (bytesWritten != data.length) {
            throw new IOException("Could not write complete serial data");
        }
    }

    @Override
    public void close() throws IOException {
        if (serialPort.isOpen() && !serialPort.closePort()) {
            throw new IOException("Could not close serial port: " + serialPort.getSystemPortName());
        }
    }
}