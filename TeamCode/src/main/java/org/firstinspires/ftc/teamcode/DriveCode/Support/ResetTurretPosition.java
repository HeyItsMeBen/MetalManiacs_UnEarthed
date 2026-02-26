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

@Disabled
@TeleOp (name="Position To Theta Turret", group="Tuning")
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

            gamepad.readButtons();

            if (gamepad.getRightX() > 0){
                turret.setPower(0.2);
                telemetry.addData("Current Position: ", turret.getCurrentPosition());

            } else if (gamepad.getRightX() < 0) {
                turret.setPower(-0.2);
                telemetry.addData("Current Position: ", turret.getCurrentPosition());

            } else {
                turret.setPower(0);

            }

            if (gamepad.wasJustPressed(GamepadKeys.Button.A)) {
                turret.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                telemetry.addData("Reset Position: ","");
            }

            if (gamepad.isDown(GamepadKeys.Button.RIGHT_BUMPER)) {
                turret.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                turret.setTargetPosition(0);
                turret.setPower(0.3);         // must set power or it won’t move
                telemetry.addData("Run To Position 0: ", turret.getCurrentPosition());
            } else {
                turret.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            }

            telemetry.update();

            idle();
        }
    }
}
