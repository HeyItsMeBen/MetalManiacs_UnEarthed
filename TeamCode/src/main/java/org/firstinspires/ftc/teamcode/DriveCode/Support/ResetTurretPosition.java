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

            gamepad.readButtons();

            if (gamepad.isDown(GamepadKeys.Button.A)) {
                turret.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                telemetry.addData("Reset Position: ","");
            }

            if (gamepad.isDown(GamepadKeys.Button.X)) {
                turret.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                turret.setTargetPosition(0);
                turret.setPower(0.3);
                telemetry.addData("Run To Position 0: ", turret.getCurrentPosition());
            } else {
                turret.setPower(0);
                turret.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            }


            telemetry.addData("Press A to reset turret", "");
            telemetry.addData("To check if it returns to the 0 position, ", "press X");
            telemetry.update();

            idle();
        }
    }
}
