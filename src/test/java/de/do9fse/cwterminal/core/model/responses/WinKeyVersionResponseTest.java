package de.do9fse.cwterminal.core.model.responses;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("Keyer version tests")
class WinKeyVersionResponseTest {

    @ParameterizedTest
    @ValueSource(ints = { 1, 2, 3 })
    @DisplayName("Test that valid major version is accepted")
    void acceptsValidMajorVersion(final int majorVersion) {
        final WinKeyVersionResponse version = new WinKeyVersionResponse(majorVersion, 0);
        assertEquals(majorVersion, version.majorVersion());
    }

    @ParameterizedTest
    @ValueSource(ints = { 0, 4 })
    @DisplayName("Test that invalid major version is rejected")
    void rejectsInvalidMajorVersion(final int majorVersion) {
        final IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new WinKeyVersionResponse(majorVersion, 0)
        );

        assertEquals("Unsupported WinKey major version: " + majorVersion, exception.getMessage());
    }

    @Test
    @DisplayName("Test that negative minor version is rejected")
    void rejectsNegativeMinorVersion() {
        final IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new WinKeyVersionResponse(2, -1)
        );

        assertEquals("Minor version must not be negative: -1", exception.getMessage());
    }

    @Test
    @DisplayName("Test that version can be parsed")
    void parsesMajorAndMinorVersion() {
        final WinKeyVersionResponse version = WinKeyVersionResponse.parse("2.1");

        assertEquals(2, version.majorVersion());
        assertEquals(1, version.minorVersion());
        assertEquals("v2.1", version.getVersionString());
    }

    @Test
    @DisplayName("Test that response byte can be parsed")
    void parsesResponseByte() {
        final WinKeyVersionResponse version = WinKeyVersionResponse.parseResponse(new byte[] { 23 });

        assertEquals(2, version.majorVersion());
        assertEquals(3, version.minorVersion());
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "",
        "2",
        ".1",
        "2.",
        "x.1",
        "2.x",
        "4.0",
        "2.-1"
    })
    @DisplayName("Test that malformed or unsupported version strings are rejected")
    void rejectsInvalidVersionStrings(final String value) {
        assertThrows(IllegalArgumentException.class, () -> WinKeyVersionResponse.parse(value));
    }

    @ParameterizedTest
    @NullSource
    @DisplayName("Test that null version strings are rejected")
    void rejectsNullVersionString(final String value) {
        assertThrows(NullPointerException.class, () -> WinKeyVersionResponse.parse(value));
    }
}
