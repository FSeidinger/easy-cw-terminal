package de.do9fse.winkey.lib.core.model;

import java.util.concurrent.CompletableFuture;

import de.do9fse.winkey.lib.core.model.commands.WinKeyCommand;
import de.do9fse.winkey.lib.core.model.responses.WinKeyResponse;

public record WinKeyJob(WinKeyCommand command, CompletableFuture<WinKeyResponse> response) {}
