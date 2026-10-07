package de.do9fse.cwterminal.core.model.commands;

import static java.lang.reflect.Modifier.isAbstract;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import de.do9fse.cwterminal.core.model.WinKeyProtocolVersion;
import de.do9fse.cwterminal.core.model.error.WinKeyRuntimeException;
import de.do9fse.cwterminal.core.model.responses.WinKeyResponse;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;

/**
 * The base interface for all WinKey Commands
 *
 * <p>
 * The WinKey commands are grouped into Admin, Host and Other commands. For each
 * of this groups there is an interface extending this base interface.
 * <p>
 *
 * <p>
 * A command itself is a Java record implementing its group interface.
 * </p>
 *
 * <p>
 * A valid command is expected to have a valid
 * {@link de.do9fse.cwterminal.core.model.commands.CommandConfiguration CommandConfiguration}
 * annotation.
 * </p>
 *
 * @param <R> The type of response expected for this command or
 *     {@link de.do9fse.cwterminal.core.model.responses.EmptyResponse EmptyResponse} if no response is expected.
 */
public interface WinKeyCommand {
    /**
     * Returns the command info of this command
     *
     * <p>
     * The command info is read from the
     * {@link de.do9fse.cwterminal.core.model.commands.CommandConfiguration CommandConfiguration}
     * annotation. 
     * </p>
     *
     * @return The command info of this command
     */
    default CommandInfo getCommandInfo() {
        @SuppressWarnings("unchecked")
        final Class<WinKeyCommand> commandClass = (Class<WinKeyCommand>) getClass();
        final String commandName = commandClass.getName();

        final CommandConfiguration configuration = commandClass.getAnnotation(CommandConfiguration.class);
        if (configuration == null) {
            final String message = MessageFormat.format("Command {0} must support the command configuration", commandName);
            throw new WinKeyRuntimeException(message);
        }

        final Set<WinKeyProtocolVersion> supportedKeyProtocolVersions = Set.of(configuration.allowedProtocols());
        @SuppressWarnings("unchecked")
        final Class<WinKeyResponse> responseClass = (Class<WinKeyResponse>) configuration.responseType();

        return new CommandInfo(commandClass, responseClass, supportedKeyProtocolVersions);
    }

    /**
     * The payload bytes used to transmit via WinKey Protocol to the WinKey
     * device
     *
     * <p>
     * Command implementations are expected to override this method to deliver
     * the payload bytes.
     * <P>
     * 
     * <P>
     * In the case of grouped commands, e.g.
     * {@link de.do9fse.cwterminal.core.model.commands.admin.AdminCommand AdminCommand},
     * the {@link #getPayloadBytes()} method only delivers the admin prefix
     * byte. In this example the byte 0x00.
     *
     * @return The payload bytes of this command
     */
    byte[] getPayloadBytes();

    /**
     * The fully assembled payload
     *
     * @return Fully assembled payload
     */
    default byte[] toProtocolBytes() {
        return getPayloadBytes();
    }

    /**
     * Scans for commands
     *
     * <p>
     * This method can be used when starting an application using this library.
     * It scans all commands found in the given basePackage and its sub
     * packages. For each found command it checks if the command is valid, i.e.
     * that the command is annotated with the
     * {@link de.do9fse.cwterminal.core.model.commands.CommandConfiguration CommandConfiguration}
     * annotation.
     * </p>
     *
     * @param basePackage The base package to scan for commands
     * @return A list of valid commands found in the base package and its sub packages.
     * @throws WinKeyRuntimeException If at least one invalid command was found
     */
    static List<Class<WinKeyCommand>> validateCommands(final String basePackage) {
        final List<Class<WinKeyCommand>> validCommandClasses = new ArrayList<>();
        final List<Class<WinKeyCommand>> invalidCommandClasses = new ArrayList<>();

        // Check all classes found in base package
        try (final ScanResult commandClassCandidates = new ClassGraph()
            .acceptPackages(basePackage)
            .enableAllInfo()
            .scan()
        ) {
            // Filter classes that implement the WinKeyCommand interface
            final List<Class<WinKeyCommand>> commandClasses = commandClassCandidates
                .getClassesImplementing(WinKeyCommand.class)
                .loadClasses(WinKeyCommand.class);

            for (final Class<WinKeyCommand> commandClass : commandClasses) {
                // Ignore abstract WinKeyCommand classes
                if (isAbstract(commandClass.getModifiers())) {
                    continue;
                }

                // Load CommandConfiguration annotation
                final CommandConfiguration commandInfoAnnotation = commandClass.getAnnotation(CommandConfiguration.class);

                // Mark class as invalid, if annotation is missing
                if (commandInfoAnnotation == null) {
                    invalidCommandClasses.add(commandClass);
                    continue;
                }

                validCommandClasses.add(commandClass);
            }
        }

        // Check for command classes missing the required annotation
        if (!invalidCommandClasses.isEmpty()) {
            final String unannotatedList = invalidCommandClasses
                .stream()
                .map(Class::getName)
                .collect(Collectors.joining(", "));

            // Raise error, if such a WinKeyCommand implementation is found
            final String message = MessageFormat.format(
                "The following command classes are missing the @{0} annotation: {1}",
                CommandConfiguration.class.getSimpleName(),
                unannotatedList
            );

            throw new WinKeyRuntimeException(message);
        }

        return validCommandClasses;
    }
}
