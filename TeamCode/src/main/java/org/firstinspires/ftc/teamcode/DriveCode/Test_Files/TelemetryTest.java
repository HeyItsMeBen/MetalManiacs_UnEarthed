package org.firstinspires.ftc.teamcode.DriveCode.Test_Files;

import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.arcrobotics.ftclib.gamepad.GamepadKeys;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@Disabled
//The purpose of this file is to determine how the telemetry functions. This will be important for logging the flywheel velocities
@TeleOp (name="Telemetry Test", group="test")
public class TelemetryTest extends LinearOpMode {

    public GamepadEx gamepad;

    @Override
    public void runOpMode() {

        gamepad = new GamepadEx(gamepad1);
        waitForStart();

        //executing
        while (opModeIsActive()) {
            if (gamepad.getButton(GamepadKeys.Button.A)) {
                telemetry.log().add("Button A Pressed");
            }
            else if (gamepad.getButton(GamepadKeys.Button.B)) {
                telemetry.addData("Button B Pressed", "");
            }
            else if (gamepad.getButton(GamepadKeys.Button.X)) {
                telemetry.addData("Button X Pressed", "");
                telemetry.update();
            }
            else if (gamepad.getButton(GamepadKeys.Button.Y)) {
                telemetry.update();
            }
        }
    }
}
