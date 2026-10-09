package de.do9fse.winkey.terminal;

import org.jline.shell.CommandSession;

import de.do9fse.winkey.lib.core.model.ApplicationContext;

public class Constants {
    public static final String APPLICATION_CONTEXT_KEY = "WinKeyContest";
    public static final String TRANSPORT_KEY = "WinKeyTransport";

    public static ApplicationContext getApplicationContext(final CommandSession session) {
        return (ApplicationContext) session.get(APPLICATION_CONTEXT_KEY);
    }
}
