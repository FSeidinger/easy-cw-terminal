package de.do9fse.cwterminal.core.model.responses;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

@DisplayName("Paddle A2D response tests")
class PaddleA2DResponseTest {

    @ParameterizedTest
    @CsvSource({
        "0, BOTH_PADDLES_DOWN",
        "70, BOTH_PADDLES_DOWN",
        "71, DAH_PADDLE_DOWN",
        "103, DAH_PADDLE_DOWN",
        "104, DIT_PADDLE_DOWN",
        "151, DIT_PADDLE_DOWN",
        "152, BOTH_PADDLES_UP",
        "255, BOTH_PADDLES_UP"
    })
    void parsesAdcValueIntoPaddleState(final int adcValue, final PaddleA2DResponse.PaddleA2DState expectedState) {
        final PaddleA2DResponse response = PaddleA2DResponse.fromProtocol(new byte[] { (byte) adcValue });

        assertEquals(expectedState, response.state());
    }
}
