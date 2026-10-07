package de.do9fse.cwterminal.core.model.configuration;

import java.util.Objects;

import javax.measure.Quantity;
import javax.measure.quantity.Dimensionless;

import tech.units.indriya.AbstractUnit;
import tech.units.indriya.quantity.Quantities;

public record WPMSpeed(Quantity<Dimensionless> wpmSpeed) {
    public WPMSpeed {
        Objects.requireNonNull(wpmSpeed, "CW Speed value must not be null");

        final Quantity<Dimensionless> inWpm = wpmSpeed.to(WinKeyUnits.WPM);
        final double speedValue = inWpm.getValue().doubleValue();

        if (speedValue < 5.0 || speedValue > 99.0) {
            throw new IllegalArgumentException(
                "The speed value must be between 5 WPM and 99 WPM"
            );
        }

        wpmSpeed = inWpm;
    }

    public static WPMSpeed parseResponseByte(final int responseByte) {
        final int unsignedResponseByte = responseByte & 0xff;
        final Quantity<Dimensionless> wpmSpeed = Quantities.getQuantity(unsignedResponseByte, AbstractUnit.ONE);
        return new WPMSpeed(wpmSpeed);
    }
}
