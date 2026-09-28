package de.do9fse.cwterminal.infrastructure.winkey.v2;

public class AdminCommand extends WinKeyCommand {
    private static final byte ADMIN_COMMAND_PREFIX = 0x00;

    private AdminCommand(final byte[] commandBytes) {
        super(commandBytes);
    }

    /**
     * Resets the Winkeyer2 processor to the power up state. Do not send this as
     * part of the initialization sequence. Only send this if you want to do a cold
     * reboot of WK2.
     * 
     * @return Admin Reset command
     */
    public static AdminCommand reset() {
        return new AdminCommand(new byte[] { ADMIN_COMMAND_PREFIX, 0x01 });
    }

    /**
     * Upon power-up, Winkeyer2 initializes with the host mode turned off.
     * 
     * <p>
     * To enable host mode, the PC host must issue the admin:open command. Upon
     * open, Winkeyer2 will respond by sending the revision code back to the
     * host.
     * </p>
     * 
     * <p>
     * The host must wait for this return code before any other commands or data
     * can be sent to Winkeyer2. Upon open, WK1 mode is set.
     * </p>
     * 
     * @return Admin Host Open command
     */
    public static AdminCommand hostOpen() {
        return new AdminCommand(new byte[] { ADMIN_COMMAND_PREFIX, 0x02 });
    }

    /**
     * Use this command to turn off the host interface.
     *
     * <p>
     * Winkeyer2 will return to standby mode after this command is issued.
     * Standby settings will be restored.
     * </p>
     * 
     * @return Admin Host Close command
     */
    public static AdminCommand hostClose() {
        return new AdminCommand(new byte[] { ADMIN_COMMAND_PREFIX, 0x03 });
    }

    /**
     * Used to test the serial interface.
     *
     * <p>
     * The next character sent to Winkeyer2 after this command will be echoed
     * back to the host.
     * </p>
     *
     * @param echoCharacter The character to be echoed back by the Winkeyer2 device.
     * Must not be null or empty.

     * @return Admin Echo Test command
     */
    public static AdminCommand echoTest(final char echoCharacter) {
        return new AdminCommand(new byte[] { ADMIN_COMMAND_PREFIX, 0x04, (byte) echoCharacter });
    }

    /**
     * Returns all of the internal setup parameters.
     *
     * They are sent back in the same order as issued by the Load Defaults
     * command. Again, this command is a diagnostic aid.
     *
     * Only issue this command when host interface is closed. 
     *
     * @return Admin Get Values command
     */
    public static AdminCommand getValues() {
        return new AdminCommand(new byte[] { ADMIN_COMMAND_PREFIX, 0x07 });
    }
}
