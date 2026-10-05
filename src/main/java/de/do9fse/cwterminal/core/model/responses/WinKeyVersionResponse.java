package de.do9fse.cwterminal.core.model.responses;

import java.util.Objects;

public record WinKeyVersionResponse(int majorVersion, int minorVersion) {
    public WinKeyVersionResponse {
        if (majorVersion < 1 || majorVersion > 3) {
            throw new IllegalArgumentException("Unsupported WinKey major version: " + majorVersion);
        }

        if (minorVersion < 0) {
            throw new IllegalArgumentException("Minor version must not be negative: " + minorVersion);
        }
    }

    public static WinKeyVersionResponse parse(final String version) {
        Objects.requireNonNull(version, "Version must not be null");

        final String[] parts = version.split("\\.", 2);
        if (parts.length != 2 || parts[0].isBlank() || parts[1].isBlank()) {
            throw new IllegalArgumentException("Version must use the notation x.y: " + version);
        }

        try {
            return new WinKeyVersionResponse(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
        } catch (final NumberFormatException exception) {
            throw new IllegalArgumentException("Version must use the notation x.y: " + version, exception);
        }
    }

    public String getVersionString() {
        return "v" + majorVersion + "." + minorVersion;
    }
}