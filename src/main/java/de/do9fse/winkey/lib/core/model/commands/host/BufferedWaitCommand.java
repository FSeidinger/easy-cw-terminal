package de.do9fse.winkey.lib.core.model.commands.host;

import java.util.Objects;

import javax.measure.Quantity;
import javax.measure.quantity.Time;

import de.do9fse.winkey.lib.core.model.WinKeyProtocolVersion;
import de.do9fse.winkey.lib.core.model.commands.CommandConfiguration;
import tech.units.indriya.quantity.Quantities;
import tech.units.indriya.unit.Units;

@CommandConfiguration(allowedProtocols = WinKeyProtocolVersion.V2)
public record BufferedWaitCommand(Quantity<Time> duration) implements HostModeCommand {
    public BufferedWaitCommand {
        Objects.requireNonNull(duration, "Buffered wait duration must not be null");
        
        duration = Quantities.getQuantity(
            HostCommandSupport.requireIntegralDurationSeconds("Buffered wait", duration),
            Units.SECOND
        );
    }

    @Override
    public byte[] getPayloadBytes() {
        return new byte[] { 0x1a, (byte) HostCommandSupport.requireIntegralDurationSeconds("Buffered wait", duration) };
    }
}
