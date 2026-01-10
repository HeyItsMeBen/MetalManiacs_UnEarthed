package org.firstinspires.ftc.teamcode.DriveCode.Test_Files;

import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

@Disabled
@TeleOp (name="Test Motors", group="test")
public class TestMotors extends LinearOpMode {

    public GamepadEx gamepad;

    @Override
    public void runOpMode() {

        DcMotor Motor = hardwareMap.get(DcMotor.class, "Motor");

        gamepad = new GamepadEx(gamepad1);

        double power = 1;

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
