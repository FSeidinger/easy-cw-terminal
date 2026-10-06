package de.do9fse.cwterminal.core.model.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.do9fse.cwterminal.core.model.WinKeyProtocolVersion;
import de.do9fse.cwterminal.core.model.commands.test.suite3.CommandWithoutAnnotation;
import de.do9fse.cwterminal.core.model.commands.test.suite5.AdminCommandWithResponse;
import de.do9fse.cwterminal.core.model.commands.test.suite5.AdminCommandWithoutResponse;
import de.do9fse.cwterminal.core.model.error.WinKeyRuntimeException;
import de.do9fse.cwterminal.core.model.responses.EmptyResponse;
import de.do9fse.cwterminal.core.model.responses.WinKeyVersionResponse;

public class WinKeyCommandTest {
    private String BASE_PACKAGE;

    @BeforeEach
    void setUp() {
        this.BASE_PACKAGE = this.getClass().getPackage().getName() + ".test";
    }

    @Test 
    void testSuite1() throws Exception {
        @SuppressWarnings("rawtypes")
        final List<Class<WinKeyCommand>> validCommandClasses = WinKeyCommand.validateCommands(BASE_PACKAGE + ".suite1");
        assertEquals(0, validCommandClasses.size());
    }

    @Test 
    void testSuite2() throws Exception {
        @SuppressWarnings("rawtypes")
        final List<Class<WinKeyCommand>> validCommandClasses = WinKeyCommand.validateCommands(BASE_PACKAGE + ".suite2");
        assertEquals(0, validCommandClasses.size());
    }

    @Test
    void testSuite3() throws Exception {
        final WinKeyRuntimeException e = assertThrows(WinKeyRuntimeException.class, () -> WinKeyCommand.validateCommands(BASE_PACKAGE + ".suite3"));
        assertEquals(
            "Following command classes are missing the @SupportedProtocols annotation: " + CommandWithoutAnnotation.class.getName(),
            e.getMessage()
        );
    }

    @Test
    void testSuite4() throws Exception {
        @SuppressWarnings("rawtypes")
        final List<Class<WinKeyCommand>> validCommandClasses = WinKeyCommand.validateCommands(BASE_PACKAGE + ".suite4");
        assertEquals(0, validCommandClasses.size());
    }

    @Test
    void testSuite5() throws Exception {
        @SuppressWarnings("rawtypes")
        final List<Class<WinKeyCommand>> validCommandClasses = WinKeyCommand.validateCommands(BASE_PACKAGE + ".suite5");
        assertEquals(2, validCommandClasses.size());

        final CommandInfo<?> commandWithoutResponseInfo = new AdminCommandWithoutResponse().getCommandInfo();
        assertEquals(EmptyResponse.class, commandWithoutResponseInfo.responseType());
        assertEquals(
            Set.of(WinKeyProtocolVersion.V1, WinKeyProtocolVersion.V2, WinKeyProtocolVersion.V3),
            commandWithoutResponseInfo.allowedProtocolVersions()
        );

        final CommandInfo<?> commandWithResponseInfo = new AdminCommandWithResponse().getCommandInfo();
        assertEquals(WinKeyVersionResponse.class, commandWithResponseInfo.responseType());
        assertEquals(
            Set.of(WinKeyProtocolVersion.V1, WinKeyProtocolVersion.V2, WinKeyProtocolVersion.V3),
            commandWithResponseInfo.allowedProtocolVersions()
        );
    }
}
