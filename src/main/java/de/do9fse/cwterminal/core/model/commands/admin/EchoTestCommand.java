package de.do9fse.cwterminal.core.model.commands.admin;

import java.text.MessageFormat;

import de.do9fse.cwterminal.core.model.WinKeyProtocolVersion;
import de.do9fse.cwterminal.core.model.commands.CommandConfiguration;
import de.do9fse.cwterminal.core.model.responses.EchoResponse;

@CommandConfiguration(
    allowedProtocols = { WinKeyProtocolVersion.V1, WinKeyProtocolVersion.V2 },
    responseType = EchoResponse.class
)
public record EchoTestCommand(char echoChar) implements AdminCommand {
    private static final char MIN_PRINTABLE_ASCII = 0x20;
    private static final char MAX_PRINTABLE_ASCII = 0x7E;

    public EchoTestCommand {
        if (echoChar < MIN_PRINTABLE_ASCII || echoChar > MAX_PRINTABLE_ASCII) {
            final String message = MessageFormat.format(
                "Echo character must be printable ASCII between {0} and {1} but was {2}",
                (int) MIN_PRINTABLE_ASCII,
                (int) MAX_PRINTABLE_ASCII,
                (int) echoChar
            );
            
            throw new IllegalArgumentException(message);
        }
    }

    @Override
    public byte[] getPayloadBytes() {
        return new byte[]{ 0x00, 4, (byte) echoChar };
    }
}
