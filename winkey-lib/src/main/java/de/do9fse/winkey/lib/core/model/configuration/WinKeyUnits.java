package de.do9fse.winkey.lib.core.model.configuration;

import static javax.measure.MetricPrefix.MILLI;

import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Frequency;
import javax.measure.quantity.Time;

import tech.units.indriya.AbstractUnit;
import tech.units.indriya.ComparableQuantity;
import tech.units.indriya.quantity.Quantities;
import tech.units.indriya.unit.Units;

public final class WinKeyUnits {
    private WinKeyUnits() {}

    public static final Unit<Dimensionless> WPM = AbstractUnit.ONE.alternate("WPM");
    public static final Unit<Dimensionless> RATIO = AbstractUnit.ONE.alternate("Ratio");
    public static final Unit<Frequency> LPM = Units.HERTZ.divide(60);

    public static ComparableQuantity<Dimensionless> asWpm(final Quantity<Dimensionless> value) {
        return Quantities.getQuantity(value.getValue(), value.getUnit()).to(WPM);
    }

    public static ComparableQuantity<Dimensionless> asRatio(final Quantity<Dimensionless> value) {
        return Quantities.getQuantity(value.getValue(), value.getUnit()).to(RATIO);
    }

    public static ComparableQuantity<Dimensionless> asPercent(final Quantity<Dimensionless> value) {
        return Quantities.getQuantity(value.getValue(), value.getUnit()).to(Units.PERCENT);
    }

    public static ComparableQuantity<Time> asMilliseconds(final Quantity<Time> value) {
        return Quantities.getQuantity(value.getValue(), value.getUnit()).to(MILLI(Units.SECOND));
    }
}