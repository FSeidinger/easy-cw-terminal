package de.do9fse.cwterminal.core.model.configuration;

import java.text.MessageFormat;
import java.util.Objects;

import javax.measure.Quantity;
import javax.measure.quantity.Dimensionless;

import tech.units.indriya.ComparableQuantity;
import tech.units.indriya.quantity.Quantities;
import tech.units.indriya.unit.Units;

public record PaddleSetpoint(Quantity<Dimensionless> value) {
    public static final ComparableQuantity<Dimensionless> PERCENT_MIN = Quantities.getQuantity(10, Units.PERCENT);
    public static final ComparableQuantity<Dimensionless> PERCENT_MAX = Quantities.getQuantity(90, Units.PERCENT);
    public static final ComparableQuantity<Dimensionless> DISABLED = Quantities.getQuantity(0, Units.PERCENT);

    public PaddleSetpoint {
        Objects.requireNonNull(value, "Paddle setpoint must not be null");

        final ComparableQuantity<Dimensionless> inPercent = WinKeyUnits.asPercent(value);
        final double percentageValue = inPercent.getValue().doubleValue();

        if (percentageValue != 0 && (inPercent.isLessThan(PERCENT_MIN) || inPercent.isGreaterThan(PERCENT_MAX))) {
            final String message = MessageFormat.format(
                "Paddle setpoint must be {0} or between {1} and {2} but was {3}",
                DISABLED,
                PERCENT_MIN,
                PERCENT_MAX,
                inPercent
            );
            throw new IllegalArgumentException(message);
        }

        value = inPercent;
    }

    public static PaddleSetpoint fromProtocol(final int value) {
        return new PaddleSetpoint(Quantities.getQuantity(value & 0xff, Units.PERCENT));
    }

    public int toProtocolValue() {
        return (int) Math.round(value.getValue().doubleValue());
    }
}
