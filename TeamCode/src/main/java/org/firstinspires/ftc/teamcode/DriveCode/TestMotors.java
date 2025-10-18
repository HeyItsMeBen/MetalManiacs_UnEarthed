package org.firstinspires.ftc.teamcode.DriveCode;

import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.arcrobotics.ftclib.gamepad.GamepadKeys;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp (name="Test Motors", group="test")
public class TestMotors extends LinearOpMode {

    public GamepadEx gamepad;

    @Override
    public void runOpMode() {

        DcMotor Motor = hardwareMap.get(DcMotor.class, "Motor");

        gamepad = new GamepadEx(gamepad1);

        double power = 0.5;

        telemetry.addData("Current power: ", power);
        telemetry.update();

        waitForStart();

        //executing
        while (opModeIsActive()) {
            if (gamepad.getLeftY() > 0) {
                Motor.setPower(power);
            } else if (gamepad.getLeftY() < 0) {
                Motor.setPower(-power);
            }
            Motor.setPower(0);

            idle();
        }
    }
}
