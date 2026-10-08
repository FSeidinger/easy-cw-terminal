package de.do9fse.winkey.lib.core.model.responses;

@ResponseConfiguration(
    expectedResponseBytes = 1
)
public record EchoResponse(char echoChar) implements WinKeyResponse {
    public static EchoResponse fromProtocol(final byte[] responseBytes) {
        return new EchoResponse((char) responseBytes[0]);
    }
}
