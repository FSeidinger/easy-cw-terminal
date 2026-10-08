package de.do9fse.winkey.lib.core.model.commands.host;

import java.util.Objects;

import javax.measure.Quantity;
import javax.measure.quantity.Frequency;

import de.do9fse.winkey.lib.core.model.WinKeyProtocolVersion;
import de.do9fse.winkey.lib.core.model.commands.CommandConfiguration;
import tech.units.indriya.quantity.Quantities;
import tech.units.indriya.unit.Units;

@CommandConfiguration(
    allowedProtocols = WinKeyProtocolVersion.V2
)
public record SideToneControlCommand(boolean enablePaddleSideToneOnly, SideToneFrequency sideToneFrequency) implements HostModeCommand {
    public enum SideToneFrequency {
        FREQUENCY_4000_HZ(4000),
        FREQUENCY_2000_HZ(2000),
        FREQUENCY_1333_HZ(1333),
        FREQUENCY_1000_HZ(1000),
        FREQUENCY_800_HZ(800),
        FREQUENCY_666_HZ(666),
        FREQUENCY_571_HZ(571),
        FREQUENCY_500_HZ(500),
        FREQUENCY_444_HZ(444),
        FREQUENCY_400_HZ(400);

        private final Quantity<Frequency> frequency;

        SideToneFrequency(final int frequencyHz) {
            this.frequency = Quantities.getQuantity(frequencyHz, Units.HERTZ);
        }

        public Quantity<Frequency> frequency() {
            return frequency;
        }

        public static SideToneFrequency parseResponseByte(final int responseByte) {
            final int frequencyCode = (responseByte & 0xff) & 0x0f;
            return switch (frequencyCode) {
                case 1 -> FREQUENCY_4000_HZ;
                case 2 -> FREQUENCY_2000_HZ;
                case 3 -> FREQUENCY_1333_HZ;
                case 4 -> FREQUENCY_1000_HZ;
                case 5 -> FREQUENCY_800_HZ;
                case 6 -> FREQUENCY_666_HZ;
                case 7 -> FREQUENCY_571_HZ;
                case 8 -> FREQUENCY_500_HZ;
                case 9 -> FREQUENCY_444_HZ;
                case 10 -> FREQUENCY_400_HZ;
                default -> throw new IllegalArgumentException(
                    "Unsupported side tone frequency code: " + frequencyCode
                );
            };
        }
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
