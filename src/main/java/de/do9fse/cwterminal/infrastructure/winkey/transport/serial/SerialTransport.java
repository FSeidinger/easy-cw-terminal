package de.do9fse.cwterminal.infrastructure.winkey.transport.serial;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fazecast.jSerialComm.SerialPort;

import de.do9fse.cwterminal.infrastructure.winkey.ByteTransport;

/**
 * Implements the physical transport to a keyer device using a locally connected
 * serial port
 */
public class SerialTransport implements ByteTransport {
    private static final Logger LOGGER = LoggerFactory.getLogger(SerialTransport.class);

    private final SerialPort serialPort;
    private final String portName;

    private final OutputStream outputStream;
    private final InputStream inputStream;

    /**
     * Creates a new serial transport.
     *
     * @param serialPort The configured serial port to use for communicating
     * with the serial device.
     */
    public SerialTransport(final SerialPort serialPort) {
        this.serialPort = Objects.requireNonNull(serialPort, "Serial port must not be null");
        this.portName = serialPort.getSystemPortPath();

        // The streams are thin wrappers around the serial interface and can be
        // created before port is open
        this.outputStream = serialPort.getOutputStream();
        this.inputStream = serialPort.getInputStream();
        
        LOGGER.info("Successfully created serial transport");
    }

    public void open() throws IOException {
        if (!serialPort.isOpen() && !serialPort.openPort()) {
            throw new IOException("Failed to open serial port: " + portName);
        }

        LOGGER.info("Opened the serial transport to {}", portName);
    }

    @Override
    public void close() throws IOException {
        if (serialPort.isOpen() && !serialPort.closePort()) {
            throw new IOException("Failed to close serial port: " + portName);
        }

        LOGGER.info("Closed serial transport to {}", portName);
    }

    
    @Override
    public void discardInput() {
        serialPort.flushIOBuffers();
    }

    @Override
    public void send(byte[] buffer) throws IOException {
        this.outputStream.write(buffer);
    }

    @Override
    public int receive() throws IOException {
        return this.inputStream.read();
    }

    @Override
    public int bytesAvailable() {
        return serialPort.bytesAvailable();
    }
}