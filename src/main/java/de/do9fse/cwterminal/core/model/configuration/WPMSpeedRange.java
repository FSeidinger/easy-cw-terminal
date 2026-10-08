package de.do9fse.cwterminal.core.model.configuration;

import java.text.MessageFormat;
import java.util.Objects;

import javax.measure.Quantity;
import javax.measure.quantity.Dimensionless;

import tech.units.indriya.ComparableQuantity;
import tech.units.indriya.quantity.Quantities;

public record WPMSpeedRange(Quantity<Dimensionless> value) {
    public static final ComparableQuantity<Dimensionless> RANGE_MIN = Quantities.getQuantity(0, WinKeyUnits.WPM);
    public static final ComparableQuantity<Dimensionless> RANGE_MAX = Quantities.getQuantity(99, WinKeyUnits.WPM);

    public WPMSpeedRange {
        Objects.requireNonNull(value, "WPM speed range must not be null");

        final ComparableQuantity<Dimensionless> inWpm = WinKeyUnits.asWpm(value);

        if (inWpm.isLessThan(RANGE_MIN) || inWpm.isGreaterThan(RANGE_MAX)) {
            final String message = MessageFormat.format(
                "WPM speed range must be between {0} and {1} but was {2}",
                RANGE_MIN,
                RANGE_MAX,
                inWpm
            );

            throw new IllegalArgumentException(message);
        }

        if (inWpm.getValue().doubleValue() != Math.rint(inWpm.getValue().doubleValue())) {
            throw new IllegalArgumentException("WPM speed range must be an integer number of WPM");
        }

        value = inWpm;
    }

    public static WPMSpeedRange fromProtocol(final int value) {
        return new WPMSpeedRange(Quantities.getQuantity(value & 0xff, WinKeyUnits.WPM));
    }

    public int toProtocolValue() {
        return (int) Math.round(value.getValue().doubleValue());
    }
}
