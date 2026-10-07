package de.do9fse.cwterminal.core.model.commands.test.suite3;

import de.do9fse.cwterminal.core.model.commands.WinKeyCommand;

public class CommandWithoutAnnotation implements WinKeyCommand{
    @Override
    public byte[] getPayloadBytes() {
        return null;
    }
}
