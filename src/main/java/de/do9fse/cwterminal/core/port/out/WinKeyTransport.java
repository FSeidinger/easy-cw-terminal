package de.do9fse.cwterminal.core.port.out;

import java.util.concurrent.CompletableFuture;

import de.do9fse.cwterminal.core.model.WinKeyJob;
import de.do9fse.cwterminal.core.model.commands.WinKeyCommand;
import de.do9fse.cwterminal.core.model.error.WinKeyApplicationException;
import de.do9fse.cwterminal.core.model.error.WinKeyRuntimeException;
import de.do9fse.cwterminal.core.model.responses.WinKeyResponse;

public interface WinKeyTransport extends AutoCloseable {
    /**
     * Opens this transport
     *
     * <p>
     * Allocates all necessary resources for communicating with a WinKey device
     * </p>
     *
     * @throws WinKeyApplicationException If port is already open
     */
    void open() throws WinKeyApplicationException;


    /**    (non-Javadoc)
     * @throws WinKeyApplicationException If port is not open
     * @throws WinKeyRuntimeException if closing fails
     */
    void close() throws WinKeyApplicationException;

    /**
     * Submits a WinKey command to the transport's job queue
     * 
     * <p>
     * The command is expected to return a result after completion.
     * </p>
     * 
     * @param <R> The type of the expected result
     * 
     * @param command The command to be submitted
     * @param response Receives the response of this job after execution
     * @return The job to be submitted
     * @throws WinKeyApplicationException If submitting failed
     */
    WinKeyJob submitJob(final WinKeyCommand command, final CompletableFuture<WinKeyResponse> response) throws WinKeyApplicationException;
}
