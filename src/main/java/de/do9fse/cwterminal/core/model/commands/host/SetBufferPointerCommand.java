package de.do9fse.cwterminal.core.model.commands.host;

import java.util.Objects;

import de.do9fse.cwterminal.core.model.WinKeyProtocolVersion;
import de.do9fse.cwterminal.core.model.commands.CommandConfiguration;

@CommandConfiguration(allowedProtocols = WinKeyProtocolVersion.V2)
public record SetBufferPointerCommand(Operation operation, int positionOrNullCount) implements HostModeCommand {
    public enum Operation {
        RESET,
        OVERWRITE,
        INSERT,
        ADD_NULLS
    }

    public SetBufferPointerCommand {
        Objects.requireNonNull(operation, "Buffer pointer operation must not be null");
        if (operation == Operation.RESET) {
            if (positionOrNullCount != -1) {
                throw new IllegalArgumentException("Reset operation does not accept a position or null count");
            }
        } else {
            HostCommandSupport.requireRange("Buffer pointer position or null count", positionOrNullCount, 0, 255);
        }
    }

    public static SetBufferPointerCommand reset() {
        return new SetBufferPointerCommand(Operation.RESET, -1);
    }

    @Override
    public byte[] getPayloadBytes() {
        if (operation == Operation.RESET) {
            return new byte[] { 0x16, 0x00 };
        }
        return new byte[] { 0x16, (byte) operation.ordinal(), (byte) positionOrNullCount };
    }
}
