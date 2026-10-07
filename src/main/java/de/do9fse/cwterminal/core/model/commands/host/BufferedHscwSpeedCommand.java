package de.do9fse.cwterminal.core.model.commands.host;

import java.util.Objects;

import javax.measure.Quantity;
import javax.measure.quantity.Frequency;

import de.do9fse.cwterminal.core.model.WinKeyProtocolVersion;
import de.do9fse.cwterminal.core.model.commands.CommandConfiguration;
import de.do9fse.cwterminal.core.model.configuration.WinKeyUnits;
import tech.units.indriya.quantity.Quantities;

@CommandConfiguration(allowedProtocols = WinKeyProtocolVersion.V2)
public record BufferedHscwSpeedCommand(Quantity<Frequency> lpm) implements HostModeCommand {
    public BufferedHscwSpeedCommand {
        Objects.requireNonNull(lpm, "Buffered HSCW speed must not be null");
        final int lpmValue = HostCommandSupport.requireIntegralFrequencyRange(
            "Buffered HSCW speed",
            lpm,
            WinKeyUnits.LPM,
            1000,
            8000
        );
        if (lpmValue % 100 != 0) {
            throw new IllegalArgumentException("Buffered HSCW speed must be a multiple of 100 LPM");
        }
        lpm = Quantities.getQuantity(lpmValue, WinKeyUnits.LPM);
    }

    @Override
    public byte[] getPayloadBytes() {
        return new byte[] { 0x1d, (byte) (lpm.getValue().intValue() / 100) };
    }
}
