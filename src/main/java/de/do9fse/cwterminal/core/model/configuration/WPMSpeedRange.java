package de.do9fse.cwterminal.core.model.configuration;

public record WPMSpeedRange(int range) {
    public WPMSpeedRange {
        if (range < 0 || range > 99) {
            throw new IllegalArgumentException("WPM speed range must be between 0 and 99 WPM");
        }
    }

    public static WPMSpeedRange parseResponseByte(final int responseByte) {
        return new WPMSpeedRange(responseByte & 0xff);
    }
}
