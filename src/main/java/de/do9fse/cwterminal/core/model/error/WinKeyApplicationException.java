package de.do9fse.cwterminal.core.model.error;

public class WinKeyApplicationException extends Exception {
    public WinKeyApplicationException(final String message) {
        super(message);
    }

    public WinKeyApplicationException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
