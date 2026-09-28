package de.do9fse.cwterminal.infrastructure.winkey;


public class CwTextValidator {
    public static String sanitizeAndValidate(String rawText) {
        if (rawText == null || rawText.isBlank()) {
            throw new IllegalArgumentException("CW text must not be null or empty");
        }

        // Convert German umlauts and ß to their ASCII equivalents and convert to uppercase
        String normalized = rawText
                .replace("Ä", "AE").replace("ä", "AE")
                .replace("Ö", "OE").replace("ö", "OE")
                .replace("Ü", "UE").replace("ü", "UE")
                .replace("ß", "SS")
                .toUpperCase();

        // Replace multiple whitespace characters with a single space and trim leading/trailing spaces
        normalized = normalized.replaceAll("\\s+", " ").strip();

        // Check if the normalized text contains only printable ASCII characters
        if (normalized.chars().anyMatch(character -> character < ' ' || character > '~')) {
            throw new IllegalArgumentException(
                "CW text contains invalid characters or WinKey control bytes: " + rawText
            );
        }

        return normalized;
    }
}