package de.do9fse.cwterminal.infrastructure.winkey;

import java.io.IOException;
import java.util.Objects;

import com.fazecast.jSerialComm.SerialPort;

import de.do9fse.cwterminal.core.model.KeyerCommand;
import de.do9fse.cwterminal.core.port.out.WinKeyTransport;
import de.do9fse.cwterminal.infrastructure.winkey.v2.AdminCommand;

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
    public void close() throws IOException {
        if (serialPort.isOpen() && !serialPort.closePort()) {
            throw new IOException("Could not close serial port: " + serialPort.getSystemPortName());
        }
    }

    @Override
    public void sendCommand(KeyerCommand command) throws IOException {
        switch (command) {
            case KeyerCommand.OpenHostCommand openHostCommand -> sendOpenHostCommand();
            case KeyerCommand.SendTextCommand sendTextCommand -> sendTextCommand(sendTextCommand);
            
            default -> throw new IllegalArgumentException("Unsupported command type: " + command.getClass().getName());
        }
    }

    private void sendOpenHostCommand() throws IOException {
        final AdminCommand openHostCommand = AdminCommand.hostOpen();
        sendBuffer(openHostCommand.getCommandBytes());
    }

    private void sendTextCommand(final KeyerCommand.SendTextCommand command) throws IOException {
        final byte[] textBytes = command.text().getBytes();
        sendBuffer(textBytes);
    }

    private void sendBuffer(final byte[] buffer) throws IOException {
        final int bufferLength = buffer.length;

        final int bytesWritten = serialPort.writeBytes(buffer, bufferLength);

        if (bytesWritten != bufferLength) {
            throw new IOException("Could only write " + bytesWritten + " out of " + bufferLength + " bytes to serial port: " + serialPort.getSystemPortName());
        }
    }
}