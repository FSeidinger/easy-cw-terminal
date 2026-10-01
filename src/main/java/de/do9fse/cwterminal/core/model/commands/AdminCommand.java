package de.do9fse.cwterminal.core.model.commands;

public abstract sealed class AdminCommand implements KeyerCommand
permits
    CalibrateCommand,
    ResetCommand,
    HostOpenCommand,
    HostCloseCommand,
    EchoTestCommand,
    ReadPaddleADCommand,
    ReadSpeedA2DCommand,
    GetValuesCommand,
    ReservedCommand,
    GetCalibrationValueCommand,
    SetWK1ModeCommand,
    SetWK2ModeCommand,
    DumpEEPROMCommand,
    LoadEEPROMCommand,
    SendStandaloneMessageCommand
{
    
    @Override
    public String toString() {
        return getClass().getSimpleName()
        + "["
        + stringifyFields()
        +  "]";
    }

    protected String stringifyFields() {
        return "";
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) return true;

        if (obj == null || getClass() != obj.getClass()) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
