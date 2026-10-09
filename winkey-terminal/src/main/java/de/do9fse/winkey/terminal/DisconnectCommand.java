package de.do9fse.winkey.terminal;

import java.io.PrintStream;
import java.text.MessageFormat;

import org.jline.shell.CommandSession;
import org.jline.shell.impl.AbstractCommand;

import com.fazecast.jSerialComm.SerialPortInvalidPortException;

import de.do9fse.winkey.lib.core.model.error.WinKeyApplicationException;
import de.do9fse.winkey.lib.core.model.error.WinKeyRuntimeException;
import de.do9fse.winkey.lib.core.port.out.WinKeyTransport;

public class DisconnectCommand extends AbstractCommand {
    protected DisconnectCommand() {
        super("disconnect", "dc" );
    }

    @Override
    public String description() {
        return "disconnect the WinKey device";
    }

    @Override
    public Object execute(final CommandSession session, String[] args) throws Exception {
        final PrintStream stdout = session.out();
        final PrintStream stderr = session.err();

        if (args.length != 0) {
            stderr.println("usage: disconnect");
            return null;
        }

        try {
            final WinKeyTransport transport = (WinKeyTransport) session.get(ConnectCommand.TRANSPORT_KEY);
            if (transport == null) {
                stderr.println("Device is not open");
                return null;
            }

            transport.close();
            session.put(ConnectCommand.TRANSPORT_KEY, null);

        } catch (final SerialPortInvalidPortException e) {
            final String message = MessageFormat.format("Failed to disconnect device - {0}", e.getMessage());
            stderr.println(message);

            return null;
        } catch (final WinKeyRuntimeException | WinKeyApplicationException e) {
            final String message = MessageFormat.format("Failed to disconnect device - {0}", e.getMessage());
            stderr.println(message);

            return null;
        }

        stdout.println("Successfully disconnected device");

        return 0;
    }
}
