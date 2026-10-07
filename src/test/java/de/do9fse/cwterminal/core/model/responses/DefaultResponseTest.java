package de.do9fse.cwterminal.core.model.responses;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static javax.measure.MetricPrefix.MILLI;

import javax.measure.Quantity;
import javax.measure.quantity.Time;

import org.junit.jupiter.api.Test;

import de.do9fse.cwterminal.core.model.commands.host.SideToneControlCommand.SideToneFrequency;
import de.do9fse.cwterminal.core.model.configuration.KeyMode;
import de.do9fse.cwterminal.core.model.configuration.PinConfiguration;
import tech.units.indriya.unit.Units;

class DefaultResponseTest {
    @Test
    void parsesConfigurationFieldsFromDefaultsResponse() {
        final byte[] responseBytes = new byte[15];
        responseBytes[0] = 0x31;
        responseBytes[1] = 25;
        responseBytes[2] = 4;
        responseBytes[3] = 50;
        responseBytes[4] = 12;
        responseBytes[5] = 25;
        responseBytes[6] = 10;
        responseBytes[7] = 20;
        responseBytes[8] = 50;
        responseBytes[9] = 7;
        responseBytes[10] = 18;
        responseBytes[11] = 55;
        responseBytes[12] = 50;
        responseBytes[13] = (byte) 0b10011011;
        responseBytes[14] = (byte) 0xff;

        final DefaultsResponse response = DefaultsResponse.parseResponse(responseBytes);

        assertEquals(KeyMode.BUG_MODE, response.mode().keyMode());
        assertEquals(25.0, response.wpmSpeed().wpmSpeed().getValue().doubleValue());
        assertEquals(SideToneFrequency.FREQUENCY_1000_HZ, response.sideToneFrequency());
        assertEquals(50.0, response.weighting().percentage().getValue().doubleValue());
        assertEquals(12.0, delayInMilliseconds(response.leadInDelay().delay()));
        assertEquals(25.0, delayInMilliseconds(response.tailDelay().delay()));
        assertEquals(50.0, delayInMilliseconds(response.firstExtensionDelay().delay()));
        assertEquals(7.0, delayInMilliseconds(response.keyCompensation().duration()));
        assertEquals(18.0, response.farnsworthSpeed().wpmSpeed().getValue().doubleValue());
        assertEquals(55.0, response.paddleSetpoint().percentage().getValue().doubleValue());
        assertEquals(3.0, response.ditDahRatio().ratio().getValue().doubleValue());
        assertEquals(PinConfiguration.UltimaticPriority.DIT, response.pinConfiguration().ultimaticPriority());
        assertEquals(255, response.reservedValue());
    }

    @Test
    void parsesSideToneFrequencyCodes() {
        assertEquals(SideToneFrequency.FREQUENCY_4000_HZ, SideToneFrequency.parseResponseByte(1));
        assertEquals(SideToneFrequency.FREQUENCY_400_HZ, SideToneFrequency.parseResponseByte(10));
    }

    private static double delayInMilliseconds(final Quantity<Time> delay) {
        return delay.to(MILLI(Units.SECOND)).getValue().doubleValue();
    }
}
