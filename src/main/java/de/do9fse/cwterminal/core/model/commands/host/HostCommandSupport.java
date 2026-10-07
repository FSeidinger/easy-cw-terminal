package de.do9fse.cwterminal.core.model.commands.host;

import static javax.measure.MetricPrefix.MILLI;

import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Frequency;
import javax.measure.quantity.Time;

import tech.units.indriya.unit.Units;

final class HostCommandSupport {
    private HostCommandSupport() {}

    static int requireRange(final String name, final int value, final int min, final int max) {
        if (value < min || value > max) {
            throw new IllegalArgumentException(name + " must be between " + min + " and " + max);
        }
        return value;
    }

    static int requireIntegralRange(
        final String name,
        final Quantity<Dimensionless> quantity,
        final Unit<Dimensionless> unit,
        final int min,
        final int max
    ) {
        final double value = quantity.to(unit).getValue().doubleValue();
        if (value < min || value > max || value != Math.rint(value)) {
            throw new IllegalArgumentException(name + " must be an integer between " + min + " and " + max);
        }
        return (int) value;
    }

    static int requireIntegralDurationSeconds(final String name, final Quantity<Time> duration) {
        final double seconds = duration.to(Units.SECOND).getValue().doubleValue();
        if (seconds < 0 || seconds > 99 || seconds != Math.rint(seconds)) {
            throw new IllegalArgumentException(name + " must be an integer between 0 and 99 seconds");
        }
        return (int) seconds;
    }

    static int requireIntegralFrequencyRange(
        final String name,
        final Quantity<Frequency> quantity,
        final Unit<Frequency> unit,
        final int min,
        final int max
    ) {
        final double value = quantity.to(unit).getValue().doubleValue();
        if (value < min || value > max || value != Math.rint(value)) {
            throw new IllegalArgumentException(name + " must be an integer between " + min + " and " + max);
        }
        return (int) value;
    }

    static int requirePttDelay10Ms(final String name, final Quantity<Time> duration) {
        final double milliseconds = duration.to(MILLI(Units.SECOND)).getValue().doubleValue();
        if (milliseconds < 0 || milliseconds > 2500 || milliseconds % 10 != 0) {
            throw new IllegalArgumentException(name + " must be a multiple of 10 ms between 0 and 2500 ms");
        }
        return (int) (milliseconds / 10);
    }
}
