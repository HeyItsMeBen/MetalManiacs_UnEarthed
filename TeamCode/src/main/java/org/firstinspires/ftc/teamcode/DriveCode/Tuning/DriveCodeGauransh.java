package org.firstinspires.ftc.teamcode.DriveCode.Tuning;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp(name = "Basic Drive")
public class DriveCodeGauransh extends LinearOpMode {

    DcMotor leftFront;
    DcMotor leftBack;
    DcMotor rightFront;
    DcMotor rightBack;

    @Override
    public void runOpMode() {

        // Connect the motors to the names in the Robot Controller configuration
        leftFront = hardwareMap.get(DcMotor.class, "leftFront");
        leftBack = hardwareMap.get(DcMotor.class, "leftBack");
        rightFront = hardwareMap.get(DcMotor.class, "rightFront");
        rightBack = hardwareMap.get(DcMotor.class, "rightBack");

        // Reverse the right side
        rightFront.setDirection(DcMotor.Direction.REVERSE);
        rightBack.setDirection(DcMotor.Direction.REVERSE);

        telemetry.addLine("Ready!");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {

            // Get joystick values
            double forward = -gamepad1.left_stick_y;
            double turn = gamepad1.left_stick_x;

            // Combine forward + turning
            double leftPower = forward + turn;
            double rightPower = forward - turn;

            // Keep the values between -1 and 1
            double max = Math.max(Math.abs(leftPower), Math.abs(rightPower));

            if (max > 1.0) {
                leftPower /= max;
                rightPower /= max;
            }

            // Send power to the motors
            leftFront.setPower(leftPower);
            leftBack.setPower(leftPower);

            rightFront.setPower(rightPower);
            rightBack.setPower(rightPower);

            telemetry.addData("Left Power", leftPower);
            telemetry.addData("Right Power", rightPower);
            telemetry.update();
        }
    }
}

