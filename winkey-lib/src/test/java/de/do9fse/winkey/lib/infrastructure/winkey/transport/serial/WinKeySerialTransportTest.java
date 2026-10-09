package de.do9fse.winkey.lib.infrastructure.winkey.transport.serial;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PushbackInputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortTimeoutException;

import de.do9fse.winkey.lib.core.model.ApplicationContext;
import de.do9fse.winkey.lib.core.model.WinKeyJob;
import de.do9fse.winkey.lib.core.model.WinKeyProtocolVersion;
import de.do9fse.winkey.lib.core.model.WinKeyState;
import de.do9fse.winkey.lib.core.model.commands.WinKeyCommand;
import de.do9fse.winkey.lib.core.model.commands.admin.EchoTestCommand;
import de.do9fse.winkey.lib.core.model.commands.admin.HostOpenCommand;
import de.do9fse.winkey.lib.core.model.commands.admin.SetWK1ModeCommand;
import de.do9fse.winkey.lib.core.model.commands.admin.SetWK2ModeCommand;
import de.do9fse.winkey.lib.core.model.commands.host.BackspaceCommand;
import de.do9fse.winkey.lib.core.model.error.WinKeyApplicationException;
import de.do9fse.winkey.lib.core.model.error.WinKeyRuntimeException;
import de.do9fse.winkey.lib.core.model.responses.SpeedPotValueResponse;
import de.do9fse.winkey.lib.core.model.responses.WinKeyResponse;
import de.do9fse.winkey.lib.core.model.responses.WinKeyStatusResponse;
import de.do9fse.winkey.lib.core.port.out.WinKeyJobQueue;
import de.do9fse.winkey.lib.infrastructure.winkey.DefaultWinKeyJobQueue;

@DisplayName("Given the WinKey serial Transport")
class WinKeySerialTransportTest {
    @Nested
    @DisplayName("lifecycle")
    class Lifecycle {
        @Test
        void openMovesToInitializingWithoutSendingOrStartingReader() throws Exception {
            final ApplicationContext context = new ApplicationContext(WinKeyProtocolVersion.V2);
            final SerialPort serialPort = mockSerialPort(new ByteArrayInputStream(new byte[0]), new ByteArrayOutputStream());
            final WinKeySerialTransport transport =
                new WinKeySerialTransport(context, new DefaultWinKeyJobQueue(), serialPort);

            transport.open();

            assertEquals(WinKeyState.INITIALIZING, context.getState());
            assertNull(getField(transport, "serialPortReaderThread"));
            verify(serialPort).openPort();
        }

        @Test
        void rejectsCommandSubmissionUntilReady() throws Exception {
            final ApplicationContext context = new ApplicationContext(WinKeyProtocolVersion.V2);
            final DefaultWinKeyJobQueue queue = new DefaultWinKeyJobQueue();
            final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            final SerialPort serialPort = mockSerialPort(new ByteArrayInputStream(new byte[0]), outputStream);
            final WinKeySerialTransport transport = new WinKeySerialTransport(context, queue, serialPort);
            transport.open();

            assertThrows(
                IllegalStateException.class,
                () -> transport.submitJob(new BackspaceCommand(), new CompletableFuture<>())
            );

            assertNull(queue.peek());
            assertEquals(0, outputStream.size());

            transport.close();
            assertEquals(WinKeyState.CLOSED, context.getState());
        }

        @Test
        void initializeRepeatsEchoUntilTheExpectedResponseAndStartsReader() throws Exception {
            final ApplicationContext context = new ApplicationContext(WinKeyProtocolVersion.V2);
            final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            final SerialPort serialPort = mockSerialPort(
                new ByteArrayInputStream(new byte[] { '?', 'R' }),
                outputStream
            );
            final WinKeySerialTransport transport =
                new WinKeySerialTransport(context, new DefaultWinKeyJobQueue(), serialPort);
            transport.open();

            transport.initialize(1, TimeUnit.SECONDS);

            final byte[] echoBytes = new EchoTestCommand('R').toProtocolBytes();
            final byte[] expectedWrites = new byte[echoBytes.length * 2];
            System.arraycopy(echoBytes, 0, expectedWrites, 0, echoBytes.length);
            System.arraycopy(echoBytes, 0, expectedWrites, echoBytes.length, echoBytes.length);
            assertArrayEquals(expectedWrites, outputStream.toByteArray());
            assertEquals(WinKeyState.READY, context.getState());
            assertTrue((Thread) getField(transport, "serialPortReaderThread") != null);
        }

