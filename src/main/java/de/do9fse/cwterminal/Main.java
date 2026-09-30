package de.do9fse.cwterminal;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fazecast.jSerialComm.SerialPort;

import de.do9fse.cwterminal.application.KeyerEventReceiver;
import de.do9fse.cwterminal.application.KeyerUseCase;
import de.do9fse.cwterminal.core.model.KeyerCommand.OpenHostCommand;
import de.do9fse.cwterminal.core.model.KeyerSession;
import de.do9fse.cwterminal.core.port.out.SessionRepository;
import de.do9fse.cwterminal.infrastructure.persistence.inmemory.InMemorySessionRepository;
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

    private SerialTransport transport;
    private WinKeyReceiverThread receiver;

    private KeyerCommandQueue queue;
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
        this.queue = new KeyerCommandQueue();
    }

    private void createReceiver() {
        final SessionRepository repository = new InMemorySessionRepository();
        
        final KeyerSession session = repository.loadSession();
        final OpenHostCommand command = new OpenHostCommand();
        session.handleCommand(command);

        final KeyerEventReceiver receiver = new KeyerEventReceiver(repository);

        this.receiver = new WinKeyReceiverThread(this.transport, this.queue, receiver);
        this.receiver.start();
    }

    private void createSender() {
        this.adapter = new WinKeySenderAdapter(this.transport, this.queue);
    }
}