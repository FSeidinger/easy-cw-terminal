package de.do9fse.winkey.lib.core.model.configuration;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.text.MessageFormat;

import javax.measure.Quantity;
import javax.measure.quantity.Dimensionless;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import tech.units.indriya.AbstractUnit;
import tech.units.indriya.quantity.Quantities;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class FarnsworthSpeedTest {
    @Nested
    class Given_our_constants {
        @Test
        void test_that_they_are_valid() {
            assertEquals(Quantities.getQuantity(10.0, WinKeyUnits.WPM), FarnsworthSpeed.WPM_MIN);
            assertEquals(Quantities.getQuantity(99.0, WinKeyUnits.WPM), FarnsworthSpeed.WPM_MAX);
        }
    }

    @CsvSource(value = { "10.0", "45.0", "99.0" })
    @ParameterizedTest(name = "should succeed for a farnsworth speed of {0}")
    void Create_with_valid_ratio(final double value) {
        final Quantity<Dimensionless> quantity = Quantities.getQuantity(value, AbstractUnit.ONE);
        assertDoesNotThrow(() -> new FarnsworthSpeed(quantity));
    }

    @Test
    void Creation_with_null_should_fail() {
        final Quantity<Dimensionless> quantity = null;
        final Exception actualException = assertThrows(NullPointerException.class, () -> new FarnsworthSpeed(quantity));
        assertEquals("Farnsworth speed must not be null", actualException.getMessage());
    }

    @CsvSource(value = { "9.99", "99.01" })
    @ParameterizedTest(name = "should fail for a farnsworth speed of {0}")
    void Creation_with_invalid_ratio(final double value) {
        final Quantity<Dimensionless> quantity = Quantities.getQuantity(value, WinKeyUnits.WPM);
        final Exception actualException = assertThrows(IllegalArgumentException.class, () -> new FarnsworthSpeed(quantity));
        assertEquals(rejectedValueMessage(quantity), actualException.getMessage());
    }

    @Test
    void Converting_from_protocol_value_should_succeed() {
        final FarnsworthSpeed expected = new FarnsworthSpeed(FarnsworthSpeed.WPM_MIN);
        assertEquals(expected, FarnsworthSpeed.fromProtocol(10));
    }

    @Test
    void Converting_from_invalid_protocol_value_should_fail() {
        assertThrows(IllegalArgumentException.class, () -> FarnsworthSpeed.fromProtocol(0));
    }

    @Test
    void Converting_to_protocol_version_should_succeed() {
        final FarnsworthSpeed value = new FarnsworthSpeed(FarnsworthSpeed.WPM_MIN);
        final int protocolValue = value.toProtocolValue();
        assertEquals(10, protocolValue);
    }

    private String rejectedValueMessage(final Quantity<Dimensionless> value) {
        return MessageFormat.format(
            "Farnsworth speed must be between {0} and {1} but was {2}",
            FarnsworthSpeed.WPM_MIN,
            FarnsworthSpeed.WPM_MAX,
            value
        );
    }
}
