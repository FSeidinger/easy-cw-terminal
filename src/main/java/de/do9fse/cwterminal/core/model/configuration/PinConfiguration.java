package de.do9fse.cwterminal.core.model.configuration;

public record PinConfiguration(
    boolean isPttEnabled,
    boolean isSidetoneEnabled,
    boolean isKeyOutput2Enabled,
    boolean isKeyOutput1Enabled,
    UltimaticPriority ultimaticPriority,
    PaddleHangTime paddleHangTime
) {
    public enum UltimaticPriority {
        NORMAL,
        DAH,
        DIT,
        UNDEFINED
    }

    public enum PaddleHangTime {
        ONE_LETTERSPACE,
        ONE_AND_ONE_THIRD_LETTERSPACES,
        ONE_AND_TWO_THIRDS_LETTERSPACES,
        TWO_LETTERSPACES
    }

    public static PinConfiguration parseResponseByte(final int responseByte) {
        final int pinConfiguration = responseByte & 0xff;
        final int priorityCode = (pinConfiguration >>> 6) & 0b11;
        final int hangTimeCode = (pinConfiguration >>> 4) & 0b11;

        return new PinConfiguration(
            (pinConfiguration & 0b00000001) != 0,
            (pinConfiguration & 0b00000010) != 0,
            (pinConfiguration & 0b00000100) != 0,
            (pinConfiguration & 0b00001000) != 0,
            UltimaticPriority.values()[priorityCode],
            PaddleHangTime.values()[hangTimeCode]
        );
    }
}
