package de.do9fse.cwterminal.core.model;

import java.util.concurrent.CompletableFuture;

import de.do9fse.cwterminal.core.model.commands.WinKeyCommand;
import de.do9fse.cwterminal.core.model.responses.WinKeyResponse;

public record WinKeyJob(WinKeyCommand command, CompletableFuture<WinKeyResponse> response) {}
