package de.do9fse.cwterminal.core.model.configuration;

import java.util.Objects;

public enum KeyMode {
    IAMBIC_B("Iambic B mode"),
    IAMBIC_A("Iambic A mode"),
    ULTIMATIC("Ultimatic mode"),
    BUG_MODE("Bug mode");

    private final String description;

    KeyMode(final String description) {
        this.description = Objects.requireNonNull(description, "Description must not be null");
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return description;
    }

    public static KeyMode fromProtocol(final int value) {
        final int responseCode = ((value & 0xff) & 0b00110000) >> 4;

        return switch (responseCode) {
            case 0 -> IAMBIC_B;
            case 1 -> IAMBIC_A;
            case 2 -> ULTIMATIC;
            case 3 -> BUG_MODE;
            default -> throw new IllegalArgumentException("Unsupported key mode: " + responseCode);
        };
    }

    public int toProtocolValue() {
        return ordinal() << 4;
    }
}
