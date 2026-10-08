package de.do9fse.winkey.lib.core.model.responses;

@ResponseConfiguration()
public record EmptyResponse() implements WinKeyResponse {
    public static EmptyResponse fromProtocol(final byte[] responseBytes) {
        return new EmptyResponse();
    }
}
