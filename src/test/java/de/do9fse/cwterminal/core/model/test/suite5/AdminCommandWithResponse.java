package de.do9fse.cwterminal.core.model.test.suite5;

import de.do9fse.cwterminal.core.model.WinKeyProtocolVersion;
import de.do9fse.cwterminal.core.model.commands.CommandInfoAnnotation;
import de.do9fse.cwterminal.core.model.commands.admin.AdminCommand;
import de.do9fse.cwterminal.core.model.responses.WinKeyVersionResponse;

@CommandInfoAnnotation(
    allowedProtocols = { WinKeyProtocolVersion.V1, WinKeyProtocolVersion.V2, WinKeyProtocolVersion.V3},
    resultType = WinKeyVersionResponse.class
)
public record AdminCommandWithResponse() implements AdminCommand {}
