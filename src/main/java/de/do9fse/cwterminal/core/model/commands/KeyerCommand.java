package de.do9fse.cwterminal.core.model.commands;

public sealed interface KeyerCommand permits AdminCommand, HostModeCommand, TextCommand {};