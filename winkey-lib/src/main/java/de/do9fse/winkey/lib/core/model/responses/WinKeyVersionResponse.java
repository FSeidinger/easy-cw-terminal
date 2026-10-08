package de.do9fse.winkey.lib.core.model.responses;

import java.text.MessageFormat;
import java.util.Objects;

@ResponseConfiguration(expectedResponseBytes = 1)
public record WinKeyVersionResponse(int majorVersion, int minorVersion) implements WinKeyResponse {
    public static final int MAJOR_VERSION_MIN = 1;
    public static final int MAJOR_VERSION_MAX = 3;
    public static final int MINOR_VERSION_MIN = 0;

    public static WinKeyVersionResponse fromProtocol(final byte[] responseBytes) {
        final byte versionByte = responseBytes[0];
        return new WinKeyVersionResponse(versionByte / 10, versionByte % 10);
    }

    public WinKeyVersionResponse {
        if (majorVersion < MAJOR_VERSION_MIN || majorVersion > MAJOR_VERSION_MAX) {
            final String message = MessageFormat.format(
                "WinKey major version must be between {0} and {1} but was {2}",
                MAJOR_VERSION_MIN,
                MAJOR_VERSION_MAX,
                majorVersion
            );

            throw new IllegalArgumentException(message);
        }

        if (minorVersion < MINOR_VERSION_MIN) {
            final String message = MessageFormat.format(
                "Minor version must not be less than {0} but was {1}",
                MINOR_VERSION_MIN,
                minorVersion
            );
            
            throw new IllegalArgumentException(message);
        }
    }

    public static WinKeyVersionResponse parse(final String version) {
        Objects.requireNonNull(version, "Version must not be null");

        final String[] parts = version.split("\\.", 2);
        if (parts.length != 2 || parts[0].isBlank() || parts[1].isBlank()) {
            throw new IllegalArgumentException(MessageFormat.format("Version must use the notation x.y: {0}", version));
        }

        try {
            return new WinKeyVersionResponse(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
        } catch (final NumberFormatException exception) {
            final String message = MessageFormat.format("Version must use the notation x.y: {0}", version);
            throw new IllegalArgumentException(message, exception);
        }
    }

    public String getVersionString() {
        return "v" + majorVersion + "." + minorVersion;
    }
}