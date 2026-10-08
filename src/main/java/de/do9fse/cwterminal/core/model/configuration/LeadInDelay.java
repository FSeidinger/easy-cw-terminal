package de.do9fse.cwterminal.core.model.configuration;

import static javax.measure.MetricPrefix.MILLI;

import java.text.MessageFormat;
import java.util.Objects;

import javax.measure.Quantity;
import javax.measure.quantity.Time;

import tech.units.indriya.ComparableQuantity;
import tech.units.indriya.quantity.Quantities;
import tech.units.indriya.unit.Units;

public record LeadInDelay(Quantity<Time> value) {
    public static final ComparableQuantity<Time> DELAY_MIN = Quantities.getQuantity(0, MILLI(Units.SECOND));
    public static final ComparableQuantity<Time> DELAY_MAX = Quantities.getQuantity(250, MILLI(Units.SECOND));

    public LeadInDelay {
        Objects.requireNonNull(value, "Lead in delay must not be null");

        final ComparableQuantity<Time> inMilliseconds = WinKeyUnits.asMilliseconds(value);

        if (inMilliseconds.isLessThan(DELAY_MIN) || inMilliseconds.isGreaterThan(DELAY_MAX)) {
            final String message = MessageFormat.format(
                "Lead-in delay must be between {0} and {1} but was {2}",
                DELAY_MIN,
                DELAY_MAX,
                inMilliseconds
            );
            throw new IllegalArgumentException(message);
        }

        value = inMilliseconds;
    }

    public static LeadInDelay fromProtocol(final int value) {
        final int delayInMilliseconds = value & 0xff;
        return new LeadInDelay(Quantities.getQuantity(delayInMilliseconds, MILLI(Units.SECOND)));
    }

    public int toProtocolValue() {
        return (int) Math.round(value.getValue().doubleValue());
    }
}
