package org.firstinspires.ftc.teamcode.DriveCode.Debug;

import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.arcrobotics.ftclib.gamepad.GamepadKeys;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;


@Disabled
@TeleOp (name="Voltage Drop Checker", group="Debug")
public class VoltageDropAcrossMotors extends LinearOpMode {

    public GamepadEx gamepad;

    public DcMotor intakeMotor;

    public DcMotor flywheelMotor;

    public DcMotor turretMotor;
    public DcMotor transferMotor;
    public DcMotor currentMotor;

    String currentSetMotor;

    boolean reversed = false;

    ElapsedTime flywheelTimer;

    double targetPower = 1;
    double rampSeconds = 5;

    @Override
    public void runOpMode() {

        gamepad = new GamepadEx(gamepad1);

        intakeMotor = hardwareMap.get(DcMotorEx.class, "intake");

        flywheelMotor = hardwareMap.get(DcMotorEx.class, "flywheel");

        turretMotor = hardwareMap.get(DcMotorEx.class, "turret");

        transferMotor = hardwareMap.get(DcMotorEx.class, "transferDrum");

        intakeMotor.setDirection(DcMotorSimple.Direction.REVERSE);

        flywheelMotor.setDirection(DcMotorSimple.Direction.REVERSE);

        turretMotor.setDirection(DcMotorSimple.Direction.FORWARD);

        transferMotor.setDirection(DcMotorSimple.Direction.FORWARD);

        currentMotor = flywheelMotor;

        ElapsedTime timer = new ElapsedTime();
        timer.reset();

        waitForStart();
        //executing
        while (opModeIsActive()) {

            telemetry.addData("Debug File: ", "Applies max continuous power to a motor");
            telemetry.addData("Check for drops in voltage", "");

            telemetry.addData("Press A: ", " Set motor to intake motor");
            telemetry.addData("Press B: ", " Set motor to flywheel motor");
            telemetry.addData("Press X: ", " Set motor to turret motor");
            telemetry.addData("Press Y: ", " Set motor to transfer motor");
            telemetry.addData("Press Right Bumper: ", " Reverse current motor direction");
            telemetry.addData("Press D PAD Up: ", " Run set motor forward");
            telemetry.addData(" ", "");
            telemetry.addData("Current Set Motor: ", currentSetMotor);
            telemetry.update();

            if (gamepad.wasJustPressed(GamepadKeys.Button.A)) {
                currentMotor = intakeMotor;
                currentSetMotor = "intake";
            } else if (gamepad.wasJustPressed(GamepadKeys.Button.B)) {
                currentMotor = flywheelMotor;
                currentSetMotor = "flywheel";
            } else if (gamepad.wasJustPressed(GamepadKeys.Button.X)) {
                currentMotor = turretMotor;
                currentSetMotor = "turret";
            } else if (gamepad.wasJustPressed(GamepadKeys.Button.Y)) {
                currentMotor = transferMotor;
                currentSetMotor = "transferMotor";
            }

            if (gamepad.isDown(GamepadKeys.Button.DPAD_UP)){
                double rampTime = rampSeconds;        // 5 seconds
                double maxPower = targetPower;
                double elapsed = timer.seconds();
                double progress = Math.min(elapsed / rampTime, 1.0);
                double newPower = maxPower * progress;
                currentMotor.setPower(newPower);
            } else {
                currentMotor.setPower(0);
            }

            if (gamepad.wasJustPressed(GamepadKeys.Button.RIGHT_BUMPER)) {
                reversed = !reversed;
                currentMotor.setDirection(
                        reversed ? DcMotorSimple.Direction.REVERSE
                                : DcMotorSimple.Direction.FORWARD
                );
            }

            gamepad.readButtons();
            idle();
        }
    }
}
