package de.do9fse.cwterminal.core.model.configuration;

import java.util.Objects;

import javax.measure.Quantity;
import javax.measure.quantity.Dimensionless;

import tech.units.indriya.quantity.Quantities;

public record FarnsworthSpeed(Quantity<Dimensionless> wpmSpeed) {
    public FarnsworthSpeed {
        Objects.requireNonNull(wpmSpeed, "Farnsworth speed must not be null");

        final Quantity<Dimensionless> inWpm = wpmSpeed.to(WinKeyUnits.WPM);
        final double speedValue = inWpm.getValue().doubleValue();
        if (speedValue != 0 && (speedValue < 10 || speedValue > 99)) {
            throw new IllegalArgumentException("Farnsworth speed must be 0 or between 10 WPM and 99 WPM");
        }

        wpmSpeed = inWpm;
    }

    public static FarnsworthSpeed parseResponseByte(final int responseByte) {
        final int unsignedResponseByte = responseByte & 0xff;
        return new FarnsworthSpeed(Quantities.getQuantity(unsignedResponseByte, WinKeyUnits.WPM));
    }
}
