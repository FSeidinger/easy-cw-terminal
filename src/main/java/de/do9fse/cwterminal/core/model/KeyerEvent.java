package de.do9fse.cwterminal.core.model;

public sealed interface KeyerEvent permits KeyerEvent.HostOpenedEvent {
    record HostOpenedEvent(KeyerVersion version) implements KeyerEvent {}
}
