package de.do9fse.cwterminal.core.port.out;

public interface KeyerOutput {
    public void setWPMSpeed(final byte wpm);
    public void setPTTLeadingTime (final int ms);
    public void setPTTTrailingTime (final int ms);
    public void setPTTHangTime (final int ms);

    public void sentText(final String text);
}
