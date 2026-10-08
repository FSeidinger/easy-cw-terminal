package de.do9fse.cwterminal.core.model.configuration;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.text.MessageFormat;

import javax.measure.Quantity;
import javax.measure.quantity.Dimensionless;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import tech.units.indriya.AbstractUnit;
import tech.units.indriya.quantity.Quantities;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class DitDahRatioTests {
    @Nested
    class Given_our_constants {
        @Test
        void test_that_they_are_valid() {
            assertEquals(Quantities.getQuantity(1.98, WinKeyUnits.RATIO), DitDahRatio.RATIO_MIN);
            assertEquals(Quantities.getQuantity(3.96, WinKeyUnits.RATIO), DitDahRatio.RATIO_MAX);
            assertEquals(Quantities.getQuantity(3.00, WinKeyUnits.RATIO), DitDahRatio.DEFAULT_RATIO);
        }
    }

    @Nested
    class Given_our_formulas {
        @CsvSource({
            "33, 1.98",
            "50, 3.00", 
            "66, 3.96"
        })
        @ParameterizedTest(name = "Ratio for {0} should be {1}")
        void When_applying_the_ratio_formula(final int value, final double expected) {
            assertEquals(expected, DitDahRatio.applyRatioFormula(value));
        }

        @DisplayName("")
        @CsvSource({
            "1.98, 33",
            "3.00, 50",
            "3.96, 66"
        })
        @ParameterizedTest(name = "Protocol value for {0} should be {1}")
        void When_applying_the_reciprocal_ratio_formula(final double value, final int expected) {
            final Quantity<Dimensionless> ratio = Quantities.getQuantity(value, AbstractUnit.ONE);
            assertEquals(expected, DitDahRatio.applyReciprocalRatioFormula(ratio));
        }
    }

    @CsvSource(value = { "1.98", "2.50", "3.96" })
    @ParameterizedTest(name = "should succeed for a ratio of {0}")
    void Create_with_valid_ratio(final double value) {
        final Quantity<Dimensionless> quantity = Quantities.getQuantity(value, AbstractUnit.ONE);
        assertDoesNotThrow(() -> new DitDahRatio(quantity));
    }

    @Test
    void Creation_with_null_should_fail() {
        final Quantity<Dimensionless> quantity = null;
        final Exception actualException = assertThrows(NullPointerException.class, () -> new DitDahRatio(quantity));
        assertEquals("Ratio must not be null", actualException.getMessage());
    }

    @CsvSource(value = { "1.97", "3.97" })
    @ParameterizedTest(name = "should fail for a ration of {0}")
    void Creation_with_invalid_ratio(final double value) {
        final Quantity<Dimensionless> quantity = Quantities.getQuantity(value, WinKeyUnits.RATIO);
        final Exception actualException = assertThrows(IllegalArgumentException.class, () -> new DitDahRatio(quantity));
        assertEquals(rejectedValueMessage(quantity), actualException.getMessage());
    }

    @Test
    void Converting_from_protocol_value_should_succeed() {
        final DitDahRatio expectedRatio = new DitDahRatio(DitDahRatio.DEFAULT_RATIO);
        assertEquals(expectedRatio, DitDahRatio.fromProtocol(50));
    }

    @Test
    void Converting_from_invalid_protocol_value_should_fail() {
        assertThrows(IllegalArgumentException.class, () -> DitDahRatio.fromProtocol(0));
    }

    @Test
    void Converting_to_protocol_version_should_succeed() {
        final DitDahRatio ratio = new DitDahRatio(DitDahRatio.DEFAULT_RATIO);
        final int protocolValue = ratio.toProtocolValue();
        assertEquals(50, protocolValue);
    }

    private String rejectedValueMessage(final Quantity<Dimensionless> value) {
        return MessageFormat.format(
            "Ratio must be between {0} and {1} but was {2}",
            DitDahRatio.RATIO_MIN,
            DitDahRatio.RATIO_MAX,
            value
        );
    }
}
