package de.do9fse.winkey.lib.core.model.responses;

@ResponseConfiguration(expectedResponseBytes = 1)
public record SpeedPotentiometerResponse(int value) implements WinKeyResponse {
    public static SpeedPotentiometerResponse fromProtocol(final byte[] responseBytes) {
        return new SpeedPotentiometerResponse(Byte.toUnsignedInt(responseBytes[0]));
    }
}
