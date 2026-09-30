package de.do9fse.cwterminal.core.port.out;

import de.do9fse.cwterminal.core.model.KeyerEvent;

public interface WinKeyReceiver {
    void on(final KeyerEvent event);
}
