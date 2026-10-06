package de.do9fse.cwterminal.core.model.commands.admin;

import de.do9fse.cwterminal.core.model.commands.WinKeyCommand;

public interface AdminCommand<R> extends WinKeyCommand<R> {
    @Override
    default byte[] getPayloadBytes() {
        return new byte[] { 0x00 };
    }
}
