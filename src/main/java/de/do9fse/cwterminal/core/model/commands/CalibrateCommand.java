package de.do9fse.cwterminal.core.model.commands;

public final class CalibrateCommand extends AdminCommand {
    private final int calibrationValue;

    public CalibrateCommand(final int calibrationValue) {
        this.calibrationValue = calibrationValue;
    }

    public int getCalibrationValue() {
        return calibrationValue;
    }

    @Override
    protected String stringifyFields() {
        return "calibrationValue=" + calibrationValue;
    }
}
