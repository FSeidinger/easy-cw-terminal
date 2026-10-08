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
class KeyCompensationTest {
    @Nested
    class Given_our_constants {
        @Test
        void Then_they_are_valid() {
            assertEquals(Quantities.getQuantity(0, MILLI(Units.SECOND)), KeyCompensation.DURATION_MIN);
            assertEquals(Quantities.getQuantity(250, MILLI(Units.SECOND)), KeyCompensation.DURATION_MAX);
        }
    }

    @Nested
    class Given_a_duration {
        @Nested
        class When_constructed {
            @CsvSource({ "0", "120", "250" })
            @ParameterizedTest(name = "should accept a duration of {0} milliseconds")
            void Then_valid_durations_are_accepted(final double value) {
                final Quantity<Time> duration = Quantities.getQuantity(value, MILLI(Units.SECOND));
                assertDoesNotThrow(() -> new KeyCompensation(duration));
            }

            @Test
            void Then_null_is_rejected() {
                final Quantity<Time> duration = null;
                final Exception actualException = assertThrows(
                    NullPointerException.class,
                    () -> new KeyCompensation(duration)
                );
                assertEquals("Duration of key compensation must not be null", actualException.getMessage());
            }

            @CsvSource({ "-0.01", "250.01" })
            @ParameterizedTest(name = "should reject a duration of {0} milliseconds")
            void Then_out_of_range_durations_are_rejected(final double value) {
                final Quantity<Time> duration = Quantities.getQuantity(value, MILLI(Units.SECOND));
                final IllegalArgumentException actualException = assertThrows(
                    IllegalArgumentException.class,
                    () -> new KeyCompensation(duration)
                );
                assertEquals(rejectedValueMessage(duration), actualException.getMessage());
            }
        }
    }

    @Nested
    class Given_a_protocol_value {
        @Nested
        class When_converting_from_protocol {
            @CsvSource({ "120, 120.0", "250, 250.0" })
            @ParameterizedTest(name = "should decode {0} as {1} milliseconds")
            void Then_the_value_is_converted_to_a_duration(final int value, final double expectedMilliseconds) {
                final double actualMilliseconds = KeyCompensation.fromProtocol(value)
                    .value()
                    .to(MILLI(Units.SECOND))
                    .getValue()
                    .doubleValue();
                assertEquals(expectedMilliseconds, actualMilliseconds);
            }

            @Test
            void Then_out_of_range_values_are_rejected() {
                assertThrows(IllegalArgumentException.class, () -> KeyCompensation.fromProtocol(251));
                assertThrows(IllegalArgumentException.class, () -> KeyCompensation.fromProtocol(-1));
            }
        }

        @Nested
        class When_converting_to_protocol {
            @CsvSource({ "120.0, 120", "120.5, 121", "250.0, 250" })
            @ParameterizedTest(name = "should encode {0} milliseconds as {1}")
            void Then_the_duration_is_converted_to_a_protocol_value(
                final double milliseconds,
                final int expectedValue
            ) {
                final KeyCompensation compensation = new KeyCompensation(
                    Quantities.getQuantity(milliseconds, MILLI(Units.SECOND))
                );
                assertEquals(expectedValue, compensation.toProtocolValue());
            }
        }
    }

    private static String rejectedValueMessage(final Quantity<Time> value) {
        return MessageFormat.format(
            "Key compensation must be between {0} and {1} but was {2}",
            KeyCompensation.DURATION_MIN,
            KeyCompensation.DURATION_MAX,
            value
        );
    }
}
