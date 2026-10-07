package de.do9fse.cwterminal.core.model.configuration;

import javax.measure.Unit;
import javax.measure.quantity.Dimensionless;

import tech.units.indriya.AbstractUnit;

public final class WinKeyUnits {
    private WinKeyUnits() {}

    public static final Unit<Dimensionless> WPM = AbstractUnit.ONE.alternate("WPM");
    public static final Unit<Dimensionless> RATIO = AbstractUnit.ONE.alternate("Ratio");
}