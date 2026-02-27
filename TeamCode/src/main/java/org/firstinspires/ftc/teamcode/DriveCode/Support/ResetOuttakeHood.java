package org.firstinspires.ftc.teamcode.DriveCode.Support;

import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.arcrobotics.ftclib.gamepad.GamepadKeys;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.Hardware.OuttakeHood;

@TeleOp (name="Reset Outtake Hood Servo Position", group="Debug")
public class ResetOuttakeHood extends LinearOpMode {

    OuttakeHood outtakeHood;

    public GamepadEx gamepad;

    @Override
    public void runOpMode() {

        outtakeHood = new OuttakeHood(hardwareMap);
        gamepad = new GamepadEx(gamepad1);

        waitForStart();

        //executing
        while (opModeIsActive()) {

            gamepad.readButtons();

            if (gamepad.wasJustPressed(GamepadKeys.Button.A)) {
                outtakeHood.setCurrentPositionAsZeroPosition();
                telemetry.addData("Reset Position: ","");
            }

            telemetry.addData("Press A to reset hood servo", "");
            telemetry.update();

            idle();

        }
    }
}
