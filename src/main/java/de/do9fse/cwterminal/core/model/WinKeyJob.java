package de.do9fse.cwterminal.core.model;

import java.util.concurrent.CompletableFuture;

import de.do9fse.cwterminal.core.model.commands.WinKeyCommand;

public record WinKeyJob<R>(WinKeyCommand command, CompletableFuture<R> result) {
    public static WinKeyJob<Void> of(final WinKeyCommand command) {
        return new WinKeyJob<>(command, new CompletableFuture<>());
    }

    public static <R> WinKeyJob<R> of(final WinKeyCommand command, final CompletableFuture<R> result) {
        return new WinKeyJob<>(command, result);
    }
}
