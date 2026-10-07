package de.do9fse.cwterminal.core.model.commands;

import java.util.Set;

import de.do9fse.cwterminal.core.model.WinKeyProtocolVersion;
import de.do9fse.cwterminal.core.model.responses.WinKeyResponse;

public record CommandInfo(Class<WinKeyCommand> commandClass, Class<WinKeyResponse> responseType, Set<WinKeyProtocolVersion> allowedProtocolVersions) {
    public boolean supportsProtocol(final WinKeyProtocolVersion protocolVersion) {
        return allowedProtocolVersions.contains(protocolVersion);
    }
}
