package de.do9fse.cwterminal.core.port.out;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import de.do9fse.cwterminal.core.model.KeyerCommand;

/**
 * <p>
 * The contract to communicate with the keyer device.
 * </p>
 */
public interface WinKeySender {
    void initialize() throws TimeoutException;

    /**
     * Sends a keyer command to keyer device
     * 
     * @param command The keyer command to send
     * @throws IOException If sending the command fails
     */
    void sendCommand(final KeyerCommand command) throws IOException;
}