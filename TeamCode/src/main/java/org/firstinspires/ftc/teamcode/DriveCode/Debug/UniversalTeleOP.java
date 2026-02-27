package org.firstinspires.ftc.teamcode.DriveCode.Debug;

import com.acmerobotics.dashboard.config.Config;
import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.arcrobotics.ftclib.gamepad.GamepadKeys;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;

@Disabled
@Config
@TeleOp(name = "Universal TeleOP", group = "Debug")

public class UniversalTeleOP extends LinearOpMode {

    // Driver Code
    public GamepadEx driver;

    private DcMotor frontLeft = null;
    private DcMotor backLeft = null;
    private DcMotor frontRight = null;
    private DcMotor backRight = null;

    public DcMotor intakeMotor;

    public DcMotorEx flywheelMotor;

    public DcMotor turretMotor;

    public DcMotor drum;

    double targetVelocity = 2500;
    double rampSeconds = 5;

    boolean turret_direction_reversed;

    // Note: pushing stick forward gives negative value
    @Override
    public void runOpMode() {

        driver = new GamepadEx(gamepad1);

        ElapsedTime runtime = new ElapsedTime();

        intakeMotor = hardwareMap.get(DcMotorEx.class, "intake");

        flywheelMotor = hardwareMap.get(DcMotorEx.class, "flywheel");

        turretMotor = hardwareMap.get(DcMotorEx.class, "turret");

        drum = hardwareMap.get(DcMotor.class, "transferDrum");

        intakeMotor.setDirection(DcMotorSimple.Direction.REVERSE);

        flywheelMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        flywheelMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        turretMotor.setDirection(DcMotorSimple.Direction.FORWARD);

        drum.setDirection(DcMotor.Direction.REVERSE);

        // Driver Code
        frontLeft  = hardwareMap.get(DcMotor.class, "frontLeft");
        frontRight = hardwareMap.get(DcMotor.class, "frontRight");
        backLeft   = hardwareMap.get(DcMotor.class, "backLeft");
        backRight  = hardwareMap.get(DcMotor.class, "backRight");

        frontLeft.setDirection(DcMotor.Direction.REVERSE);
        frontRight.setDirection(DcMotor.Direction.FORWARD);
        backLeft.setDirection(DcMotor.Direction.FORWARD);
        backRight.setDirection(DcMotor.Direction.FORWARD);

        ElapsedTime timer = new ElapsedTime();
        timer.reset();

        waitForStart();
        runtime.reset();

        while (opModeIsActive()) {
            // Drive Code
            double x = gamepad1.left_stick_x;
            double y = -gamepad1.left_stick_y;
            double turn = gamepad1.right_stick_x;

            double theta = Math.atan2(y, x);
            double power = Math.hypot(x, y);
            double sin = Math.sin(theta - Math.PI/4);
            double cos = Math.cos(theta - Math.PI/4);
            double max = Math.max(Math.abs(sin), Math.abs(cos));

            // Combine the joystick requests for each axis-motion to determine each wheel's power.
            // Set up a variable for each drive wheel to save the power level for telemetry.
            double leftFrontPower = power * cos/max + turn;
            double rightFrontPower = power * sin/max - turn;
            double leftBackPower = power * sin/max + turn;
            double rightBackPower = power * cos/max - turn;

            if ((power + Math.abs(turn)) > 1){
                leftFrontPower /= power + turn;
                rightFrontPower /= power + turn;
                leftBackPower /= power + turn;
                rightBackPower /= power + turn;
            }

            // Send calculated power to wheels
            frontLeft.setPower(leftFrontPower * 0.7);
            frontRight.setPower(rightFrontPower * 0.7);
            backLeft.setPower(leftBackPower * 0.7);
            backRight.setPower(rightBackPower * 0.7);

            telemetry.addData("Universal TeleOP to run all motors and CR servos", "");
            telemetry.addData(" ", "");
            telemetry.addData("Use joysticks to run robot centric drive ", "");
            telemetry.addData(" ", "");
            telemetry.addData("Press A: ", "Run intake, power 0.5");
            telemetry.addData("Press B: ", "Run flywheels, target Velocity 2500");
            telemetry.addData("Press X: ", "Run turret, power 0.3 (will cycle back and forth)");
            telemetry.addData("Press Y: ", "Run transfer drum, power 1");
            telemetry.addData(" ", "");

            telemetry.update();

            if (driver.wasJustPressed(GamepadKeys.Button.A)) {
                intakeMotor.setPower(0.5);
            } else if (driver.wasJustPressed(GamepadKeys.Button.B)) {
                double rampTime = rampSeconds;        // 5 seconds
                double maxVelocity = targetVelocity;  // 2000 ticks per second
                double elapsed = timer.seconds();
                double progress = Math.min(elapsed / rampTime, 1.0);
                double newVelocity = maxVelocity * progress;
                flywheelMotor.setVelocity(newVelocity);
                telemetry.addData("Target Flywheel Velocity: ", targetVelocity);
                telemetry.addData("Current Flywheel Velocity: ", flywheelMotor.getVelocity());
                telemetry.update();
            } else if (driver.wasJustPressed(GamepadKeys.Button.X)) {
                double elapsedst = timer.seconds();
                while (driver.isDown(GamepadKeys.Button.X)) {
                    turretMotor.setPower(0.3);
                    if (elapsedst == 1) {
                        turret_direction_reversed = !turret_direction_reversed;
                        turretMotor.setDirection(
                                turret_direction_reversed ? DcMotorSimple.Direction.REVERSE
                                        : DcMotorSimple.Direction.FORWARD
                        );
                        elapsedst = 0;
                    }
                }
            } else if (driver.wasJustPressed(GamepadKeys.Button.Y)) {
                drum.setPower(1);
            } else {
                intakeMotor.setPower(0);
                flywheelMotor.setVelocity(0);
                turretMotor.setPower(0);
                drum.setPower(0);
            }
            driver.readButtons();
        }
    }
    //end
}