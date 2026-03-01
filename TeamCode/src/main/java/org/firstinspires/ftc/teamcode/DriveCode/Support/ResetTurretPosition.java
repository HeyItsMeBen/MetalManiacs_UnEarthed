package org.firstinspires.ftc.teamcode.DriveCode.Support;

import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.arcrobotics.ftclib.gamepad.GamepadKeys;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

@TeleOp (name="Reset Turret Position", group="Tuning")
public class ResetTurretPosition extends LinearOpMode {

    public GamepadEx gamepad;

    public DcMotor turret;

    @Override
    public void runOpMode() {

        gamepad = new GamepadEx(gamepad1);

        turret = hardwareMap.get(DcMotor.class, "turret");
        turret.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        turret.setDirection(DcMotor.Direction.REVERSE);

        waitForStart();
        //executing
        while (opModeIsActive()) {

            turret.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

            telemetry.addData("Reset Turret Position", "");
            telemetry.update();

            idle();
        }
    }
}
