package de.do9fse.cwterminal.core.model.responses;

@ResponseConfiguration(expectedResponseBytes = 1)
public record WinKeyStatusResponse(
    int rawValue,
    boolean waiting,
    boolean keyDown,
    boolean busy,
    boolean breakIn,
    boolean xoff,
    boolean pushButtonStatus,
    int pushButtonMask
) implements WinKeyResponse {
    public static WinKeyStatusResponse parseResponse(final byte[] responseBytes) {
        return parseResponse(responseBytes, false);
    }

    public static WinKeyStatusResponse parseResponse(final byte[] responseBytes, final boolean wk2Mode) {
        final int value = Byte.toUnsignedInt(responseBytes[0]);
        if ((value & 0b11100000) != 0b11000000) {
            throw new IllegalArgumentException("Invalid WinKey status byte: " + value);
        }

        final boolean isPushButtonStatus = wk2Mode && (value & 0b00001000) != 0;
        final int pushButtonMask = isPushButtonStatus
            ? ((value & 0b00010000) != 0 ? 0b1000 : 0) | (value & 0b00000111)
            : 0;
        return new WinKeyStatusResponse(
            value,
            !isPushButtonStatus && (value & 0b00010000) != 0,
            !wk2Mode && (value & 0b00001000) != 0,
            !isPushButtonStatus && (value & 0b00000100) != 0,
            !isPushButtonStatus && (value & 0b00000010) != 0,
            !isPushButtonStatus && (value & 0b00000001) != 0,
            isPushButtonStatus,
            pushButtonMask
        );
    }
}
