package de.do9fse.cwterminal.core.model.responses;

import java.text.MessageFormat;

@ResponseConfiguration(expectedResponseBytes = 1)
public record WinKeyStatusResponse(
    boolean waiting,
    boolean keyDown,
    boolean busy,
    boolean breakIn,
    boolean xoff,
    boolean pushButtonStatus,
    int pushButtonMask
) implements WinKeyResponse {
    private static final int RESPONSE_TAG_MASK = 0b11100000;
    private static final int RESPONSE_TAG = 0b11000000;

    public static WinKeyStatusResponse fromProtocol(final byte[] responseBytes) {
        return fromProtocol(responseBytes, false);
    }

    public static WinKeyStatusResponse fromProtocol(final byte[] responseBytes, final boolean wk2Mode) {
        final int value = Byte.toUnsignedInt(responseBytes[0]);

        if ((value & RESPONSE_TAG_MASK) != RESPONSE_TAG) {
            throw new IllegalArgumentException(
                MessageFormat.format("Invalid WinKey status byte: {0}", value)
            );
        }

        final boolean isPushButtonStatus = wk2Mode && (value & 0b00001000) != 0;
        final int pushButtonMask = isPushButtonStatus
            ? ((value & 0b00010000) != 0 ? 0b1000 : 0) | (value & 0b00000111)
            : 0;

        return new WinKeyStatusResponse(
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
