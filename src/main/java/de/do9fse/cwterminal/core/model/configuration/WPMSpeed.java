package de.do9fse.cwterminal.core.model.configuration;

import java.text.MessageFormat;
import java.util.Objects;

import javax.measure.Quantity;
import javax.measure.quantity.Dimensionless;

import tech.units.indriya.ComparableQuantity;
import tech.units.indriya.quantity.Quantities;

public record WPMSpeed(Quantity<Dimensionless> wpmSpeed) {
    public static final ComparableQuantity<Dimensionless> WPM_MIN = Quantities.getQuantity(5.0, WinKeyUnits.WPM);
    public static final ComparableQuantity<Dimensionless> WPM_MAX = Quantities.getQuantity(99.0, WinKeyUnits.WPM);

    public WPMSpeed {
        Objects.requireNonNull(wpmSpeed, "CW Speed value must not be null");

        final ComparableQuantity<Dimensionless> inWpm = WinKeyUnits.asWpm(wpmSpeed);

        if (inWpm.isLessThan(WPM_MIN) || inWpm.isGreaterThan(WPM_MAX)) {
            final String message = MessageFormat.format(
                "CW speed must be between {0} and {1} but was {2}",
                WPM_MIN,
                WPM_MAX,
                inWpm
            );
            throw new IllegalArgumentException(message);
        }

        wpmSpeed = inWpm;
    }

    public static WPMSpeed fromProtocol(final int value) {
        return new WPMSpeed(Quantities.getQuantity(value & 0xff, WinKeyUnits.WPM));
    }

    public int toProtocolValue() {
        return (int) Math.round(wpmSpeed.getValue().doubleValue());
    }
}
