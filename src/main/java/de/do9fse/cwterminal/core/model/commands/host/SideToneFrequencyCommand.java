package de.do9fse.cwterminal.core.model.commands.host;

import java.util.Objects;

public record SideToneFrequencyCommand(SideToneFrequency sideToneFrequency) implements HostModeCommand {
    public enum SideToneFrequency {
        FREQUENCY_3759_HZ,
        FREQUENCY_1879_HZ,
        FREQUENCY_1252_HZ,
        FREQUENCY_940_HZ,
        FREQUENCY_752_HZ,
        FREQUENCY_625_HZ,
        FREQUENCY_535_HZ,
        FREQUENCY_469_HZ,
        FREQUENCY_417_HZ,
        FREQUENCY_375_HZ
    }

    public SideToneFrequencyCommand {
        Objects.requireNonNull(sideToneFrequency, "sideToneFrequency must not be null");
    }

    @Override
    public byte[] getPayloadBytes() {
        byte sideToneFrequencyByte = switch(sideToneFrequency) {
            case FREQUENCY_3759_HZ -> (byte) 0x01;
            case FREQUENCY_1879_HZ -> (byte) 0x02;
            case FREQUENCY_1252_HZ -> (byte) 0x03;
            case FREQUENCY_940_HZ -> (byte) 0x04;
            case FREQUENCY_752_HZ -> (byte) 0x05;
            case FREQUENCY_625_HZ -> (byte) 0x06;
            case FREQUENCY_535_HZ -> (byte) 0x07;
            case FREQUENCY_469_HZ -> (byte) 0x08;
            case FREQUENCY_417_HZ -> (byte) 0x09;
            case FREQUENCY_375_HZ -> (byte) 0x0a;
        };

        return new byte[]{ 0x01, sideToneFrequencyByte };
    }
}
