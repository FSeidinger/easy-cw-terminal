package de.do9fse.cwterminal.core.model.responses;

@ResponseConfiguration(expectedResponseBytes = 1)
public record PaddleA2DResponse(PaddleA2DState state) implements WinKeyResponse {
    public enum PaddleA2DState {
        BOTH_PADDLES_UP,
        DIT_PADDLE_DOWN,
        DAH_PADDLE_DOWN,
        BOTH_PADDLES_DOWN
    }

    public static PaddleA2DResponse parseResponse(final byte[] responseBytes) {
        final Integer adcValue = Byte.toUnsignedInt(responseBytes[0]);
        
        final PaddleA2DState state = switch(adcValue) {
            case Integer i when i >= 0 && i <= 70 -> PaddleA2DState.BOTH_PADDLES_DOWN;
            case Integer i when i > 70 && i <= 103 -> PaddleA2DState.DAH_PADDLE_DOWN;
            case Integer i when i > 103 && i <= 151 -> PaddleA2DState.DIT_PADDLE_DOWN;
            default -> PaddleA2DState.BOTH_PADDLES_UP;
        };

        return new PaddleA2DResponse(state);
    }
}
