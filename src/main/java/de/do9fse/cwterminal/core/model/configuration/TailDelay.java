package de.do9fse.cwterminal.core.model.configuration;

import static javax.measure.MetricPrefix.MILLI;

import java.util.Objects;

import javax.measure.Quantity;
import javax.measure.quantity.Time;

import tech.units.indriya.quantity.Quantities;
import tech.units.indriya.unit.Units;

public record TailDelay(Quantity<Time> delay) {
    public TailDelay {
        Objects.requireNonNull(delay, "Tail delay must not be null");

        final Quantity<Time> inMS = delay.to(MILLI(Units.SECOND));
        final double mS = inMS.getValue().doubleValue();

        if (mS < 0 || mS > 250) {
            throw new IllegalArgumentException("Tail delay must be between 0 mS and 250 mS");
        }

        delay = inMS;
    }

    public static TailDelay parseResponseByte(final int responseByte) {
        final int delayInMilliseconds = (responseByte & 0xff);
        return new TailDelay(Quantities.getQuantity(delayInMilliseconds, MILLI(Units.SECOND)));
    }
}
