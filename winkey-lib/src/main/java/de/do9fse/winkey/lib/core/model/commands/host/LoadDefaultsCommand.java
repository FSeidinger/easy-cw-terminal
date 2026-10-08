package de.do9fse.winkey.lib.core.model.commands.host;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Objects;

import de.do9fse.winkey.lib.core.model.WinKeyProtocolVersion;
import de.do9fse.winkey.lib.core.model.commands.CommandConfiguration;

@CommandConfiguration(allowedProtocols = WinKeyProtocolVersion.V2)
public record LoadDefaultsCommand(byte[] values) implements HostModeCommand {
    private static final int DEFAULTS_BYTES = 15;

    public LoadDefaultsCommand {
        Objects.requireNonNull(values, "Default values must not be null");
        
        if (values.length != DEFAULTS_BYTES) {
            final String message = MessageFormat.format(
                "Load defaults requires exactly {0} values but received {1}",
                DEFAULTS_BYTES,
                values.length
            );

            throw new IllegalArgumentException(message);
        }
        values = Arrays.copyOf(values, values.length);
    }

    @Override
    public byte[] values() {
        return Arrays.copyOf(values, values.length);
    }

    @Override
    public byte[] getPayloadBytes() {
        final byte[] payload = new byte[DEFAULTS_BYTES + 1];
        payload[0] = 0x0f;
        System.arraycopy(values, 0, payload, 1, DEFAULTS_BYTES);
        return payload;
    }
}
