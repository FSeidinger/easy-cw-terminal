package de.do9fse.winkey.terminal;

import java.io.PrintStream;
import java.text.MessageFormat;

import org.jline.shell.CommandSession;
import org.jline.shell.impl.AbstractCommand;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortInvalidPortException;

import de.do9fse.winkey.lib.core.model.error.WinKeyApplicationException;
import de.do9fse.winkey.lib.core.model.error.WinKeyRuntimeException;
import de.do9fse.winkey.lib.core.port.out.WinKeyTransport;
import de.do9fse.winkey.lib.infrastructure.winkey.transport.serial.WinKeySerialTransport;

public class ConnectCommand extends AbstractCommand {
    public static final String TRANSPORT_KEY = "WinKeyTransport";

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

        final String portName = args[0];

        try {
            if (session.get(TRANSPORT_KEY) != null) {
                stderr.println("Device is already open");
                return null;
            }

            final SerialPort serialPort = SerialPort.getCommPort(portName);
            serialPort.setBaudRate(1200);
            serialPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING, 1000, 0);

            final WinKeyTransport transport = new WinKeySerialTransport(serialPort);
            
            transport.open();
            session.put(TRANSPORT_KEY, transport);
        } catch (final SerialPortInvalidPortException e) {
            final String message = MessageFormat.format("Invalid port {0}", portName);
            stderr.println(message);

            return null;
        } catch (final WinKeyRuntimeException | WinKeyApplicationException e) {
            final String message = MessageFormat.format("Failed to open device {0} - {1}", portName, e.getMessage());
            stderr.println(message);

            return null;
        }

        final String message = MessageFormat.format("Successfully opened {0}", portName);
        stdout.println(message);

        return 0;
    }
}
