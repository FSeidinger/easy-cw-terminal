package de.do9fse.cwterminal.core.model.configuration;

import java.util.Objects;

import javax.measure.Quantity;
import javax.measure.quantity.Dimensionless;

import tech.units.indriya.quantity.Quantities;
import tech.units.indriya.unit.Units;

public record PaddleSetpoint(Quantity<Dimensionless> percentage) {
    public PaddleSetpoint {
        Objects.requireNonNull(percentage, "Paddle setpoint must not be null");

        final Quantity<Dimensionless> inPercent = percentage.to(Units.PERCENT);
        final double percentageValue = inPercent.getValue().doubleValue();

        if (percentageValue != 0 && (percentageValue < 10 || percentageValue > 90)) {
            throw new IllegalArgumentException("Paddle setpoint must be 0 or between 10% and 90%");
        }

        percentage = inPercent;
    }

    public static PaddleSetpoint parseResponseByte(final int responseByte) {
        final int percentage = responseByte & 0xff;
        return new PaddleSetpoint(Quantities.getQuantity(percentage, Units.PERCENT));
    }
}
