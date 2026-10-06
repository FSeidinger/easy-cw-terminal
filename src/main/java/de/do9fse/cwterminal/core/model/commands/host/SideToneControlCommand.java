package de.do9fse.cwterminal.core.model.commands.host;

import java.util.Objects;

public record SideToneControlCommand(boolean enablePaddleSideToneOnly, SideToneFrequency sideToneFrequency) implements HostModeCommand<SideToneControlCommand> {
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
}
