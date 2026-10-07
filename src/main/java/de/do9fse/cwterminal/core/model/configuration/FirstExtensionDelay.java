package de.do9fse.cwterminal.core.model.configuration;

import static javax.measure.MetricPrefix.MILLI;

import java.util.Objects;

import javax.measure.Quantity;
import javax.measure.quantity.Time;

import tech.units.indriya.quantity.Quantities;
import tech.units.indriya.unit.Units;

public record FirstExtensionDelay(Quantity<Time> delay) {
    public FirstExtensionDelay {
        Objects.requireNonNull(delay, "First extension delay must not be null");

        final Quantity<Time> inMS = delay.to(MILLI(Units.SECOND));
        final double mS = inMS.getValue().doubleValue();

        if (mS < 0 || mS > 250) {
            throw new IllegalArgumentException("First extension delay must be between 0 mS and 250 mS");
        }

        delay = inMS;
    }

    public static FirstExtensionDelay parseResponseByte(final int responseByte) {
        final int delayInMilliseconds = responseByte & 0xff;
        return new FirstExtensionDelay(Quantities.getQuantity(delayInMilliseconds, MILLI(Units.SECOND)));
    }
}
