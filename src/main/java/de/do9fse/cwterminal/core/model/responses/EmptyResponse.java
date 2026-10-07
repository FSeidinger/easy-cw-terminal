package de.do9fse.cwterminal.core.model.responses;

@ResponseConfiguration()
public record EmptyResponse() implements WinKeyResponse {
    public static EmptyResponse parseResponse(final byte[] responseBytes) {
        return new EmptyResponse();
    }
}
