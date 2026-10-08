package de.do9fse.cwterminal.core.model.configuration;

import java.util.Objects;

public record ModeRegister(
    boolean isPaddleWatchdogEnabled,
    boolean isPaddleEchobackEnabled,
    KeyMode keyMode,
    boolean isPaddleSwapped,
    boolean isSerialEchobackEnabled,
    boolean isAutospaceEnabled,
    boolean isCTSpacingEnabled
) {
    public ModeRegister {
        Objects.requireNonNull(keyMode, "Key mode must not be null");
    }

    public static ModeRegister fromProtocol(final int value) {
        final int modeRegister = value & 0xff;

        return new ModeRegister(
            (modeRegister & 0b10000000) == 0,
            (modeRegister & 0b01000000) != 0,
            KeyMode.fromProtocol(modeRegister),
            (modeRegister & 0b00001000) != 0,
            (modeRegister & 0b00000100) != 0,
            (modeRegister & 0b00000010) != 0,
            (modeRegister & 0b00000001) != 0
        );
    }

    public int toProtocolValue() {
        return (isPaddleWatchdogEnabled ? 0 : 0x80)
            | (isPaddleEchobackEnabled ? 0x40 : 0)
            | keyMode.toProtocolValue()
            | (isPaddleSwapped ? 0x08 : 0)
            | (isSerialEchobackEnabled ? 0x04 : 0)
            | (isAutospaceEnabled ? 0x02 : 0)
            | (isCTSpacingEnabled ? 0x01 : 0);
    }
}
