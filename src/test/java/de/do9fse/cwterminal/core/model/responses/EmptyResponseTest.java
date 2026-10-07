package de.do9fse.cwterminal.core.model.responses;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Empty response tests")
class EmptyResponseTest {

    @Test
    void parsesResponseWithoutPayload() {
        assertNotNull(EmptyResponse.parseResponse(new byte[0]));
    }
}
