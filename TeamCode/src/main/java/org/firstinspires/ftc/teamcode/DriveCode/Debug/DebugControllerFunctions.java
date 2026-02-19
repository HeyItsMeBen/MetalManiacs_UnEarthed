package org.firstinspires.ftc.teamcode.DriveCode.Debug;

import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.arcrobotics.ftclib.gamepad.GamepadKeys;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@Disabled
@TeleOp (name="Debug Controller Functions", group="Debug")
public class DebugControllerFunctions extends LinearOpMode {

    public GamepadEx gamepad;

    @Override
    public void runOpMode() {

        gamepad = new GamepadEx(gamepad1);

        waitForStart();
        //executing
        while (opModeIsActive()) {

            telemetry.addData("Debug File: ", "Check if each function responds to the controller");
            telemetry.addData("", "");

            telemetry.addData("A Pressed?: ", gamepad.isDown(GamepadKeys.Button.A));
            telemetry.addData("B Pressed?: ", gamepad.isDown(GamepadKeys.Button.B));
            telemetry.addData("X Pressed?: ", gamepad.isDown(GamepadKeys.Button.X));
            telemetry.addData("Y Pressed?: ", gamepad.isDown(GamepadKeys.Button.Y));
            telemetry.addData("", "");
            telemetry.addData("D Pad Up Pressed?: ", gamepad.isDown(GamepadKeys.Button.DPAD_UP));
            telemetry.addData("D pad Left Pressed?: ", gamepad.isDown(GamepadKeys.Button.DPAD_LEFT));
            telemetry.addData("D pad Down Pressed?: ", gamepad.isDown(GamepadKeys.Button.DPAD_DOWN));
            telemetry.addData("D pad Right Pressed?: ", gamepad.isDown(GamepadKeys.Button.DPAD_RIGHT));
            telemetry.addData("", "");
            telemetry.addData("Left Stick Up?: ", gamepad.getLeftY() > 0.2);
            telemetry.addData("Left Stick Left?: ", gamepad.getLeftX() > 0.2);
            telemetry.addData("Left Stick Down?: ", gamepad.getLeftX() < 0.2);
            telemetry.addData("Left Stick Right?: ", gamepad.getLeftY() < 0.2);
            telemetry.addData("Left Stick Button Pressed?: ", gamepad.isDown(GamepadKeys.Button.LEFT_STICK_BUTTON));
            telemetry.addData("", "");
            telemetry.addData("Right Stick Up?: ", gamepad.getRightY() > 0.2);
            telemetry.addData("Right Stick Left?: ", gamepad.getRightX() > 0.2);
            telemetry.addData("Right Stick Down?: ", gamepad.getRightX() < 0.2);
            telemetry.addData("Right Stick Right?: ", gamepad.getRightY() < 0.2);
            telemetry.addData("Right Stick Button Pressed?: ", gamepad.isDown(GamepadKeys.Button.RIGHT_STICK_BUTTON));
            telemetry.addData("", "");
            telemetry.addData("Bumper Left Pressed?: ", gamepad.isDown(GamepadKeys.Button.LEFT_BUMPER));
            telemetry.addData("Bumper Right Pressed?: ", gamepad.isDown(GamepadKeys.Button.RIGHT_BUMPER));
            telemetry.addData("", "");
            telemetry.addData("Left Trigger Pressed?: ", gamepad.getTrigger(GamepadKeys.Trigger.LEFT_TRIGGER) > 0.2);
            telemetry.addData("Right Trigger Pressed?: ", gamepad.getTrigger(GamepadKeys.Trigger.RIGHT_TRIGGER) > 0.2);
            telemetry.update();


            idle();
        }
    }
}

