package de.do9fse.cwterminal.core.model.responses;

@ResponseConfiguration(
    expectedResponseBytes = 1
)
public record EchoResponse(char echoChar) implements WinKeyResponse {
    public static EchoResponse parseResponse(final byte[] responseBytes) {
        return new EchoResponse((char) responseBytes[0]);
    }
}
