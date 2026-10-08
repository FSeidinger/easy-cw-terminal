package de.do9fse.winkey.lib.core.model.commands.host;

import javax.measure.Quantity;
import javax.measure.quantity.Time;

import de.do9fse.winkey.lib.core.model.WinKeyProtocolVersion;
import de.do9fse.winkey.lib.core.model.commands.CommandConfiguration;
import java.util.Objects;
import tech.units.indriya.quantity.Quantities;
import tech.units.indriya.unit.Units;

@CommandConfiguration(allowedProtocols = WinKeyProtocolVersion.V2)
public record TimedKeyDownCommand(Quantity<Time> duration) implements HostModeCommand {
    public TimedKeyDownCommand {
        Objects.requireNonNull(duration, "Timed key down duration must not be null");
        
        duration = Quantities.getQuantity(
            HostCommandSupport.requireIntegralDurationSeconds("Timed key down", duration),
            Units.SECOND
        );
    }

    @Override
    public byte[] getPayloadBytes() {
        return new byte[] { 0x19, (byte) HostCommandSupport.requireIntegralDurationSeconds("Timed key down", duration) };
    }
}
