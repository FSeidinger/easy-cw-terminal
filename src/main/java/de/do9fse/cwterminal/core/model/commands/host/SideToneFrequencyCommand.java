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
}
