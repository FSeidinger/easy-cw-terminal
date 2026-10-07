package de.do9fse.cwterminal.core.model.responses;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Echo response tests")
class EchoResponseTest {

    @Test
    void parsesEchoedCharacter() {
        final EchoResponse response = EchoResponse.parseResponse(new byte[] { (byte) 'K' });

        assertEquals('K', response.echoChar());
    }
}
