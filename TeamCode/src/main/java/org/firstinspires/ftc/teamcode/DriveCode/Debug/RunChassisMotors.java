package org.firstinspires.ftc.teamcode.DriveCode.Debug;

import com.acmerobotics.dashboard.config.Config;
import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.arcrobotics.ftclib.gamepad.GamepadKeys;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

@Config
@TeleOp(name = "[Old] Drive Code", group = "Debug File")

public class RunChassisMotors extends LinearOpMode {

    // Driver Code
    public GamepadEx driver;

    private DcMotor frontLeft = null;
    private DcMotor backLeft = null;
    private DcMotor frontRight = null;
    private DcMotor backRight = null;

    DcMotor currentMotor;

    // Note: pushing stick forward gives negative value
    @Override
    public void runOpMode() {

        driver = new GamepadEx(gamepad1);

        ElapsedTime runtime = new ElapsedTime();

        // Driver Code
        frontLeft  = hardwareMap.get(DcMotor.class, "frontLeft");
        frontRight = hardwareMap.get(DcMotor.class, "frontRight");
        backLeft   = hardwareMap.get(DcMotor.class, "backLeft");
        backRight  = hardwareMap.get(DcMotor.class, "backRight");

        backLeft.setDirection(DcMotor.Direction.FORWARD);
        frontLeft.setDirection(DcMotor.Direction.REVERSE);
        backRight.setDirection(DcMotor.Direction.REVERSE);
        frontRight.setDirection(DcMotor.Direction.REVERSE);

        currentMotor = frontLeft;

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

            //run individual wheels
            telemetry.addData("Use joystick to run robot centric drive ", "");
            telemetry.addData("", "");
            telemetry.addData("Press A: ", " Set motor to frontLeft");
            telemetry.addData("Press B: ", " Set motor to frontRight");
            telemetry.addData("Press X: ", " Set motor to backLeft");
            telemetry.addData("Press Y: ", " Set Motor to backRight");
            telemetry.addData("Press D PAD Up/Down: ", " Run set motor forward/reverse");

            telemetry.update();
            
            if (driver.wasJustPressed(GamepadKeys.Button.A)) {
                currentMotor = frontLeft;
            } else if (driver.wasJustPressed(GamepadKeys.Button.B)) {
                currentMotor = frontRight;
            } else if (driver.wasJustPressed(GamepadKeys.Button.X)) {
                currentMotor = backLeft;
            } else if (driver.wasJustPressed(GamepadKeys.Button.Y)) {
                currentMotor = backRight;
            }

            while (driver.getButton(GamepadKeys.Button.DPAD_UP)){
                currentMotor.setPower(1.0);
            }

            while (driver.getButton(GamepadKeys.Button.DPAD_DOWN)){
                currentMotor.setPower(-1.0);
            }
        }
        //Run OpMode
    }
    //end
}