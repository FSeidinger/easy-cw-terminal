package de.do9fse.cwterminal.infrastructure.winkey;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import javax.naming.OperationNotSupportedException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.do9fse.cwterminal.core.model.KeyerVersion;
import de.do9fse.cwterminal.core.model.commands.CalibrateCommand;
import de.do9fse.cwterminal.core.model.commands.DumpEEPROMCommand;
import de.do9fse.cwterminal.core.model.commands.EchoTestCommand;
import de.do9fse.cwterminal.core.model.commands.GetCalibrationValueCommand;
import de.do9fse.cwterminal.core.model.commands.GetValuesCommand;
import de.do9fse.cwterminal.core.model.commands.HostCloseCommand;
import de.do9fse.cwterminal.core.model.commands.HostOpenCommand;
import de.do9fse.cwterminal.core.model.commands.KeyerCommand;
import de.do9fse.cwterminal.core.model.commands.LoadEEPROMCommand;
import de.do9fse.cwterminal.core.model.commands.ReadPaddleADCommand;
import de.do9fse.cwterminal.core.model.commands.ReadSpeedA2DCommand;
import de.do9fse.cwterminal.core.model.commands.ReservedCommand;
import de.do9fse.cwterminal.core.model.commands.ResetCommand;
import de.do9fse.cwterminal.core.model.commands.SendStandaloneMessageCommand;
import de.do9fse.cwterminal.core.model.commands.SetWK1ModeCommand;
import de.do9fse.cwterminal.core.model.commands.SetWK2ModeCommand;
import de.do9fse.cwterminal.core.model.commands.SideToneControlCommand;
import de.do9fse.cwterminal.core.model.commands.SideToneFrequencyCommand;
import de.do9fse.cwterminal.core.model.commands.TextCommand;

public class CommandFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(CommandFactory.class);

    private static final Charset US_ASCII = StandardCharsets.US_ASCII;

    private KeyerVersion version;

    public CommandFactory(final KeyerVersion version) {
        this.version = Objects.requireNonNull(version, "Keyer version must not be null");
        LOGGER.info("Command factory created to be compatible with WinKey version v{}.x", version.majorVersion());
    }

    public byte[] from(final KeyerCommand command) throws OperationNotSupportedException {
        switch (version.majorVersion()) {
            case 1: return fromV1(command);
            case 2: return fromV2(command);
            default: throw new OperationNotSupportedException("Protocol version V" + this.version.majorVersion() + " is not yet implemented"); 
        }
    }

    private byte[] fromV1(final KeyerCommand command) {
        if (isNotSupportedInV1(command)) {
            throw new IllegalArgumentException("Command " + command.getClass().getName() + " is not supported in V1");
        }

        return switch (command) {
            // Admin commands
            case CalibrateCommand c ->              new byte[] { 0x00, 0x00, (byte) 0xFF };
            case ResetCommand c ->                  new byte[] { 0x00, 0x01 };
            case HostOpenCommand c ->               new byte[] { 0x00, 0x02 };
            case HostCloseCommand c ->              new byte[] { 0x00, 0x03 };
            case EchoTestCommand c ->               new byte[] { 0x00, 0x04, (byte) c.getEchoChar() };
            case ReadPaddleADCommand c ->           new byte[] { 0x00, 0x05 };
            case ReadSpeedA2DCommand c ->           new byte[] { 0x00, 0x06 };
            case GetValuesCommand c ->              new byte[] { 0x00, 0x07 };
            case ReservedCommand c ->               new byte[] { 0x00, 0x08 };
            case GetCalibrationValueCommand c ->    new byte[] { 0x00, 0x09 };

            // Host mode commands
            case SideToneFrequencyCommand c ->      new byte[] { 0x01, fromSideToneFrequencyCommand(c) };

            case TextCommand c -> c.text().getBytes();
            default ->  throw new IllegalArgumentException("Unsupported command type: " + command.getClass().getName());
        };
    }

    private byte[] fromV2(final KeyerCommand command) {
        if (isNotSupportedInV2(command)) {
            throw new IllegalArgumentException("Command " + command.getClass().getName() + " is not supported in V2");
        }
       
        return switch (command) {
            // Admin commands
            case ResetCommand c ->                  new byte[] { 0x00, 0x01 };
            case HostOpenCommand c ->               new byte[] { 0x00, 0x02 };
            case HostCloseCommand c ->              new byte[] { 0x00, 0x03 };
            case EchoTestCommand c ->               new byte[] { 0x00, 0x04, (byte) c.getEchoChar() };
            case GetValuesCommand c ->              new byte[] { 0x00, 0x07 };
            case ReservedCommand c ->               new byte[] { 0x00, 0x08 };
            case SetWK1ModeCommand c ->             new byte[] { 0x00, 0x0A };
            case SetWK2ModeCommand c ->             new byte[] { 0x00, 0x0B };
            case DumpEEPROMCommand c ->             new byte[] { 0x00, 0x0C };
            case LoadEEPROMCommand c ->             new byte[] { 0x00, 0x0D };
            case SendStandaloneMessageCommand c ->  new byte[] { 0x00, 0x0E, (byte) c.getMessageId() };

            // Host mode commands
            case SideToneControlCommand c ->        new byte[] { 0x01, fromSideToneControlCommand(c) };

            case TextCommand c -> c.text().getBytes(US_ASCII);

            default ->  throw new IllegalArgumentException("Unknown command type: " + command.getClass().getName());
        };
    }

    private boolean isNotSupportedInV1(final KeyerCommand command) {
        return switch (command) {
            case SetWK1ModeCommand c -> true;
            case SetWK2ModeCommand c -> true;
            case DumpEEPROMCommand c -> true;
            case LoadEEPROMCommand c -> true;
            case SendStandaloneMessageCommand c -> true;
            case SideToneControlCommand c -> true;

            default -> false;
        };
    }

    private boolean isNotSupportedInV2(final KeyerCommand command) {
        return switch (command) {
            case CalibrateCommand c -> true;
            case ReadPaddleADCommand c -> true;
            case ReadSpeedA2DCommand c -> true;
            case SideToneFrequencyCommand c -> true;

            default -> false;
        };
    }

    private byte fromSideToneFrequencyCommand(final SideToneFrequencyCommand command) {
        final int stf = command.getSideToneFrequency().ordinal();
        return (byte) (stf + 1);
    }
    
    private byte fromSideToneControlCommand(final SideToneControlCommand command) {
        int value = 0;

        // Calculate the side tone frequency from enum ordinal stored in bits 0-3
        value |= command.getSideToneFrequency().ordinal() + 1;

        // If paddle sidetone only is enabled, set bit 7
        if (command.isEnablePaddleSidetoneOnly()) {
            value |= 0x80;
        }

        return (byte) value;
    }
}