package de.do9fse.winkey.lib.core.port.in;

import de.do9fse.winkey.lib.core.model.responses.WinKeyResponse;

@FunctionalInterface
public interface WinKeyUnsolicitedResponseListener {
    /**
     * Called on the serial reader thread when the device sends a response that does not belong to the active command.
     * Implementations should return quickly; listener exceptions are logged and do not stop the reader.
     *
     * @param response the decoded unsolicited response
     */
    void onUnsolicitedResponse(WinKeyResponse response);
}
