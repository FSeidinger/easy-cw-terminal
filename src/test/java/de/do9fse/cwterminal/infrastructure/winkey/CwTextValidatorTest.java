package de.do9fse.cwterminal.infrastructure.winkey;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("CW text validation")
class CwTextValidatorTest {
    @Test
    @DisplayName("Test that ASCII text is accepted and converted to uppercase")
    void convertsAsciiTextToUppercase() {
        assertEquals("CQ DE DO9FSE", CwTextValidator.sanitizeAndValidate("cq de do9fse"));
    }

    @Test
    @DisplayName("Test that German umlauts and ß are converted to their ASCII equivalents")
    void convertsGermanCharactersToAscii() {
        assertEquals("AE AE OE OE UE UE SS", CwTextValidator.sanitizeAndValidate("ä Ä ö Ö ü Ü ß"));
    }

    @Test
    @DisplayName("Test that whitespace is collapsed and text is trimmed")
    void normalizesWhitespace() {
        assertEquals("CQ DE DO9FSE", CwTextValidator.sanitizeAndValidate(" \tcq  \nde do9fse\t "));
    }

    @Test
    @DisplayName("Test that null and blank text are rejected")
    void rejectsNullAndBlankText() {
        assertThrows(IllegalArgumentException.class, () -> CwTextValidator.sanitizeAndValidate(null));
        assertThrows(IllegalArgumentException.class, () -> CwTextValidator.sanitizeAndValidate(""));
        assertThrows(IllegalArgumentException.class, () -> CwTextValidator.sanitizeAndValidate(" \t\n "));
    }

    @Test
    @DisplayName("Test that control and non-ASCII characters are rejected")
    void rejectsUnsupportedCharacters() {
        final Exception exception = assertThrows(IllegalArgumentException.class, () -> CwTextValidator.sanitizeAndValidate("CQ" + (char) 0x01));
        assertEquals("CW text contains invalid characters or WinKey control bytes: CQ\u0001", exception.getMessage());

        final Exception exception2 = assertThrows(IllegalArgumentException.class, () -> CwTextValidator.sanitizeAndValidate("CQ" + (char) 0x7F));
        assertEquals("CW text contains invalid characters or WinKey control bytes: CQ\u007F", exception2.getMessage());

        final Exception exception3 = assertThrows(IllegalArgumentException.class, () -> CwTextValidator.sanitizeAndValidate("café"));
        assertEquals("CW text contains invalid characters or WinKey control bytes: café", exception3.getMessage());
    }
}
