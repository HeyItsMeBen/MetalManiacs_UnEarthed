package org.firstinspires.ftc.teamcode.DriveCode.Debug;

import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.arcrobotics.ftclib.gamepad.GamepadKeys;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

@TeleOp (name="Voltage Drop Checker", group="Debug")
public class VoltageDropAcrossMotors extends LinearOpMode {

    public GamepadEx gamepad;

    public DcMotor intakeMotor;

    public DcMotor flywheelMotor;

    public DcMotor turretMotor;
    public DcMotor currentMotor;

    String currentSetMotor;

    boolean reversed = false;

    @Override
    public void runOpMode() {

        gamepad = new GamepadEx(gamepad1);

        intakeMotor = hardwareMap.get(DcMotorEx.class, "intake");

        flywheelMotor = hardwareMap.get(DcMotorEx.class, "flywheel");

        turretMotor = hardwareMap.get(DcMotorEx.class, "turret");

        intakeMotor.setDirection(DcMotorSimple.Direction.REVERSE);

        flywheelMotor.setDirection(DcMotorSimple.Direction.REVERSE);

        turretMotor.setDirection(DcMotorSimple.Direction.FORWARD);

        currentMotor = flywheelMotor;

        waitForStart();
        //executing
        while (opModeIsActive()) {

            telemetry.addData("Debug File: ", "Applies max continuous power to a motor");
            telemetry.addData("Check for drops in voltage", "");

            telemetry.addData("Press A: ", " Set motor to intake motor");
            telemetry.addData("Press B: ", " Set motor to flywheel motor");
            telemetry.addData("Press X: ", " Set motor to turret motor");
            telemetry.addData("Press Y: ", " Reverse current motor direction");
            telemetry.addData("Press D PAD Up/Down: ", " Run set motor forward/reverse");
            telemetry.addData(" ", "");
            telemetry.addData("Current Set Motor: ", currentSetMotor);
            telemetry.update();

            if (gamepad.wasJustPressed(GamepadKeys.Button.A)) {
                currentMotor = intakeMotor;
                currentSetMotor = "Front Left";
            } else if (gamepad.wasJustPressed(GamepadKeys.Button.B)) {
                currentMotor = flywheelMotor;
                currentSetMotor = "Front Right";
            } else if (gamepad.wasJustPressed(GamepadKeys.Button.X)) {
                currentMotor = turretMotor;
                currentSetMotor = "Back Left";
            }

            if (gamepad.isDown(GamepadKeys.Button.DPAD_UP)){
                currentMotor.setPower(1);
            } else {
                currentMotor.setPower(0);
            }

            if (gamepad.wasJustPressed(GamepadKeys.Button.Y)) {
                reversed = !reversed;
                currentMotor.setDirection(
                        reversed ? DcMotorSimple.Direction.REVERSE
                                : DcMotorSimple.Direction.FORWARD
                );
            }

            idle();
        }
    }
}
