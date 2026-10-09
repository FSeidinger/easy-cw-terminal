package de.do9fse.winkey.lib.core.port.out;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import de.do9fse.winkey.lib.core.model.WinKeyJob;
import de.do9fse.winkey.lib.core.model.commands.WinKeyCommand;
import de.do9fse.winkey.lib.core.model.error.WinKeyApplicationException;
import de.do9fse.winkey.lib.core.model.error.WinKeyRuntimeException;
import de.do9fse.winkey.lib.core.model.responses.WinKeyResponse;
import de.do9fse.winkey.lib.core.port.in.WinKeyUnsolicitedResponseListener;

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


    /**
     * Initializes the transport by sending an echo-test command and waiting for the
     * matching response. The command is sent again whenever a serial read times out.
     * Once the response is received, the transport starts its background reader and
     * becomes ready to accept jobs.
     *
     * <p>The transport must be in the initializing state before this method is called.
     * The serial port's read timeout should be configured to a suitable value because
     * it determines how long each read can block while this method waits.</p>
     *
     * @param timeout the maximum time to wait for the echo response
     * @param unit the time unit of {@code timeout}
     * @throws TimeoutException if the echo response is not received before the timeout
     * @throws WinKeyRuntimeException if sending the command or reading the response fails
     * @throws IllegalStateException if the transport is not initializing
     * @throws IllegalArgumentException if {@code timeout} is not positive
     * @throws NullPointerException if {@code unit} is {@code null}
     */
    void initialize(final long timeout, final TimeUnit unit) throws TimeoutException, WinKeyRuntimeException;

    /**    (non-Javadoc)
     * @throws WinKeyApplicationException If port is not open
     * @throws WinKeyRuntimeException if closing fails
     */
    void close() throws WinKeyApplicationException;

    /**
     * Registers a listener for responses sent by the device outside the active command.
     *
     * @param listener the listener to register
     */
    void addUnsolicitedResponseListener(final WinKeyUnsolicitedResponseListener listener);

    /**
     * Removes a previously registered unsolicited response listener.
     *
     * @param listener the listener to remove
     */
    void removeUnsolicitedResponseListener(final WinKeyUnsolicitedResponseListener listener);

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
