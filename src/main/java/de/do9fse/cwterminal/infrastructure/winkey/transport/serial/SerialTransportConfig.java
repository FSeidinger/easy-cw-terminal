package de.do9fse.cwterminal.infrastructure.winkey.transport.serial;

import com.fazecast.jSerialComm.SerialPort;

public record SerialTransportConfig(String portName, int baudRate, int dataBits, int stopBits, int parity) {
    private static final int DEFAULT_BAUD_RATE = 1200;
    private static final int DEFAULT_DATA_BITS = 8;
    private static final int DEFAULT_STOP_BITS = 2;
    private static final int DEFAULT_PARITY = SerialPort.NO_PARITY;

    public SerialTransportConfig(final String portName) {
        this(portName, DEFAULT_BAUD_RATE, DEFAULT_DATA_BITS, DEFAULT_STOP_BITS, DEFAULT_PARITY);
    }

    public SerialTransportConfig(final String portName, final int baudRate, final int dataBits) {
        this(portName, baudRate, dataBits, DEFAULT_STOP_BITS, DEFAULT_PARITY);
    }

    public SerialTransportConfig(final String portName, final int baudRate, final int dataBits, final int stopBits) {
        this(portName, baudRate, dataBits, stopBits, DEFAULT_PARITY);
    }

    public SerialTransportConfig {
        if (portName == null || portName.isBlank()) {
            throw new IllegalArgumentException("Port name must not be blank");
        }

        if (baudRate <= 0) {
            throw new IllegalArgumentException("Baud rate must be greater than zero");
        }

        if (dataBits < 5 || dataBits > 8) {
            throw new IllegalArgumentException("Data bits must be between 5 and 8");
        }

        if (stopBits != SerialPort.ONE_STOP_BIT && stopBits != SerialPort.TWO_STOP_BITS && stopBits != SerialPort.ONE_POINT_FIVE_STOP_BITS) {
            throw new IllegalArgumentException("Stop bits must be 1, 1.5, or 2");
        }

        if (parity != SerialPort.NO_PARITY && parity != SerialPort.ODD_PARITY  && parity != SerialPort.EVEN_PARITY && parity != SerialPort.MARK_PARITY && parity != SerialPort.SPACE_PARITY) {
            throw new IllegalArgumentException("Unsupported parity value: " + parity);
        }
    }
}