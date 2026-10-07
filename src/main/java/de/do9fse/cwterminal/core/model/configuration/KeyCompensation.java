package de.do9fse.cwterminal.core.model.configuration;

import static javax.measure.MetricPrefix.MILLI;

import java.util.Objects;

import javax.measure.Quantity;
import javax.measure.quantity.Time;

import tech.units.indriya.quantity.Quantities;
import tech.units.indriya.unit.Units;

public record KeyCompensation(Quantity<Time> duration) {
    public KeyCompensation {
        Objects.requireNonNull(duration, "Duration of key compensation must not be null");

        final Quantity<Time> inMS = duration.to(MILLI(Units.SECOND));
        final double mS = inMS.getValue().doubleValue();

        if (mS < 0 || mS > 250) {
            throw new IllegalArgumentException("Key compensation must be between 0 mS and 250 mS");
        }

        duration = inMS;
    }

    public static KeyCompensation parseResponseByte(final int responseByte) {
        final int durationInMilliseconds = responseByte & 0xff;
        return new KeyCompensation(Quantities.getQuantity(durationInMilliseconds, MILLI(Units.SECOND)));
    }
}
