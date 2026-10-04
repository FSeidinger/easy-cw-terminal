package de.do9fse.cwterminal.core.model.error;

public class WinKeyRuntimeException extends RuntimeException {
    public WinKeyRuntimeException(final String message) {
        super(message);
    }

    public WinKeyRuntimeException(final String message, final Throwable throwable) {
        super(message, throwable);
    }
}
