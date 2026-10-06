package de.do9fse.cwterminal.core.model.commands;

import static java.lang.reflect.Modifier.isAbstract;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import de.do9fse.cwterminal.core.model.WinKeyProtocolVersion;
import de.do9fse.cwterminal.core.model.error.WinKeyRuntimeException;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;

public interface WinKeyCommand<R> {
    default CommandInfo<R> getCommandInfo() {
        @SuppressWarnings("unchecked")
        final Class<WinKeyCommand<R>> commandClass = (Class<WinKeyCommand<R>>) getClass();
        final String commandName = commandClass.getName();

        final CommandConfiguration configuration = commandClass.getAnnotation(CommandConfiguration.class);
        if (configuration == null) {
            final String message = MessageFormat.format("Command {0} must support the command configuration", commandName);
            throw new WinKeyRuntimeException(message);
        }

        final Set<WinKeyProtocolVersion> supportedKeyProtocolVersions = Set.of(configuration.allowedProtocols());
        @SuppressWarnings("unchecked")
        final Class<R> responseClass = (Class<R>) configuration.responseType();

        return new CommandInfo<>(commandClass, responseClass, supportedKeyProtocolVersions);
    }

    @SuppressWarnings("rawtypes")
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
            throw new WinKeyRuntimeException(
                "Following command classes are missing the @SupportedProtocols annotation: " + unannotatedList
            );
        }

        return validCommandClasses;
    }
}
