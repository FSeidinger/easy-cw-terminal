package de.do9fse.cwterminal.core.model.responses;

import java.util.Arrays;

@ResponseConfiguration(expectedResponseBytes = 256)
public record EEPROMDumpResponse(byte[] data) implements WinKeyResponse {
    public EEPROMDumpResponse {
        if (data.length != 256) {
            throw new IllegalArgumentException("EEPROM dump must contain exactly 256 bytes");
        }
        
        data = Arrays.copyOf(data, data.length);
    }

    @Override
    public byte[] data() {
        return Arrays.copyOf(data, data.length);
    }

    public static EEPROMDumpResponse parseResponse(final byte[] responseBytes) {
        return new EEPROMDumpResponse(responseBytes);
    }
}
