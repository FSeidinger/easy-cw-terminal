package de.do9fse.cwterminal.core.model.configuration;

import static javax.measure.MetricPrefix.MILLI;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.text.MessageFormat;

import javax.measure.Quantity;
import javax.measure.quantity.Time;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import tech.units.indriya.quantity.Quantities;
import tech.units.indriya.unit.Units;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class FirstExtensionDelayTest {
    @Nested
    class Given_our_constants {
        @Test
        void Then_they_are_valid() {
            assertEquals(Quantities.getQuantity(0, MILLI(Units.SECOND)), FirstExtensionDelay.DELAY_MIN);
            assertEquals(Quantities.getQuantity(250, MILLI(Units.SECOND)), FirstExtensionDelay.DELAY_MAX);
        }
    }

    @Nested
    class Given_a_delay {
        @Nested
        class When_constructed {
            @CsvSource({ "0", "120", "250" })
            @ParameterizedTest(name = "should accept a delay of {0} milliseconds")
            void Then_valid_delays_are_accepted(final double value) {
                final Quantity<Time> delay = Quantities.getQuantity(value, MILLI(Units.SECOND));
                assertDoesNotThrow(() -> new FirstExtensionDelay(delay));
            }

            @Test
            void Then_null_is_rejected() {
                final Quantity<Time> delay = null;
                final Exception actualException = assertThrows(
                    NullPointerException.class,
                    () -> new FirstExtensionDelay(delay)
                );
                assertEquals("First extension delay must not be null", actualException.getMessage());
            }

            @CsvSource({ "-0.01", "250.01" })
            @ParameterizedTest(name = "should reject a delay of {0} milliseconds")
            void Then_out_of_range_delays_are_rejected(final double value) {
                final Quantity<Time> delay = Quantities.getQuantity(value, MILLI(Units.SECOND));
                final IllegalArgumentException actualException = assertThrows(
                    IllegalArgumentException.class,
                    () -> new FirstExtensionDelay(delay)
                );
                assertEquals(rejectedValueMessage(delay), actualException.getMessage());
            }
        }
    }

    @Nested
    class Given_a_protocol_value {
        @Nested
        class When_converting_from_protocol {
            @CsvSource({ "120, 120.0", "250, 250.0" })
            @ParameterizedTest(name = "should decode {0} as {1} milliseconds")
            void Then_the_value_is_converted_to_a_delay(final int value, final double expectedMilliseconds) {
                final double actualMilliseconds = FirstExtensionDelay.fromProtocol(value)
                    .delay()
                    .to(MILLI(Units.SECOND))
                    .getValue()
                    .doubleValue();
                assertEquals(expectedMilliseconds, actualMilliseconds);
            }

            @Test
            void Then_out_of_range_values_are_rejected() {
                assertThrows(IllegalArgumentException.class, () -> FirstExtensionDelay.fromProtocol(251));
            }

        }

        @Nested
        class When_converting_to_protocol {
            @CsvSource({ "120.0, 120", "120.5, 121", "250.0, 250" })
            @ParameterizedTest(name = "should encode {0} milliseconds as {1}")
            void Then_the_delay_is_converted_to_a_protocol_value(
                final double milliseconds,
                final int expectedValue
            ) {
                final FirstExtensionDelay delay = new FirstExtensionDelay(
                    Quantities.getQuantity(milliseconds, MILLI(Units.SECOND))
                );
                assertEquals(expectedValue, delay.toProtocolValue());
            }
        }

    }

    private static String rejectedValueMessage(final Quantity<Time> value) {
        return MessageFormat.format(
            "First extension delay must be between {0} and {1} but was {2}",
            FirstExtensionDelay.DELAY_MIN,
            FirstExtensionDelay.DELAY_MAX,
            value
        );
    }
}
