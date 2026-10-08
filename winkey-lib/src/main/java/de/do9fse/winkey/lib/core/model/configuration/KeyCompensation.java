package de.do9fse.winkey.lib.core.model.configuration;

import static javax.measure.MetricPrefix.MILLI;

import java.text.MessageFormat;
import java.util.Objects;

import javax.measure.Quantity;
import javax.measure.quantity.Time;

import tech.units.indriya.ComparableQuantity;
import tech.units.indriya.quantity.Quantities;
import tech.units.indriya.unit.Units;

public record KeyCompensation(Quantity<Time> value) {
    public static final ComparableQuantity<Time> DURATION_MIN = Quantities.getQuantity(0, MILLI(Units.SECOND));
    public static final ComparableQuantity<Time> DURATION_MAX = Quantities.getQuantity(250, MILLI(Units.SECOND));

    public KeyCompensation {
        Objects.requireNonNull(value, "Duration of key compensation must not be null");

        final ComparableQuantity<Time> inMilliseconds = WinKeyUnits.asMilliseconds(value);

        if (inMilliseconds.isLessThan(DURATION_MIN) || inMilliseconds.isGreaterThan(DURATION_MAX)) {
            final String message = MessageFormat.format(
                "Key compensation must be between {0} and {1} but was {2}",
                DURATION_MIN,
                DURATION_MAX,
                inMilliseconds
            );

            throw new IllegalArgumentException(message);
        }

        value = inMilliseconds;
    }

    public static KeyCompensation fromProtocol(final int value) {
        final int durationInMilliseconds = value & 0xff;
        return new KeyCompensation(Quantities.getQuantity(durationInMilliseconds, MILLI(Units.SECOND)));
    }

    public int toProtocolValue() {
        return (int) Math.round(value.getValue().doubleValue());
    }
}
