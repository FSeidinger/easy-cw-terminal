package de.do9fse.cwterminal.core.model.commands.host;

import java.util.Objects;

import javax.measure.Quantity;
import javax.measure.quantity.Dimensionless;

import de.do9fse.cwterminal.core.model.WinKeyProtocolVersion;
import de.do9fse.cwterminal.core.model.commands.CommandConfiguration;
import de.do9fse.cwterminal.core.model.configuration.WinKeyUnits;
import de.do9fse.cwterminal.core.model.configuration.WPMSpeed;
import tech.units.indriya.quantity.Quantities;

@CommandConfiguration(allowedProtocols = WinKeyProtocolVersion.V2)
public record BufferedSpeedChangeCommand(Quantity<Dimensionless> wpm) implements HostModeCommand {
    public BufferedSpeedChangeCommand {
        Objects.requireNonNull(wpm, "Buffered WPM speed must not be null");
        
        wpm = Quantities.getQuantity(HostCommandSupport.requireIntegralRange(
            "Buffered WPM speed",
            wpm,
            WinKeyUnits.WPM,
            WPMSpeed.WPM_MIN.getValue().intValue(),
            WPMSpeed.WPM_MAX.getValue().intValue()
        ), WinKeyUnits.WPM);
    }

    @Override
    public byte[] getPayloadBytes() {
        return new byte[] { 0x1c, (byte) wpm.getValue().intValue() };
    }
}
