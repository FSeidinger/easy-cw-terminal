package de.do9fse.cwterminal;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fazecast.jSerialComm.SerialPort;

import de.do9fse.cwterminal.core.port.out.WinKeyTransport;
import de.do9fse.cwterminal.infrastructure.winkey.transport.serial.WinKeySerialTransport;

public final class Main {
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static final int ERROR_OPEN = -1;

    private String portName;
    private SerialPort port;

    private WinKeyTransport transport;

    public static void main(final String[] args) throws Exception {
        if (args.length != 1) {
            LOGGER.error("Usage: easy-cw-terminal <serial-port>");
            return;
        }

        final Main application = new Main(args[0]);
        application.start();
        application.waitForExit();
        application.stop();
    }

    private Main(final String portName) throws Exception {
        this.portName = portName;
    }

    private void start() throws Exception {
        createSerialTransport();
    }

    private void waitForExit() throws IOException {
        LOGGER.info("Press Enter to exit");
        System.in.read();
    }

    private void stop() throws Exception {
        if (transport != null) {
            transport.close();
        }
    }

    private void createSerialTransport() throws Exception {
        // Create serial port configuration with WinKey default values
        this.port = SerialPort.getCommPort(portName);
        this.port.setBaudRate(1200);
        port.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING, 1000, 0);

        transport = new WinKeySerialTransport(port);
        transport.open();
    }
}