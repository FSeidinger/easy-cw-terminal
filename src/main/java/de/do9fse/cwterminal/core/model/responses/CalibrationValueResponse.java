package de.do9fse.cwterminal.core.model.responses;

@ResponseConfiguration(expectedResponseBytes = 1)
public record CalibrationValueResponse(int value) implements WinKeyResponse {
    public static CalibrationValueResponse parseResponse(final byte[] responseBytes) {
        return new CalibrationValueResponse(Byte.toUnsignedInt(responseBytes[0]));
    }
}
