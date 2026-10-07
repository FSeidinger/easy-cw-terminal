package de.do9fse.cwterminal.core.model.commands.host;

import java.util.Objects;

public record SideToneControlCommand(boolean enablePaddleSideToneOnly, SideToneFrequency sideToneFrequency) implements HostModeCommand {
    public enum SideToneFrequency {
        FREQUENCY_4000_HZ,
        FREQUENCY_2000_HZ,
        FREQUENCY_1333_HZ,
        FREQUENCY_1000_HZ,
        FREQUENCY_800_HZ,
        FREQUENCY_666_HZ,
        FREQUENCY_571_HZ,
        FREQUENCY_500_HZ,
        FREQUENCY_444_HZ,
        FREQUENCY_400_HZ
    }

    public SideToneControlCommand {
        Objects.requireNonNull(sideToneFrequency, "Side tone frequency must not be null");
    }

    @Override
    public byte[] getPayloadBytes() {
        byte sideToneControlByte = switch(sideToneFrequency) {
            case FREQUENCY_4000_HZ -> (byte) 0x01;
            case FREQUENCY_2000_HZ -> (byte) 0x02;
            case FREQUENCY_1333_HZ -> (byte) 0x03;
            case FREQUENCY_1000_HZ -> (byte) 0x04;
            case FREQUENCY_800_HZ -> (byte) 0x05;
            case FREQUENCY_666_HZ -> (byte) 0x06;
            case FREQUENCY_571_HZ -> (byte) 0x07;
            case FREQUENCY_500_HZ -> (byte) 0x08;
            case FREQUENCY_444_HZ -> (byte) 0x09;
            case FREQUENCY_400_HZ -> (byte) 0x0a;
        };

        if (enablePaddleSideToneOnly) {
            sideToneControlByte = (byte) (sideToneControlByte | 0x80);
        }

        return new byte[]{ 0x01, sideToneControlByte };
    }
}
