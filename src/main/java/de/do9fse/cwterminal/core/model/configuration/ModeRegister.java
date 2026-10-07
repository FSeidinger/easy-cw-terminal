package de.do9fse.cwterminal.core.model.configuration;

public record ModeRegister(
    boolean isPaddleWatchdogEnabled,
    boolean isPaddleEchobackEnabled,
    KeyMode keyMode,
    boolean isPaddleSwapped,
    boolean isSerialEchobackEnabled,
    boolean isAutospaceEnabled,
    boolean isCTSpacingEnabled
) {
    public static ModeRegister parseResponseByte(final int responseByte) {
        final int modeRegister = responseByte & 0xff;

        return new ModeRegister(
            (modeRegister & 0b10000000) == 0,
            (modeRegister & 0b01000000) != 0,
            KeyMode.parseResponseByte(modeRegister),
            (modeRegister & 0b00001000) != 0,
            (modeRegister & 0b00000100) != 0,
            (modeRegister & 0b00000010) != 0,
            (modeRegister & 0b00000001) != 0
        );
    }
}
