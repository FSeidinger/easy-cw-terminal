package de.do9fse.cwterminal.core.model.commands.admin;


import de.do9fse.cwterminal.core.model.WinKeyProtocolVersion;
import de.do9fse.cwterminal.core.model.commands.CommandConfiguration;
import de.do9fse.cwterminal.core.model.responses.WinKeyVersionResponse;

@CommandConfiguration(
    allowedProtocols = { WinKeyProtocolVersion.V1, WinKeyProtocolVersion.V2, WinKeyProtocolVersion.V3 },
    responseType = WinKeyVersionResponse.class
)
public record HostOpenCommand() implements AdminCommand<HostOpenCommand> {}
