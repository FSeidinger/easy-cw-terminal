package de.do9fse.cwterminal.core.model.commands;

import java.util.Objects;

public final class SideToneControlCommand extends HostModeCommand {
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

    private final boolean enablePaddleSidetoneOnly;
    private final SideToneFrequency sideToneFrequency;

    public SideToneControlCommand(final boolean enablePaddleSidetoneOnly, final SideToneFrequency sideToneFrequency) {
        this.enablePaddleSidetoneOnly = enablePaddleSidetoneOnly;
        this.sideToneFrequency = Objects.requireNonNull(sideToneFrequency, "sideToneFrequency must not be null");
    }

    public boolean isEnablePaddleSidetoneOnly() {
        return enablePaddleSidetoneOnly;
    }

    public SideToneFrequency getSideToneFrequency() {
        return sideToneFrequency;
    }

    @Override
    protected String stringifyFields() {
        return
            "enablePaddleSidetoneOnly=" + enablePaddleSidetoneOnly
            + ", sideToneFrequency=" + sideToneFrequency;
    }
}
