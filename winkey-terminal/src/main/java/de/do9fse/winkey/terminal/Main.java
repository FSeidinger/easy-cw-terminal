package de.do9fse.winkey.terminal;

import java.io.IOException;
import java.lang.reflect.Method;

import org.jline.shell.CommandGroup;
import org.jline.shell.Shell;
import org.jline.shell.impl.SimpleCommandGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);
    
    final CommandGroup transportCommands = new SimpleCommandGroup(
        "device",
        new ConnectCommand(),
        new DisconnectCommand()
    );

    public void run() {
        setRootLogLevel("OFF");

        try (final Shell shell = Shell
            .builder()
            .groups(transportCommands)
            .prompt("cw> ")
            .build()
        ) {
            shell.run();
        } catch (final Exception e) {
            LOGGER.error("Shell failed", e);
        }
    }

    public static void main(String[] args) throws IOException {
        final Main main = new Main();
        main.run();
   }

    private void setRootLogLevel(String levelName) {
        try {
            final Logger slf4jLogger = LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
            final Class<?> levelClass = Class.forName("ch.qos.logback.classic.Level");
            
            final Method toLevelMethod = levelClass.getMethod("toLevel", String.class);
            final Object levelObject = toLevelMethod.invoke(null, levelName);

            final Method setLevelMethod = slf4jLogger.getClass().getMethod("setLevel", levelClass);
            setLevelMethod.invoke(slf4jLogger, levelObject);

        } catch (final ReflectiveOperationException e) {
            LOGGER.error("Failed to set log level", e);
        }
    }
}
