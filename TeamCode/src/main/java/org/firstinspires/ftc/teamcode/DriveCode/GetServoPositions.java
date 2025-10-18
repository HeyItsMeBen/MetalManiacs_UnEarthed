package org.firstinspires.ftc.teamcode.DriveCode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;


@TeleOp (name="Get Servo Positions", group="test")

public class GetServoPositions extends LinearOpMode {

    @Override
    public void runOpMode() {

        Servo servo = hardwareMap.get(Servo.class, "hinge");

        double position = 0;

        double interval = 0.5;

        telemetry.addData("Starting Position: ", "0");
        telemetry.addData("Intervals: ", interval);
        telemetry.addData("To increase, ", "press dpad_up");
        telemetry.addData("To decrease, ", "press dpad_down");
        telemetry.addData("To reset, ", "press Y");
        telemetry.update();

        waitForStart();

        //executing
        while (opModeIsActive()) {
            if (gamepad1.dpad_up) {
                position += interval;
                telemetry.addData("Current Servo Position: ", position);
                telemetry.update();
                servo.setPosition(position);
            }
            else if (gamepad1.dpad_down) {
                position -= interval;
                telemetry.addData("Current Servo Position: ", position);
                telemetry.update();
                servo.setPosition(position);
            }
            else if (gamepad1.y) {
                position = 0;
                telemetry.addData("Current Servo Position: ", position);
                telemetry.update();
                servo.setPosition(position);
            }

            idle();
        }
    }
}
