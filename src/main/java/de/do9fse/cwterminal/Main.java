package de.do9fse.cwterminal;

import java.io.IOException;

import de.do9fse.cwterminal.core.model.KeyerCommand;
import de.do9fse.cwterminal.core.port.out.WinKeyTransport;
import de.do9fse.cwterminal.infrastructure.winkey.SerialTransport;
import de.do9fse.cwterminal.infrastructure.winkey.SerialTransportConfig;
import de.do9fse.cwterminal.infrastructure.winkey.WinKeyAdapter;

public final class Main {
    private Main() {
    }

    public static void main(final String[] args) throws IOException {
        if (args.length != 1) {
            System.err.println("Usage: easy-cw-terminal <serial-port>");
            return;
        }

        final SerialTransportConfig config = new SerialTransportConfig(args[0]);
        final WinKeyTransport transport = new SerialTransport(config);
        final WinKeyAdapter adapter = new WinKeyAdapter(transport);

        System.out.println("WinKey adapter configured for serial port " + config.portName());

        adapter.open();
        adapter.sendCommand(new KeyerCommand.OpenHostCommand());
        adapter.close();
    }
}