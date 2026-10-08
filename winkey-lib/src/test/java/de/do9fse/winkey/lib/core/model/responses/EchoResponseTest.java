package de.do9fse.winkey.lib.core.model.responses;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Echo response tests")
class EchoResponseTest {

    @Test
    void parsesEchoedCharacter() {
        final EchoResponse response = EchoResponse.fromProtocol(new byte[] { (byte) 'K' });

        assertEquals('K', response.echoChar());
    }
}
