package de.do9fse.winkey.lib.core.model.commands.host;

import java.text.MessageFormat;

import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Frequency;
import javax.measure.quantity.Time;

import de.do9fse.winkey.lib.core.model.configuration.WinKeyUnits;
import tech.units.indriya.unit.Units;

final class HostCommandSupport {
    static final int PROTOCOL_BYTE_MIN = 0;
    static final int PROTOCOL_BYTE_MAX = 255;
    static final int DURATION_SECONDS_MIN = 0;
    static final int DURATION_SECONDS_MAX = 99;
    static final int PTT_DELAY_MILLISECONDS_MIN = 0;
    static final int PTT_DELAY_MILLISECONDS_MAX = 2500;
    static final int PTT_DELAY_STEP_MILLISECONDS = 10;
    static final int HSCW_SPEED_MIN_LPM = 1000;
    static final int HSCW_SPEED_MAX_LPM = 8000;
    static final int HSCW_SPEED_STEP_LPM = 100;

    private HostCommandSupport() {}

    static int requireRange(final String name, final int value, final int min, final int max) {
        if (value < min || value > max) {
            final String message = MessageFormat.format(
                "{0} must be between {1} and {2} but was {3}",
                name,
                min,
                max,
                value
            );

            throw new IllegalArgumentException(message);
        }
        return value;
    }

    static int requireIntegralRange(
        final String name,
        final Quantity<Dimensionless> quantity,
        final Unit<Dimensionless> unit,
        final int min,
        final int max
    ) {
        final double value = quantity.to(unit).getValue().doubleValue();
        if (value < min || value > max || value != Math.rint(value)) {
            final String message = MessageFormat.format(
                "{0} must be an integer between {1} and {2} but was {3}",
                name,
                min,
                max,
                value
            );

            throw new IllegalArgumentException(message);
        }

        return (int) value;
    }

    static int requireIntegralDurationSeconds(final String name, final Quantity<Time> duration) {
        final double seconds = duration.to(Units.SECOND).getValue().doubleValue();
        if (seconds < DURATION_SECONDS_MIN || seconds > DURATION_SECONDS_MAX || seconds != Math.rint(seconds)) {
            final String message = MessageFormat.format(
                "{0} must be an integer between {1} and {2} seconds but was {3}",
                name,
                DURATION_SECONDS_MIN,
                DURATION_SECONDS_MAX,
                seconds
            );

            throw new IllegalArgumentException(message);
        }
        return (int) seconds;
    }

    static int requireIntegralFrequencyRange(
        final String name,
        final Quantity<Frequency> quantity,
        final Unit<Frequency> unit,
        final int min,
        final int max
    ) {
        final double value = quantity.to(unit).getValue().doubleValue();
        if (value < min || value > max || value != Math.rint(value)) {
            final String message = MessageFormat.format(
                "{0} must be an integer between {1} and {2} but was {3}",
                name,
                min,
                max,
                value
            );

            throw new IllegalArgumentException(message);
        }
        return (int) value;
    }

    static int requirePttDelay10Ms(final String name, final Quantity<Time> duration) {
        final double milliseconds = WinKeyUnits.asMilliseconds(duration).getValue().doubleValue();
        if (milliseconds < PTT_DELAY_MILLISECONDS_MIN
            || milliseconds > PTT_DELAY_MILLISECONDS_MAX
            || milliseconds % PTT_DELAY_STEP_MILLISECONDS != 0) {
            final String message = MessageFormat.format(
                "{0} must be a multiple of {1} ms between {2} and {3} ms but was {4} ms",
                name,
                PTT_DELAY_STEP_MILLISECONDS,
                PTT_DELAY_MILLISECONDS_MIN,
                PTT_DELAY_MILLISECONDS_MAX,
                milliseconds
            );

            throw new IllegalArgumentException(message);
        }
        
        return (int) (milliseconds / PTT_DELAY_STEP_MILLISECONDS);
    }
}
