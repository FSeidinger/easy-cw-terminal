package de.do9fse.cwterminal.core.model.commands.host;

import java.util.Objects;

import javax.measure.Quantity;
import javax.measure.quantity.Dimensionless;

import de.do9fse.cwterminal.core.model.WinKeyProtocolVersion;
import de.do9fse.cwterminal.core.model.commands.CommandConfiguration;
import de.do9fse.cwterminal.core.model.configuration.WinKeyUnits;
import tech.units.indriya.quantity.Quantities;

@CommandConfiguration(allowedProtocols = WinKeyProtocolVersion.V2)
public record SetSpeedCommand(Quantity<Dimensionless> wpm) implements HostModeCommand {
    public SetSpeedCommand {
        Objects.requireNonNull(wpm, "WPM speed must not be null");
        wpm = wpm.to(WinKeyUnits.WPM);
        final double value = wpm.getValue().doubleValue();
        if (value != 0 && (value < 5 || value > 99 || value != Math.rint(value))) {
            throw new IllegalArgumentException("WPM speed must be 0 or an integer between 5 and 99");
        }
    }

    public static SetSpeedCommand useSpeedPot() {
        return new SetSpeedCommand(Quantities.getQuantity(0, WinKeyUnits.WPM));
    }

    @Override
    public byte[] getPayloadBytes() {
        return new byte[] { 0x02, (byte) wpm.getValue().intValue() };
    }
}
