package de.do9fse.cwterminal.core.model.configuration;

import java.text.MessageFormat;
import java.util.Objects;

import javax.measure.Quantity;
import javax.measure.quantity.Dimensionless;

import tech.units.indriya.ComparableQuantity;
import tech.units.indriya.quantity.Quantities;

public record DitDahRatio(Quantity<Dimensionless> value) {
    public static final double MULTIPLIER = 3.0;
    public static final double DIVISOR = 50.0;

    public static final ComparableQuantity<Dimensionless> RATIO_MIN = Quantities.getQuantity(1.98, WinKeyUnits.RATIO);
    public static final ComparableQuantity<Dimensionless> RATIO_MAX = Quantities.getQuantity(3.96, WinKeyUnits.RATIO);
    public static final ComparableQuantity<Dimensionless> DEFAULT_RATIO = Quantities.getQuantity(3.0, WinKeyUnits.RATIO);

    public DitDahRatio {
        Objects.requireNonNull(value, "Ratio must not be null");

        final ComparableQuantity<Dimensionless> ratio = WinKeyUnits.asRatio(value);

        if (ratio.isLessThan(RATIO_MIN) || ratio.isGreaterThan(RATIO_MAX)) {
            final String message = MessageFormat.format(
                "Ratio must be between {0} and {1} but was {2}",
                RATIO_MIN,
                RATIO_MAX,
                ratio
            );

            throw new IllegalArgumentException(message);
        }

        value = ratio;
    }

    public static DitDahRatio fromProtocol(final int value) {
        return new DitDahRatio(Quantities.getQuantity(applyRatioFormula(value), WinKeyUnits.RATIO));
    }

    public static double applyRatioFormula(final int value) {
        return MULTIPLIER * ((double) value / DIVISOR);
    }

    public static double applyReciprocalRatioFormula(final Quantity<Dimensionless> ratio) {
       return DIVISOR * (ratio.getValue().doubleValue() / MULTIPLIER);
    }

    public int toProtocolValue() {
        return (int) Math.round(applyReciprocalRatioFormula(value));
    }
}
