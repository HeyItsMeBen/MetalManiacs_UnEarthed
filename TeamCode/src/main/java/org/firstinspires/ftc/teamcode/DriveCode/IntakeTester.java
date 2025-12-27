package org.firstinspires.ftc.teamcode.DriveCode;

import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import org.firstinspires.ftc.teamcode.Hardware.IntakeSystem;
import org.firstinspires.ftc.teamcode.Old_Code.Intake;

@TeleOp (name="Intake Tester", group="test")
public class IntakeTester extends LinearOpMode {

    public GamepadEx gamepad;

    IntakeSystem intake;

    @Override
    public void runOpMode() {

        DcMotor Motor = hardwareMap.get(DcMotor.class, "Intake");

        intake = new IntakeSystem(hardwareMap);

        gamepad = new GamepadEx(gamepad1);

        waitForStart();

        //executing
        while (opModeIsActive()) {
            if (gamepad.getLeftY() > 0) {
                intake.runIntakeFullPower();
            } else if (gamepad.getLeftY() < 0) {
                intake.reverseIntake();
            }
            intake.stopIntake();

            idle();
        }
    }
}
