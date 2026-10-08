package de.do9fse.cwterminal.core.model.commands.host;

import java.text.MessageFormat;
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
            HostCommandSupport.HSCW_SPEED_MIN_LPM,
            HostCommandSupport.HSCW_SPEED_MAX_LPM
        );

        if (lpmValue % HostCommandSupport.HSCW_SPEED_STEP_LPM != 0) {
            final String message = MessageFormat.format(
                "Buffered HSCW speed must be a multiple of {0} LPM but was {1} LPM",
                HostCommandSupport.HSCW_SPEED_STEP_LPM,
                lpmValue
            );

            throw new IllegalArgumentException(message);
        }
        
        lpm = Quantities.getQuantity(lpmValue, WinKeyUnits.LPM);
    }

    @Override
    public byte[] getPayloadBytes() {
        return new byte[] {
            0x1d,
            (byte) (lpm.getValue().intValue() / HostCommandSupport.HSCW_SPEED_STEP_LPM)
        };
    }
}
