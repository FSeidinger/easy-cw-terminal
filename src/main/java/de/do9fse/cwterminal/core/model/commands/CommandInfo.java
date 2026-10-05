package de.do9fse.cwterminal.core.model.commands;

import java.util.Set;

import de.do9fse.cwterminal.core.model.WinKeyProtocolVersion;

public record CommandInfo(Class<? extends WinKeyCommand> commandClass, Class<?> responseType, Set<WinKeyProtocolVersion> allowedProtocolVersions) {
    public boolean hasResponse() {
        return responseType != Void.class;
    }

    public boolean isAllowedForVersion(final WinKeyProtocolVersion protocolVersion) {
        return allowedProtocolVersions.contains(protocolVersion);
    }

    public static CommandInfo of(final Class<? extends WinKeyCommand> commandClass, final Set<WinKeyProtocolVersion> allowedProtocolVersions) {
        return CommandInfo.of(commandClass, Void.class, allowedProtocolVersions);
    }

    public static <R> CommandInfo of(final Class<? extends WinKeyCommand> commandClass, final Class<R> responseType, final Set<WinKeyProtocolVersion> allowedProtocolVersions) {
        return new CommandInfo(commandClass, responseType, allowedProtocolVersions);
    }
}
