package de.do9fse.cwterminal.infrastructure.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;

import de.do9fse.cwterminal.core.model.WinKeyProtocolVersion;
import de.do9fse.cwterminal.core.model.commands.CommandInfo;
import de.do9fse.cwterminal.core.model.commands.CommandInfoRegistry;
import de.do9fse.cwterminal.core.model.error.WinKeyApplicationException;
import de.do9fse.cwterminal.core.model.responses.WinKeyVersionResponse;
import de.do9fse.cwterminal.core.model.test.suite5.AdminCommandWithResponse;
import de.do9fse.cwterminal.core.model.test.suite5.AdminCommandWithoutResponse;

public class InMemoryCommandInfoRegistryTest {
    private static final String BASE_PACKAGE = "de.do9fse.cwterminal.core.model.test";

    @Test 
    void testSuite1() throws Exception {
        final CommandInfoRegistry registry = new InMemoryCommandInfoRegistry(BASE_PACKAGE + ".suite1");
        final int registeredCommandCount = registry.getRegisteredCommandCount();

        assertEquals(0, registeredCommandCount);
    }

    @Test 
    void testSuite2() throws Exception {
        final CommandInfoRegistry registry = new InMemoryCommandInfoRegistry(BASE_PACKAGE + ".suite2");
        final int registeredCommandCount = registry.getRegisteredCommandCount();

        assertEquals(0, registeredCommandCount);
    }

    @Test
    void testSuite3() throws Exception {
        final WinKeyApplicationException e = assertThrows(WinKeyApplicationException.class, () -> new InMemoryCommandInfoRegistry(BASE_PACKAGE + ".suite3"));
        assertEquals(
            "Following command classes are missing the @SupportedProtocols annotation: de.do9fse.cwterminal.core.model.test.suite3.CommandWithoutAnnotation",
            e.getMessage()
        );
    }

    @Test
    void testSuite4() throws Exception {
        final CommandInfoRegistry registry = new InMemoryCommandInfoRegistry(BASE_PACKAGE + ".suite4");
        final int registeredCommandCount = registry.getRegisteredCommandCount();

        assertEquals(0, registeredCommandCount);
    }

    @Test
    void testSuite5() throws Exception {
        final CommandInfoRegistry registry = new InMemoryCommandInfoRegistry(BASE_PACKAGE + ".suite5");
        final int registeredCommandCount = registry.getRegisteredCommandCount();

        assertEquals(2, registeredCommandCount);

        final CommandInfo commandInfo1 = registry.getCommandInfo(AdminCommandWithoutResponse.class).get();
        assertEquals(Void.class, commandInfo1.responseType());
        assertEquals(
            Set.of(WinKeyProtocolVersion.V1, WinKeyProtocolVersion.V2, WinKeyProtocolVersion.V3),
            commandInfo1.allowedProtocolVersions()
        );

        final CommandInfo commandInfo2 = registry.getCommandInfo(AdminCommandWithResponse.class).get();
        assertEquals(WinKeyVersionResponse.class, commandInfo2.responseType());
        assertEquals(
            Set.of(WinKeyProtocolVersion.V1, WinKeyProtocolVersion.V2, WinKeyProtocolVersion.V3),
            commandInfo2.allowedProtocolVersions()
        );
    }
}
