package de.do9fse.winkey.lib.core.model.responses;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Empty response tests")
class EmptyResponseTest {

    @Test
    void parsesResponseWithoutPayload() {
        assertNotNull(EmptyResponse.fromProtocol(new byte[0]));
    }
}
