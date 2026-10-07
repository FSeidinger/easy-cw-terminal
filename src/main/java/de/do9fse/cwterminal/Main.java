package de.do9fse.cwterminal;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fazecast.jSerialComm.SerialPort;

import de.do9fse.cwterminal.core.model.commands.WinKeyCommand;
import de.do9fse.cwterminal.core.model.commands.admin.EchoTestCommand;
import de.do9fse.cwterminal.core.model.commands.admin.GetValuesCommand;
import de.do9fse.cwterminal.core.model.error.WinKeyApplicationException;
import de.do9fse.cwterminal.core.model.error.WinKeyRuntimeException;
import de.do9fse.cwterminal.core.model.responses.EchoResponse;
import de.do9fse.cwterminal.core.model.responses.WinKeyResponse;
import de.do9fse.cwterminal.core.port.out.WinKeyTransport;
import de.do9fse.cwterminal.infrastructure.winkey.transport.serial.WinKeySerialTransport;

public final class Main {
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static final int ERROR_OPEN = -1;

    private String portName;
    private SerialPort port;

    private WinKeyTransport transport;

    public static void main(final String[] args) throws Exception {
        if (args.length != 1) {
            LOGGER.error("Usage: easy-cw-terminal <serial-port>");
            return;
        }

        final Main application = new Main(args[0]);
        application.run();
    }

    private Main(final String portName) throws Exception {
        this.portName = portName;
    }

    private void run() throws WinKeyApplicationException {       
        start();
        waitForExit();
        stop();
    }

    private void start() throws WinKeyApplicationException {
        setRootLogLevel("DEBUG");
        createSerialTransport();
    }

    private void waitForExit() throws WinKeyRuntimeException {
        try {
            LOGGER.info("Press Enter to exit");
            System.in.read();
        } catch(final IOException e) {
            throw new WinKeyRuntimeException("Failed to read keyboard", e);
        }
    }

    private void stop() throws WinKeyApplicationException {
        if (transport != null) {
            transport.close();
        }
    }

    private void createSerialTransport() throws WinKeyApplicationException {
        // Create serial port configuration with WinKey default values
        this.port = SerialPort.getCommPort(portName);
        this.port.setBaudRate(1200);
        port.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING, 1000, 0);

        transport = new WinKeySerialTransport(port);
        transport.open();

        final EchoResponse echoResponse = (EchoResponse) executeCommand(transport, new EchoTestCommand('C'));
        LOGGER.info("Echo to winkey device succeeded: {}", echoResponse);

        
        executeCommand(transport, new GetValuesCommand());
    }

    private void setRootLogLevel(String levelName) {
        try {
            final Logger slf4jLogger = LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
            final Class<?> levelClass = Class.forName("ch.qos.logback.classic.Level");
            
            final Method toLevelMethod = levelClass.getMethod("toLevel", String.class);
            final Object levelObject = toLevelMethod.invoke(null, levelName);

            final Method setLevelMethod = slf4jLogger.getClass().getMethod("setLevel", levelClass);
            setLevelMethod.invoke(slf4jLogger, levelObject);

        } catch (final ReflectiveOperationException e) {
            LOGGER.error("Failed to set log level", e);
        }
    }

    private WinKeyResponse executeCommand(final WinKeyTransport transport, final WinKeyCommand command) {
        try {
            final CompletableFuture<WinKeyResponse> response = new CompletableFuture<>();
            transport.submitJob(command, response);
            return response.get(5, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException | WinKeyApplicationException e) {
            throw new WinKeyRuntimeException("Failed to execute command " + command, e);
        }
    }
}