        @Test
        void leavesTransportInitializingWhenHandshakeTimesOut() throws Exception {
            final ApplicationContext context = new ApplicationContext(WinKeyProtocolVersion.V2);
            final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            final SerialPortTimeoutException timeoutException = mock(SerialPortTimeoutException.class);
            final InputStream timeoutInput = new InputStream() {
                @Override
                public int read() throws IOException {
                    try {
                        Thread.sleep(2);
                    } catch (final InterruptedException exception) {
                        Thread.currentThread().interrupt();
                        throw new IOException(exception);
                    }

                    throw timeoutException;
                }
            };
            final SerialPort serialPort = mockSerialPort(timeoutInput, outputStream);
            final WinKeySerialTransport transport =
                new WinKeySerialTransport(context, new DefaultWinKeyJobQueue(), serialPort);
            transport.open();

            assertThrows(TimeoutException.class, () -> transport.initialize(10, TimeUnit.MILLISECONDS));

            assertEquals(WinKeyState.INITIALIZING, context.getState());
            assertTrue(outputStream.size() > 0);
            assertNull(getField(transport, "serialPortReaderThread"));
        }

        @Test
        void closeFromInitializingReturnsToClosed() throws Exception {
            final ApplicationContext context = new ApplicationContext(WinKeyProtocolVersion.V2);
            final SerialPort serialPort = mockSerialPort(new ByteArrayInputStream(new byte[0]), new ByteArrayOutputStream());
            final WinKeySerialTransport transport =
                new WinKeySerialTransport(context, new DefaultWinKeyJobQueue(), serialPort);
            transport.open();

            transport.close();

            assertEquals(WinKeyState.CLOSED, context.getState());
            verify(serialPort).closePort();
        }
    }

    @Test
    void removesOnlyTheLastQueuedJobWhenSendingFails() throws Exception {
        final DefaultWinKeyJobQueue queue = new DefaultWinKeyJobQueue();
        final WinKeySerialTransport transport = createReadyTransport(queue, new OutputStream() {
            @Override
            public void write(final int value) throws IOException {
                throw new IOException("Write failed");
            }
        });
        final CompletableFuture<WinKeyResponse> firstResult = new CompletableFuture<>();
        final WinKeyJob firstJob = new WinKeyJob(new BackspaceCommand(), firstResult);
        queue.offer(firstJob);

        assertThrows(
            WinKeyRuntimeException.class,
            () -> transport.submitJob(new HostOpenCommand(), new CompletableFuture<>())
        );

        assertSame(firstJob, queue.peek());
        assertSame(firstJob, queue.poll());
        assertNull(queue.poll());
    }

    @Test
    void completesCommandsWithoutResponseImmediatelyAndDoesNotConsumeNextResponseJob() throws Exception {
        final WinKeySerialTransport transport = createReadyTransport(0x23);
        final CompletableFuture<WinKeyResponse> noResponse = new CompletableFuture<>();
        transport.submitJob(new BackspaceCommand(), noResponse);
        assertFalse(noResponse.isDone());
        processActiveJob(transport);
        assertInstanceOf(de.do9fse.winkey.lib.core.model.responses.EmptyResponse.class, noResponse.join());

        final CompletableFuture<WinKeyResponse> hostOpenResult = submit(transport, new HostOpenCommand());
        handleStatusBytes(transport);
        processActiveJob(transport);

        assertEquals(3, assertInstanceOf(
            de.do9fse.winkey.lib.core.model.responses.WinKeyVersionResponse.class,
            hostOpenResult.join()
        ).majorVersion());
    }

    @Test
    void failsAllPendingJobsWhenTheReaderStops() {
        final WinKeyJobQueue queue = new DefaultWinKeyJobQueue();
        final CompletableFuture<WinKeyResponse> firstResult = new CompletableFuture<>();
        final CompletableFuture<WinKeyResponse> secondResult = new CompletableFuture<>();
        queue.offer(new WinKeyJob(new HostOpenCommand(), firstResult));
        queue.offer(new WinKeyJob(new HostOpenCommand(), secondResult));

        queue.failPendingJobs(new IllegalStateException("Reader stopped"));

        assertTrue(firstResult.isCompletedExceptionally());
        assertTrue(secondResult.isCompletedExceptionally());
    }

