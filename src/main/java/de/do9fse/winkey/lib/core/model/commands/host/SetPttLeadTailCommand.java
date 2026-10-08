package de.do9fse.winkey.lib.core.model.commands.host;

import static javax.measure.MetricPrefix.MILLI;

import java.util.Objects;

import javax.measure.Quantity;
import javax.measure.quantity.Time;

import de.do9fse.winkey.lib.core.model.WinKeyProtocolVersion;
import de.do9fse.winkey.lib.core.model.commands.CommandConfiguration;
import tech.units.indriya.quantity.Quantities;
import tech.units.indriya.unit.Units;

@CommandConfiguration(allowedProtocols = WinKeyProtocolVersion.V2)
public record SetPttLeadTailCommand(Quantity<Time> leadIn, Quantity<Time> tail) implements HostModeCommand {
    public SetPttLeadTailCommand {
        Objects.requireNonNull(leadIn, "PTT lead-in must not be null");

        Objects.requireNonNull(tail, "PTT tail must not be null");

        final int leadInUnits = HostCommandSupport.requirePttDelay10Ms("PTT lead-in", leadIn);

        leadIn = Quantities.getQuantity(
            leadInUnits * HostCommandSupport.PTT_DELAY_STEP_MILLISECONDS,
            MILLI(Units.SECOND)
        );

        final int tailUnits = HostCommandSupport.requirePttDelay10Ms("PTT tail", tail);
        tail = Quantities.getQuantity(
            tailUnits * HostCommandSupport.PTT_DELAY_STEP_MILLISECONDS,
            MILLI(Units.SECOND)
        );
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
