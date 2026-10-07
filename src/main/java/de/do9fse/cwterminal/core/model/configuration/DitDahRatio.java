package de.do9fse.cwterminal.core.model.configuration;

import java.util.Objects;

import javax.measure.Quantity;
import javax.measure.quantity.Dimensionless;

import tech.units.indriya.quantity.Quantities;

public record DitDahRatio(Quantity<Dimensionless> ratio) {
    public DitDahRatio {
        Objects.requireNonNull(ratio, "Ratio value must not be null");

        final Quantity<Dimensionless> inRatio = ratio.to(WinKeyUnits.RATIO);
        final double ratioValue = inRatio.getValue().doubleValue();

        if (ratioValue < 1.98 || ratioValue > 3.96) {
            throw new IllegalArgumentException(
                "Ratio must be between 1.98 and 3.96"
            );
        }

        ratio = inRatio;
    }

    public int toProtocolValue() {
        double ratioValue = ratio.getValue().doubleValue();
        return (int) Math.round((ratioValue * 50.0) / 3.0);
    }

    public static DitDahRatio parseResponseByte(final int responseByte) {
        final double ratioValue = (responseByte & 0xff) * 3.0 / 50.0;
        return new DitDahRatio(Quantities.getQuantity(ratioValue, WinKeyUnits.RATIO));
    }
}
