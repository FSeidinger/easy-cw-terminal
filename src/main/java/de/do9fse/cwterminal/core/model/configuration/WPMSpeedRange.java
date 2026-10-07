package de.do9fse.cwterminal.core.model.configuration;

import java.util.Objects;

import javax.measure.Quantity;
import javax.measure.quantity.Dimensionless;

import tech.units.indriya.quantity.Quantities;

public record WPMSpeedRange(Quantity<Dimensionless> range) {
    public WPMSpeedRange {
        Objects.requireNonNull(range, "WPM speed range must not be null");

        final Quantity<Dimensionless> inWpm = range.to(WinKeyUnits.WPM);
        final double rangeValue = inWpm.getValue().doubleValue();
        if (rangeValue < 0 || rangeValue > 99) {
            throw new IllegalArgumentException("WPM speed range must be between 0 and 99 WPM");
        }
        if (rangeValue != Math.rint(rangeValue)) {
            throw new IllegalArgumentException("WPM speed range must be an integer number of WPM");
        }
        range = inWpm;
    }

    public static WPMSpeedRange parseResponseByte(final int responseByte) {
        return new WPMSpeedRange(Quantities.getQuantity(responseByte & 0xff, WinKeyUnits.WPM));
    }
}
