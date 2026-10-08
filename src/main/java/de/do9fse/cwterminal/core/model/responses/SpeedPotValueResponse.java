package de.do9fse.cwterminal.core.model.responses;

import java.text.MessageFormat;
import java.util.Objects;

import javax.measure.Quantity;
import javax.measure.quantity.Dimensionless;

import de.do9fse.cwterminal.core.model.configuration.WinKeyUnits;
import tech.units.indriya.ComparableQuantity;
import tech.units.indriya.quantity.Quantities;

@ResponseConfiguration(expectedResponseBytes = 1)
public record SpeedPotValueResponse(Quantity<Dimensionless> speedOffset) implements WinKeyResponse {
    private static final int RESPONSE_TAG_MASK = 0b11000000;
    private static final int RESPONSE_TAG = 0b10000000;
    private static final int SPEED_OFFSET_MASK = 0b00111111;

    public static final ComparableQuantity<Dimensionless> SPEED_OFFSET_MIN = Quantities.getQuantity(0, WinKeyUnits.WPM);
    public static final ComparableQuantity<Dimensionless> SPEED_OFFSET_MAX = Quantities.getQuantity(31, WinKeyUnits.WPM);

    public SpeedPotValueResponse {
        Objects.requireNonNull(speedOffset, "Speed pot value must not be null");

        final ComparableQuantity<Dimensionless> normalizedSpeedOffset = WinKeyUnits.asWpm(speedOffset);
        if (normalizedSpeedOffset.isLessThan(SPEED_OFFSET_MIN) || normalizedSpeedOffset.isGreaterThan(SPEED_OFFSET_MAX)) {
            final String message = MessageFormat.format(
                "Speed pot value must be between {0} and {1} but was {2}",
                SPEED_OFFSET_MIN,
                SPEED_OFFSET_MAX,
                normalizedSpeedOffset
            );

            throw new IllegalArgumentException(message);
        }

        final double value = normalizedSpeedOffset.getValue().doubleValue();
        if (value != Math.rint(value)) {
            final String message = MessageFormat.format(
                "Speed pot value must be an integer but was {0}",
                normalizedSpeedOffset
            );

            throw new IllegalArgumentException(message);
        }

        speedOffset = normalizedSpeedOffset;
    }

    public static SpeedPotValueResponse fromProtocol(final byte[] responseBytes) {
        final int responseByte = Byte.toUnsignedInt(responseBytes[0]);
        if ((responseByte & RESPONSE_TAG_MASK) != RESPONSE_TAG) {
            throw new IllegalArgumentException(
                MessageFormat.format("Invalid speed pot response byte: {0}", responseByte)
            );
        }
        return new SpeedPotValueResponse(
            Quantities.getQuantity(responseByte & SPEED_OFFSET_MASK, WinKeyUnits.WPM)
        );
    }
}
