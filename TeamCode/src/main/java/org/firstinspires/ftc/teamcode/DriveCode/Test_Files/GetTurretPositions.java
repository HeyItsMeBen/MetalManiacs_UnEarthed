package org.firstinspires.ftc.teamcode.DriveCode.Test_Files;

import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.arcrobotics.ftclib.gamepad.GamepadKeys;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.teamcode.Hardware.Turret;

@TeleOp (name="Test Motors: Turret", group="test")
public class GetTurretPositions extends LinearOpMode {

    public GamepadEx gamepad;

    public DcMotor turretMotor;

    @Override
    public void runOpMode() {

        gamepad = new GamepadEx(gamepad1);

        turretMotor = hardwareMap.get(DcMotorEx.class, "turret");
        turretMotor.setDirection(DcMotorSimple.Direction.FORWARD);
        turretMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        turretMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        waitForStart();
        //executing
        while (opModeIsActive()) {

            if (gamepad.isDown(GamepadKeys.Button.Y)){
                turretMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                turretMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            }

            if (gamepad.isDown(GamepadKeys.Button.DPAD_LEFT)) {
                turretMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                turretMotor.setTargetPosition(-350);
                turretMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                turretMotor.setPower(0.5);
            }

            if (gamepad.isDown(GamepadKeys.Button.DPAD_RIGHT)) {
                turretMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                turretMotor.setTargetPosition(350);
                turretMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                turretMotor.setPower(0.5);
            }

            if (gamepad.isDown(GamepadKeys.Button.X)){
                turretMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                turretMotor.setTargetPosition(0);
                turretMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                turretMotor.setPower(0.5);
            }

            if (turretMotor.getMode() == DcMotor.RunMode.RUN_TO_POSITION && !turretMotor.isBusy()) {
                turretMotor.setPower(0);
            }

            telemetry.addData("Current Position: ", turretMotor.getCurrentPosition());
            telemetry.update();

            idle();
        }
    }
}
