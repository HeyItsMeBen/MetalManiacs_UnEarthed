package org.firstinspires.ftc.teamcode.DriveCode.Test_Files;

import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.arcrobotics.ftclib.gamepad.GamepadKeys;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Hardware.Turret;

@TeleOp (name="Test Motors: Turret", group="test")
public class GetTurretPositions extends LinearOpMode {

    public GamepadEx gamepad;

    Turret turret;

    @Override
    public void runOpMode() {

        gamepad = new GamepadEx(gamepad1);

        turret = new Turret(hardwareMap);

        waitForStart();

        turret.resetInitial();
        //executing
        while (opModeIsActive()) {

            if (gamepad.wasJustPressed(GamepadKeys.Button.Y)){
                turret.resetInitial();
            }

            if (gamepad.getLeftX() > 0) {
                turret.setMotorPower(0.2);

            }

            if (gamepad.getLeftX() < 0) {
                turret.setMotorPower(-0.2);
            }

            if (gamepad.wasJustPressed(GamepadKeys.Button.X)){
                turret.getTurretPosition();
            }

            if (gamepad.wasJustPressed(GamepadKeys.Button.B)){
                turret.rotateToPosition(0);
            }

            idle();
        }
    }
}
