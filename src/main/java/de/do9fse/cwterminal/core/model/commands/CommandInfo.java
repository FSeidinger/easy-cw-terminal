package de.do9fse.cwterminal.core.model.commands;

import java.util.Set;

import de.do9fse.cwterminal.core.model.WinKeyProtocolVersion;
import de.do9fse.cwterminal.core.model.responses.EmptyResponse;

public record CommandInfo<R>(Class<WinKeyCommand<R>> commandClass, Class<R> responseType, Set<WinKeyProtocolVersion> allowedProtocolVersions) {
    public boolean hasResponse() {
        return responseType != EmptyResponse.class;
    }

    public boolean supportsProtocol(final WinKeyProtocolVersion protocolVersion) {
        return allowedProtocolVersions.contains(protocolVersion);
    }
}
