package de.do9fse.cwterminal.infrastructure.winkey.transport.serial;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PushbackInputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.junit.jupiter.api.Test;

import com.fazecast.jSerialComm.SerialPort;

import de.do9fse.cwterminal.core.model.WinKeyJob;
import de.do9fse.cwterminal.core.model.commands.WinKeyCommand;
import de.do9fse.cwterminal.core.model.commands.admin.HostOpenCommand;
import de.do9fse.cwterminal.core.model.commands.admin.SetWK1ModeCommand;
import de.do9fse.cwterminal.core.model.commands.admin.SetWK2ModeCommand;
import de.do9fse.cwterminal.core.model.commands.host.BackspaceCommand;
import de.do9fse.cwterminal.core.model.responses.SpeedPotValueResponse;
import de.do9fse.cwterminal.core.model.responses.WinKeyResponse;
import de.do9fse.cwterminal.core.model.responses.WinKeyStatusResponse;

class WinKeySerialTransportTest {
    @Test
    void completesCommandsWithoutResponseImmediatelyAndDoesNotConsumeNextResponseJob() throws Exception {
        final WinKeySerialTransport transport = createTransport(0x23);
        final CompletableFuture<WinKeyResponse> noResponse = new CompletableFuture<>();
        transport.submitJob(new BackspaceCommand(), noResponse);
        assertFalse(noResponse.isDone());
        processActiveJob(transport);
        assertInstanceOf(de.do9fse.cwterminal.core.model.responses.EmptyResponse.class, noResponse.join());

        final CompletableFuture<WinKeyResponse> hostOpenResult = submit(transport, new HostOpenCommand());
        handleStatusBytes(transport);
        processActiveJob(transport);

        assertEquals(3, assertInstanceOf(
            de.do9fse.cwterminal.core.model.responses.WinKeyVersionResponse.class,
            hostOpenResult.join()
        ).majorVersion());
    }

    @Test
    void failsAllPendingJobsWhenTheReaderStops() throws Exception {
        final WinKeyJobQueue queue = new WinKeyJobQueue();
        final CompletableFuture<WinKeyResponse> firstResult = new CompletableFuture<>();
        final CompletableFuture<WinKeyResponse> secondResult = new CompletableFuture<>();
        queue.submit(new WinKeyJob(new HostOpenCommand(), firstResult), job -> {});
        queue.submit(new WinKeyJob(new HostOpenCommand(), secondResult), job -> {});

        queue.failPendingJobs(new IllegalStateException("Reader stopped"));

        assertTrue(firstResult.isCompletedExceptionally());
        assertTrue(secondResult.isCompletedExceptionally());
    }

    @Test
    void leavesUnsolicitedStatusForLoggingAndCompletesHostOpenFromVersionByte() throws Exception {
        final WinKeySerialTransport transport = createTransport(0xd7, 0x9f, 0x23);
        final List<WinKeyResponse> unsolicitedResponses = new ArrayList<>();
        transport.addUnsolicitedResponseListener(unsolicitedResponses::add);
        final CompletableFuture<WinKeyResponse> result = submit(transport, new HostOpenCommand());

        handleStatusBytes(transport);
        assertFalse(result.isDone());

        handleStatusBytes(transport);
        assertFalse(result.isDone());

        handleStatusBytes(transport);
        processActiveJob(transport);

        assertEquals(3, assertInstanceOf(
            de.do9fse.cwterminal.core.model.responses.WinKeyVersionResponse.class,
            result.join()
        ).majorVersion());
        assertEquals(2, unsolicitedResponses.size());
        assertInstanceOf(WinKeyStatusResponse.class, unsolicitedResponses.get(0));
        assertInstanceOf(SpeedPotValueResponse.class, unsolicitedResponses.get(1));
    }

    @Test
    void doesNotNotifyRemovedUnsolicitedResponseListener() throws Exception {
        final WinKeySerialTransport transport = createTransport(0xd7);
        final List<WinKeyResponse> unsolicitedResponses = new ArrayList<>();
        final de.do9fse.cwterminal.core.port.in.WinKeyUnsolicitedResponseListener listener =
            unsolicitedResponses::add;
        transport.addUnsolicitedResponseListener(listener);
        transport.removeUnsolicitedResponseListener(listener);

        handleStatusBytes(transport);

        assertTrue(unsolicitedResponses.isEmpty());
    }

    @Test
    void changesStatusModeWhenReaderProcessesModeCommands() throws Exception {
        final WinKeySerialTransport transport = createTransport(0xc8, 0xc8);
        final List<WinKeyResponse> unsolicitedResponses = new ArrayList<>();
        transport.addUnsolicitedResponseListener(unsolicitedResponses::add);

        final CompletableFuture<WinKeyResponse> wk2Result = submit(transport, new SetWK2ModeCommand());
        processActiveJob(transport);
        assertInstanceOf(de.do9fse.cwterminal.core.model.responses.EmptyResponse.class, wk2Result.join());
        handleStatusBytes(transport);

        final CompletableFuture<WinKeyResponse> wk1Result = submit(transport, new SetWK1ModeCommand());
        processActiveJob(transport);
        assertInstanceOf(de.do9fse.cwterminal.core.model.responses.EmptyResponse.class, wk1Result.join());
        handleStatusBytes(transport);

        final WinKeyStatusResponse wk2Status = assertInstanceOf(WinKeyStatusResponse.class, unsolicitedResponses.get(0));
        assertTrue(wk2Status.pushButtonStatus());
        assertFalse(wk2Status.keyDown());

        final WinKeyStatusResponse wk1Status = assertInstanceOf(WinKeyStatusResponse.class, unsolicitedResponses.get(1));
        assertFalse(wk1Status.pushButtonStatus());
        assertTrue(wk1Status.keyDown());
    }

    private static WinKeySerialTransport createTransport(final int... receivedBytes) throws Exception {
        final SerialPort serialPort = mock(SerialPort.class);
        final byte[] inputBytes = new byte[receivedBytes.length];
        for (int i = 0; i < receivedBytes.length; i++) {
            inputBytes[i] = (byte) receivedBytes[i];
        }

        final WinKeySerialTransport transport = new WinKeySerialTransport(serialPort);
        setField(transport, "inputStream", new PushbackInputStream(new ByteArrayInputStream(inputBytes)));
        setField(transport, "outputStream", new ByteArrayOutputStream());
        return transport;
    }

    private static CompletableFuture<WinKeyResponse> submit(
        final WinKeySerialTransport transport,
        final WinKeyCommand command
    ) throws Exception {
        final CompletableFuture<WinKeyResponse> result = new CompletableFuture<>();
        transport.submitJob(command, result);
        return result;
    }

    private static void handleStatusBytes(final WinKeySerialTransport transport) throws Exception {
        final Method method = WinKeySerialTransport.class.getDeclaredMethod("handleStatusBytes");
        method.setAccessible(true);
        method.invoke(transport);
    }

    private static void processActiveJob(final WinKeySerialTransport transport) throws Exception {
        final Method method = WinKeySerialTransport.class.getDeclaredMethod("processActiveJob");
        method.setAccessible(true);
        method.invoke(transport);
    }

    private static void setField(
        final WinKeySerialTransport transport,
        final String fieldName,
        final Object value
    ) throws Exception {
        final Field field = WinKeySerialTransport.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(transport, value);
    }
}