    @Test
    void leavesUnsolicitedStatusForLoggingAndCompletesHostOpenFromVersionByte() throws Exception {
        final WinKeySerialTransport transport = createReadyTransport(0xd7, 0x9f, 0x23);
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
            de.do9fse.winkey.lib.core.model.responses.WinKeyVersionResponse.class,
            result.join()
        ).majorVersion());
        assertEquals(2, unsolicitedResponses.size());
        assertInstanceOf(WinKeyStatusResponse.class, unsolicitedResponses.get(0));
        assertInstanceOf(SpeedPotValueResponse.class, unsolicitedResponses.get(1));
    }

    @Test
    void doesNotNotifyRemovedUnsolicitedResponseListener() throws Exception {
        final WinKeySerialTransport transport = createReadyTransport(0xd7);
        final List<WinKeyResponse> unsolicitedResponses = new ArrayList<>();
        final de.do9fse.winkey.lib.core.port.in.WinKeyUnsolicitedResponseListener listener =
            unsolicitedResponses::add;
        transport.addUnsolicitedResponseListener(listener);
        transport.removeUnsolicitedResponseListener(listener);

        handleStatusBytes(transport);

        assertTrue(unsolicitedResponses.isEmpty());
    }

    @Test
    void changesStatusModeWhenReaderProcessesModeCommands() throws Exception {
        final WinKeySerialTransport transport = createReadyTransport(0xc8, 0xc8);
        final List<WinKeyResponse> unsolicitedResponses = new ArrayList<>();
        transport.addUnsolicitedResponseListener(unsolicitedResponses::add);

        final CompletableFuture<WinKeyResponse> wk2Result = submit(transport, new SetWK2ModeCommand());
        processActiveJob(transport);
        assertInstanceOf(de.do9fse.winkey.lib.core.model.responses.EmptyResponse.class, wk2Result.join());
        handleStatusBytes(transport);

        final CompletableFuture<WinKeyResponse> wk1Result = submit(transport, new SetWK1ModeCommand());
        processActiveJob(transport);
        assertInstanceOf(de.do9fse.winkey.lib.core.model.responses.EmptyResponse.class, wk1Result.join());
        handleStatusBytes(transport);

        final WinKeyStatusResponse wk2Status = assertInstanceOf(WinKeyStatusResponse.class, unsolicitedResponses.get(0));
        assertTrue(wk2Status.pushButtonStatus());
        assertFalse(wk2Status.keyDown());

        final WinKeyStatusResponse wk1Status = assertInstanceOf(WinKeyStatusResponse.class, unsolicitedResponses.get(1));
        assertFalse(wk1Status.pushButtonStatus());
        assertTrue(wk1Status.keyDown());
    }

    private static SerialPort mockSerialPort(final InputStream inputStream, final OutputStream outputStream)
        throws Exception {
        final SerialPort serialPort = mock(SerialPort.class);
        when(serialPort.openPort()).thenReturn(true);
        when(serialPort.getInputStream()).thenReturn(inputStream);
        when(serialPort.getOutputStream()).thenReturn(outputStream);
        return serialPort;
    }

    private static WinKeySerialTransport createReadyTransport(final int... receivedBytes) throws Exception {
        return createReadyTransport(new DefaultWinKeyJobQueue(), new ByteArrayOutputStream(), receivedBytes);
    }

    private static WinKeySerialTransport createReadyTransport(
        final DefaultWinKeyJobQueue queue,
        final OutputStream outputStream,
        final int... receivedBytes
    ) throws Exception {
        final byte[] inputBytes = new byte[receivedBytes.length];
        for (int i = 0; i < receivedBytes.length; i++) {
            inputBytes[i] = (byte) receivedBytes[i];
        }

        final ApplicationContext context = new ApplicationContext(WinKeyProtocolVersion.V2);
        context.transitionToState(WinKeyState.INITIALIZING);
        context.transitionToState(WinKeyState.READY);

        final WinKeySerialTransport transport = new WinKeySerialTransport(context, queue, mock(SerialPort.class));
        setField(transport, "inputStream", new PushbackInputStream(new ByteArrayInputStream(inputBytes)));
        setField(transport, "outputStream", outputStream);
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

    private static Object getField(final WinKeySerialTransport transport, final String fieldName) throws Exception {
        final Field field = WinKeySerialTransport.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(transport);
    }
}
