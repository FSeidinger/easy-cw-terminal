package de.do9fse.cwterminal.core.model.commands.host;

import static javax.measure.MetricPrefix.MILLI;

import java.util.Objects;

import javax.measure.Quantity;
import javax.measure.quantity.Time;

import de.do9fse.cwterminal.core.model.WinKeyProtocolVersion;
import de.do9fse.cwterminal.core.model.commands.CommandConfiguration;
import tech.units.indriya.quantity.Quantities;
import tech.units.indriya.unit.Units;

@CommandConfiguration(allowedProtocols = WinKeyProtocolVersion.V2)
public record SetPttLeadTailCommand(Quantity<Time> leadIn, Quantity<Time> tail) implements HostModeCommand {
    public SetPttLeadTailCommand {
        Objects.requireNonNull(leadIn, "PTT lead-in must not be null");
        Objects.requireNonNull(tail, "PTT tail must not be null");
        final int leadInUnits = HostCommandSupport.requirePttDelay10Ms("PTT lead-in", leadIn);
        final int tailUnits = HostCommandSupport.requirePttDelay10Ms("PTT tail", tail);
        leadIn = Quantities.getQuantity(leadInUnits * 10, MILLI(Units.SECOND));
        tail = Quantities.getQuantity(tailUnits * 10, MILLI(Units.SECOND));
    }

    @Override
    public byte[] getPayloadBytes() {
        return new byte[] {
            0x04,
            (byte) HostCommandSupport.requirePttDelay10Ms("PTT lead-in", leadIn),
            (byte) HostCommandSupport.requirePttDelay10Ms("PTT tail", tail)
        };
    }
}
