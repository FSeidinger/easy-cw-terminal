package de.do9fse.cwterminal.core.model.commands;

public abstract sealed class HostModeCommand implements KeyerCommand
permits
    SideToneFrequencyCommand,
    SideToneControlCommand
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
