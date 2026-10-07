package de.do9fse.cwterminal.core.model.responses;

import java.util.Objects;

import javax.measure.Quantity;
import javax.measure.quantity.Dimensionless;

import de.do9fse.cwterminal.core.model.configuration.WinKeyUnits;
import tech.units.indriya.quantity.Quantities;

@ResponseConfiguration(expectedResponseBytes = 1)
public record SpeedPotValueResponse(Quantity<Dimensionless> speedOffset) implements WinKeyResponse {
    public SpeedPotValueResponse {
        Objects.requireNonNull(speedOffset, "Speed pot value must not be null");
        speedOffset = speedOffset.to(WinKeyUnits.WPM);
        final double value = speedOffset.getValue().doubleValue();
        if (value < 0 || value > 31 || value != Math.rint(value)) {
            throw new IllegalArgumentException("Speed pot value must be between 0 and 31");
        }
    }

    public static SpeedPotValueResponse parseResponse(final byte[] responseBytes) {
        final int responseByte = Byte.toUnsignedInt(responseBytes[0]);
        if ((responseByte & 0b11100000) != 0b10000000) {
            throw new IllegalArgumentException("Invalid speed pot response byte: " + responseByte);
        }
        return new SpeedPotValueResponse(Quantities.getQuantity(responseByte & 0b00011111, WinKeyUnits.WPM));
    }
}
