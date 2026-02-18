package org.firstinspires.ftc.teamcode.DriveCode.Debug;

import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.arcrobotics.ftclib.gamepad.GamepadKeys;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

@TeleOp (name="Voltage Drop Checker", group="test")
public class VoltageDropAcrossMotors extends LinearOpMode {

    public GamepadEx gamepad;

    public DcMotor Motor;

    @Override
    public void runOpMode() {

        gamepad = new GamepadEx(gamepad1);

        Motor = hardwareMap.get(DcMotorEx.class, "intake");
        Motor.setDirection(DcMotorSimple.Direction.FORWARD);

        waitForStart();
        //executing
        while (opModeIsActive()) {

            if (gamepad.isDown(GamepadKeys.Button.DPAD_UP)){
                Motor.setPower(1);
            } else {
                Motor.setPower(0);
            }


            telemetry.addData("Debug File: ", "Applies max continuous power to a motor");
            telemetry.addData("Check for drops in voltage", "");
            telemetry.update();

            idle();
        }
    }
}
