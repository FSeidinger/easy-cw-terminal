package de.do9fse.cwterminal;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fazecast.jSerialComm.SerialPort;

import de.do9fse.cwterminal.application.KeyerEventReceiver;
import de.do9fse.cwterminal.core.model.KeyerSession;
import de.do9fse.cwterminal.core.model.KeyerVersion;
import de.do9fse.cwterminal.core.model.commands.HostOpenCommand;
import de.do9fse.cwterminal.core.port.out.ApplicationContext;
import de.do9fse.cwterminal.infrastructure.persistence.inmemory.InMemoryApplicationContext;
import de.do9fse.cwterminal.infrastructure.winkey.CommandFactory;
import de.do9fse.cwterminal.infrastructure.winkey.KeyerCommandQueue;
import de.do9fse.cwterminal.infrastructure.winkey.WinKeyReceiverThread;
import de.do9fse.cwterminal.infrastructure.winkey.WinKeySenderAdapter;
import de.do9fse.cwterminal.infrastructure.winkey.transport.serial.SerialTransport;
import de.do9fse.cwterminal.infrastructure.winkey.transport.serial.SerialTransportConfig;

public final class Main {
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static final int ERROR_OPEN = -1;

    private String portName;
    private SerialPort port;

    private ApplicationContext context;

    private SerialTransport transport;
    private WinKeyReceiverThread receiver;

    private WinKeySenderAdapter adapter;

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
        createDependencies();
        createSender();

        this.adapter.initialize();

        createReceiver();
       
    }

    private void waitForExit() throws IOException {
        LOGGER.info("Press Enter to exit");
        System.in.read();
    }

    private void stop() throws Exception {
        if (this.receiver != null) {
            this.receiver.stop();
        }

        if (transport != null) {
            transport.close();
        }
    }

    private void createSerialTransport() throws Exception {
        // Create serial port configuration with WinKey default values
        final SerialTransportConfig config = new SerialTransportConfig(portName);

        this.port = SerialPort.getCommPort(portName);
        port.setComPortParameters(
            config.baudRate(),
            config.dataBits(),
            config.stopBits(),
            config.parity()
        );

        port.clearDTR();

        if (!port.openPort()) {
            LOGGER.error("Failed to open {}", portName);
            System.exit(ERROR_OPEN);
        };

        port.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING, 1000, 5000);

        transport = new SerialTransport(port);
        transport.open();
    }

    private void createDependencies() {
        final KeyerVersion initialVersion = new KeyerVersion(1, 0);

        final CommandFactory factory = new CommandFactory(initialVersion);
        final KeyerCommandQueue queue = new KeyerCommandQueue();
        final KeyerSession session = new KeyerSession();

        this.context = new InMemoryApplicationContext(factory, queue, session);
    }

    private void createReceiver() {
        final KeyerSession session = context.getSession();

        // Set session to pending state
        final HostOpenCommand command = new HostOpenCommand();
        session.handleCommand(command);

        final KeyerEventReceiver receiver = new KeyerEventReceiver(context);

        final KeyerCommandQueue queue = context.getQueue();
        this.receiver = new WinKeyReceiverThread(this.transport, queue, receiver);
        this.receiver.start();
    }

    private void createSender() {
        this.adapter = new WinKeySenderAdapter(this.context, this.transport);
    }
}