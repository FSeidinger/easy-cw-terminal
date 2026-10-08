package de.do9fse.cwterminal.core.model.configuration;

import java.text.MessageFormat;
import java.util.Objects;

import javax.measure.Quantity;
import javax.measure.quantity.Dimensionless;

import tech.units.indriya.ComparableQuantity;
import tech.units.indriya.quantity.Quantities;

public record FarnsworthSpeed(Quantity<Dimensionless> value) {
    public static final ComparableQuantity<Dimensionless> WPM_MIN = Quantities.getQuantity(10.0, WinKeyUnits.WPM);
    public static final ComparableQuantity<Dimensionless> WPM_MAX = Quantities.getQuantity(99.0, WinKeyUnits.WPM);

    public FarnsworthSpeed {
        Objects.requireNonNull(value, "Farnsworth speed must not be null");

        final ComparableQuantity<Dimensionless> wpmSpeed = WinKeyUnits.asWpm(value);

        if (wpmSpeed.isLessThan(WPM_MIN) || wpmSpeed.isGreaterThan(WPM_MAX)) {
            final String message = MessageFormat.format(
                "Farnsworth speed must be between {0} and {1} but was {2}",
                WPM_MIN,
                WPM_MAX,
                wpmSpeed
            );

            throw new IllegalArgumentException(message);
        }

        value = wpmSpeed;
    }

    public static FarnsworthSpeed fromProtocol(final int value) {
        return new FarnsworthSpeed(Quantities.getQuantity(value, WinKeyUnits.WPM));
    }

    public int toProtocolValue() {
        return (int) Math.round(value.getValue().doubleValue());
    }
}
