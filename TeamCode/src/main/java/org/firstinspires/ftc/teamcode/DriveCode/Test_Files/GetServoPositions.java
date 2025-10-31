package org.firstinspires.ftc.teamcode.DriveCode.Test_Files;

import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.arcrobotics.ftclib.gamepad.GamepadKeys;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp (name="Get Servo Positions", group="test")
public class GetServoPositions extends LinearOpMode {

    public GamepadEx gamepad;

    @Override
    public void runOpMode() {

        Servo hingeServo = hardwareMap.get(Servo.class, "hinge");

        gamepad = new GamepadEx(gamepad1);

        double position = 0;

        double interval = 0.01;

        telemetry.addData("Starting Position: ", "0");
        telemetry.addData("Intervals: ", interval);
        telemetry.addData("To increase, ", "press dpad_up");
        telemetry.addData("To decrease, ", "press dpad_down");
        telemetry.addData("To reset, ", "press Y");
        telemetry.update();

        waitForStart();

        //executing
        while (opModeIsActive()) {
            if (gamepad.getButton(GamepadKeys.Button.DPAD_UP)) {
                position += interval;
                telemetry.addData("Current Servo Position: ", position);
                telemetry.update();
                hingeServo.setPosition(position);
            }
            else if (gamepad.getButton(GamepadKeys.Button.DPAD_DOWN)) {
                position -= interval;
                telemetry.addData("Current Servo Position: ", position);
                telemetry.update();
                hingeServo.setPosition(position);
            }
            else if (gamepad.getButton(GamepadKeys.Button.Y)) {
                position = 0;
                telemetry.addData("Current Servo Position: ", position);
                telemetry.update();
                hingeServo.setPosition(position);
            }
            sleep(100);
        }
    }
}
