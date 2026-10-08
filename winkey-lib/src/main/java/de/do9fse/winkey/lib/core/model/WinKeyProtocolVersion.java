package de.do9fse.winkey.lib.core.model;

public enum WinKeyProtocolVersion {
    V1(1),
    V2(2),
    V3(3);

    private final int protocolMajorVersion;

    private WinKeyProtocolVersion(final int majorVersion) {
        this.protocolMajorVersion = majorVersion;
    }

    public int getProtocolMajorVersion() {
        return protocolMajorVersion;
    }
}
