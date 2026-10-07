package de.do9fse.cwterminal.core.model.configuration;

import java.util.Objects;

import javax.measure.Quantity;
import javax.measure.quantity.Dimensionless;

import tech.units.indriya.quantity.Quantities;
import tech.units.indriya.unit.Units;

public record Weighting(Quantity<Dimensionless> percentage) {
    public Weighting {
        Objects.requireNonNull(percentage, "Weighting value must not be null");

        final Quantity<Dimensionless> inPercent = percentage.to(Units.PERCENT);
        final double percentageValue = inPercent.getValue().doubleValue();

        if (percentageValue < 10 || percentageValue > 90) {
            throw new IllegalArgumentException("Weighting percentage must be between 10 % and 90 %");
        }

        percentage = inPercent;
    }

    public static Weighting parseResponseByte(final int responseByte) {
        final int percentage = responseByte & 0xff;
        return new Weighting(Quantities.getQuantity(percentage, Units.PERCENT));
    }
}
