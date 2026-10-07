package de.do9fse.cwterminal.core.model.configuration;

import javax.measure.Unit;
import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Frequency;

import tech.units.indriya.AbstractUnit;
import tech.units.indriya.unit.Units;

public final class WinKeyUnits {
    private WinKeyUnits() {}

    public static final Unit<Dimensionless> WPM = AbstractUnit.ONE.alternate("WPM");
    public static final Unit<Dimensionless> RATIO = AbstractUnit.ONE.alternate("Ratio");
    // One LPM is one pulse per minute, or 1/60 Hz.
    public static final Unit<Frequency> LPM = Units.HERTZ.divide(60);
}