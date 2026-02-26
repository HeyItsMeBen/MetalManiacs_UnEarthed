package org.firstinspires.ftc.teamcode.DriveCode.Support;

import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.arcrobotics.ftclib.gamepad.GamepadKeys;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp (name="Get Servo Positions", group="Debug")
public class GetServoPositions extends LinearOpMode {

    public GamepadEx gamepad;

    @Override
    public void runOpMode() {

        Servo servo = hardwareMap.get(Servo.class, "transferKick");

        gamepad = new GamepadEx(gamepad1);

        double position = 0;

        double interval = 0.01;

        telemetry.addData("Intervals: ", interval);
        telemetry.addData("To increase, ", "press dpad_up");
        telemetry.addData("To decrease, ", "press dpad_down");
        telemetry.addData("To reset, ", "press Y");
        telemetry.update();

        waitForStart();

        servo.setPosition(position);

        //executing
        while (opModeIsActive()) {
            if (gamepad.getButton(GamepadKeys.Button.DPAD_UP)) {
                position += interval;
                telemetry.addData("Current Servo Position: ", position);
                telemetry.update();
                servo.setPosition(position);
            }
            else if (gamepad.getButton(GamepadKeys.Button.DPAD_DOWN)) {
                position -= interval;
                telemetry.addData("Current Servo Position: ", position);
                telemetry.update();
                servo.setPosition(position);
            }
            else if (gamepad.getButton(GamepadKeys.Button.X)) {
                telemetry.addData("Current Servo Position: ", servo.getPosition());
                telemetry.update();
            }
            else if (gamepad.getButton(GamepadKeys.Button.Y)) {
                position = 0;
                telemetry.addData("Reset Servos to position ", position);
                telemetry.update();
                servo.setPosition(position);
            }

            sleep(100);
        }
    }
}
