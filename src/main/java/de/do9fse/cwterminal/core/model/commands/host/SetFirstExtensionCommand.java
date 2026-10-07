package de.do9fse.cwterminal.core.model.commands.host;

import java.util.Objects;

import de.do9fse.cwterminal.core.model.WinKeyProtocolVersion;
import de.do9fse.cwterminal.core.model.commands.CommandConfiguration;
import de.do9fse.cwterminal.core.model.configuration.FirstExtensionDelay;

@CommandConfiguration(allowedProtocols = WinKeyProtocolVersion.V2)
public record SetFirstExtensionCommand(FirstExtensionDelay delay) implements HostModeCommand {
    public SetFirstExtensionCommand {
        Objects.requireNonNull(delay, "First extension delay must not be null");
    }

    @Override
    public byte[] getPayloadBytes() {
        return new byte[] { 0x10, (byte) delay.delay().getValue().intValue() };
    }
}
