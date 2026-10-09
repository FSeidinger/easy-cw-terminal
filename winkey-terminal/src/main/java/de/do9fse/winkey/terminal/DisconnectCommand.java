package de.do9fse.winkey.terminal;

import java.io.PrintStream;
import java.text.MessageFormat;

import org.jline.shell.CommandSession;
import org.jline.shell.impl.AbstractCommand;

import de.do9fse.winkey.lib.core.model.ApplicationContext;
import de.do9fse.winkey.lib.core.model.WinKeyState;
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

        final ApplicationContext applicationContext = Constants.getApplicationContext(session);
        if (applicationContext.getState() == WinKeyState.CLOSED) {
            final String message = MessageFormat.format("Device {0} is already closed", applicationContext.getPortName());
            stderr.println(message);
            return null;
        }

        try {
            final WinKeyTransport transport = applicationContext.getTransport();
            transport.close();
        } catch (final WinKeyRuntimeException | WinKeyApplicationException e) {
            final String message = MessageFormat.format("Failed to disconnect device {0} - {1}", applicationContext.getPortName(), e.getMessage());
            stderr.println(message);

            return null;
        }

        final String message = MessageFormat.format("Successfully disconnected device {0}", applicationContext.getPortName());
        stdout.println(message);

        return null;
    }
}
