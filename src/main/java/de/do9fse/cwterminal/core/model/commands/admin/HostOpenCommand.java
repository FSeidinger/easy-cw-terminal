package de.do9fse.cwterminal.core.model.commands.admin;

import de.do9fse.cwterminal.core.model.WinKeyProtocolVersion;
import de.do9fse.cwterminal.core.model.commands.CommandInfoAnnotation;
import de.do9fse.cwterminal.core.model.responses.WinKeyVersionResponse;

@CommandInfoAnnotation(
    allowedProtocols = { WinKeyProtocolVersion.V1, WinKeyProtocolVersion.V2, WinKeyProtocolVersion.V3 },
    resultType = WinKeyVersionResponse.class
)
public record HostOpenCommand() implements AdminCommand {}
