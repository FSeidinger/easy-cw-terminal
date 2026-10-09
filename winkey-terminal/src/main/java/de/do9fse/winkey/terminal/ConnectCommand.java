package de.do9fse.winkey.terminal;

import java.io.PrintStream;
import java.text.MessageFormat;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.jline.shell.CommandSession;
import org.jline.shell.impl.AbstractCommand;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortInvalidPortException;

import de.do9fse.winkey.lib.core.model.ApplicationContext;
import de.do9fse.winkey.lib.core.model.WinKeyState;
import de.do9fse.winkey.lib.core.model.error.WinKeyApplicationException;
import de.do9fse.winkey.lib.core.model.error.WinKeyRuntimeException;
import de.do9fse.winkey.lib.core.port.out.WinKeyTransport;
import de.do9fse.winkey.lib.infrastructure.winkey.transport.serial.WinKeySerialTransport;

public class ConnectCommand extends AbstractCommand {
    protected ConnectCommand() {
        super("connect", "co" );
    }

    @Override
    public String description() {
        return "Connect to a WinKey device";
    }

    @Override
    public Object execute(final CommandSession session, String[] args) throws Exception {
        final PrintStream stdout = session.out();
        final PrintStream stderr = session.err();

        if (args.length != 1) {
            stderr.println("usage: connect <port>");
            stderr.println("  <port>    The serial port to use (e.g. COM3 or /dev/ttyUSB0)");
            return null;
        }

        final ApplicationContext applicationContext = Constants.getApplicationContext(session);
        if (applicationContext.getState() != WinKeyState.CLOSED) {
            final String message = MessageFormat.format("Device {0} is already open", applicationContext.getPortName());
            stderr.println(message);
            return null;
        } 

        final String portName = args[0];

        try {
            final SerialPort serialPort = createSerialPort(stderr, portName).orElseThrow();
            applicationContext.setPortName(portName);
            final WinKeyTransport transport = openAndInitializeTransport(stderr, applicationContext, serialPort).orElseThrow();
            applicationContext.setTransport(transport);
        } catch(final NoSuchElementException e) {
            return null;
        }
        
        final String message = MessageFormat.format("Successfully opened device {0}", portName);
        stdout.println(message);

        return null;
    }

    private Optional<SerialPort> createSerialPort(final PrintStream stderr, final String portName) {
        try {
            final SerialPort serialPort = SerialPort.getCommPort(portName);
            serialPort.setBaudRate(1200);
            serialPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING, 500, 0);

            return Optional.of(serialPort);
        } catch (final SerialPortInvalidPortException e) {
            final String message = MessageFormat.format("Invalid port {0}", portName);
            stderr.println(message);
            return Optional.ofNullable(null);
        }
    }

    private Optional<WinKeyTransport> openAndInitializeTransport(final PrintStream stderr, final ApplicationContext context, final SerialPort serialPort) {
        final WinKeyTransport transport = new WinKeySerialTransport(context, serialPort);

        try {
            transport.open();
            transport.initialize(10, TimeUnit.SECONDS);
            return Optional.of(transport);
        } catch(final TimeoutException e) {
            closeTransport(transport);
            final String message = MessageFormat.format("Device {0} did not answer - {1}", context.getPortName(), e.getMessage());
            stderr.println(message);
            return Optional.ofNullable(null);
        } catch (final WinKeyApplicationException | WinKeyRuntimeException  e) {
            closeTransport(transport);
            final String message = MessageFormat.format("Failed to initialize device {0} - {1}", context.getPortName(), e.getMessage());
            stderr.println(message);
            return Optional.ofNullable(null);
        }
    }

    private void closeTransport(final WinKeyTransport transport) {
        try {
            transport.close();
        } catch (final WinKeyApplicationException e) {
            // Silently swallow
        }
    }
}
