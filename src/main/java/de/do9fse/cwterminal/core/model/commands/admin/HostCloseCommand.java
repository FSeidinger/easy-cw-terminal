package de.do9fse.cwterminal.core.model.commands.admin;

import de.do9fse.cwterminal.core.model.WinKeyProtocolVersion;
import de.do9fse.cwterminal.core.model.commands.CommandConfiguration;

@CommandConfiguration(
    allowedProtocols = { WinKeyProtocolVersion.V1, WinKeyProtocolVersion.V2, WinKeyProtocolVersion.V3}
)
public record HostCloseCommand() implements AdminCommand<HostCloseCommand> {}
