package de.do9fse.winkey.terminal;

import java.io.PrintStream;

import org.jline.shell.CommandSession;
import org.jline.shell.impl.AbstractCommand;

import de.do9fse.winkey.lib.core.model.ApplicationContext;
import de.do9fse.winkey.lib.core.model.WinKeyProtocolVersion;

public class SetVersionCommand extends AbstractCommand {   
    public SetVersionCommand() {
        super("set-version", "v", "sv");
    }

    @Override
    public String description() {
        return "Sets the WinKey version to be used with the keyer device";
    }

    @Override
    public Object execute(CommandSession session, String[] args) throws Exception {
        final PrintStream stdout = session.out();
        final PrintStream stderr = session.err();

        if (args.length != 1) {
            stderr.println("usage: set-version <version>");
            stderr.println("  <version>     The WinKey protocol version to use (V1, V2 or V3");
            return null;
        }

        try {
            final String versionString = args[0].toUpperCase();
            final WinKeyProtocolVersion version = WinKeyProtocolVersion.valueOf(versionString);
            final ApplicationContext context = (ApplicationContext) session.get(Constants.APPLICATION_CONTEXT_KEY);
            context.setVersion(version);

            stdout.println("Version " + version + " selected");
        } catch (final NullPointerException | IllegalArgumentException e) {
            stderr.println(e.getMessage());
        }

        return null;
    }
}
