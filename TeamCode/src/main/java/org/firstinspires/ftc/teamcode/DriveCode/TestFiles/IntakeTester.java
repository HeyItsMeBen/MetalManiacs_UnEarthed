package org.firstinspires.ftc.teamcode.DriveCode.TestFiles;

import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.telemetry;

import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.arcrobotics.ftclib.gamepad.GamepadKeys;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.Controllers.IntakeController;
import org.firstinspires.ftc.teamcode.Hardware.Flywheels;
import org.firstinspires.ftc.teamcode.Hardware.Intake;
import org.firstinspires.ftc.teamcode.Hardware.Transfer;

@TeleOp (name="Intake Tester", group="test")
public class IntakeTester extends LinearOpMode {

    public GamepadEx driver;

    Intake intake;
    IntakeController intakeController;

    public int rpm = 3000;
    public double speed = 0.5;

    Flywheels flywheel;
    Transfer transferDrum;
    Transfer transferKick;



        @Override
    public void runOpMode() {

//        DcMotor Motor = hardwareMap.get(DcMotor.class, "Intake");

        transferDrum = new Transfer(hardwareMap);
        transferKick= new Transfer(hardwareMap);

        intake = new Intake(hardwareMap);

        driver = new GamepadEx(gamepad1);

        flywheel = new Flywheels(hardwareMap);
        intakeController = new IntakeController(intake, transferDrum, transferKick);


            waitForStart();

        //executing
        while (opModeIsActive()) {
            driver.readButtons();

            if (driver.wasJustPressed(GamepadKeys.Button.RIGHT_BUMPER)) {
                intakeController.toggleIntake();
            }

            if (driver.wasJustPressed(GamepadKeys.Button.LEFT_BUMPER)) {
                intakeController.toggleReverse();
            }
            telemetry.addData("Intake RPM", intake.getIntakeMotorRPM());
            telemetry.addData("Drum RPM", transferDrum.getTransferDrumRPM());
            telemetry.addData("Drum Target Speed", intakeController.getTransferPower());
            telemetry.addData("Balls Fed", intakeController.getBallsFed());


            intakeController.update();

            telemetry.update();

            idle();
        }
    }
}
