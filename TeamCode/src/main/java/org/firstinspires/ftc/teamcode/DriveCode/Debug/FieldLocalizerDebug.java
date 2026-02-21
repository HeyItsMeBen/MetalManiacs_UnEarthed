package org.firstinspires.ftc.teamcode.DriveCode.Debug;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Pose2d;
import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.arcrobotics.ftclib.gamepad.GamepadKeys;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.AutoCode.Roadrunner.Drawing;
import org.firstinspires.ftc.teamcode.AutoCode.Roadrunner.MecanumDrive;

@Config
@TeleOp(name = "Field Localizer Debug", group = "Localizer")
public class FieldLocalizerDebug extends LinearOpMode {

    public GamepadEx driver;

    private DcMotor frontLeft = null;
    private DcMotor backLeft = null;
    private DcMotor frontRight = null;
    private DcMotor backRight = null;

    @Override
    public void runOpMode() {

        driver = new GamepadEx(gamepad1);

        frontLeft  = hardwareMap.get(DcMotor.class, "frontLeft");
        frontRight = hardwareMap.get(DcMotor.class, "frontRight");
        backLeft   = hardwareMap.get(DcMotor.class, "backLeft");
        backRight  = hardwareMap.get(DcMotor.class, "backRight");

        frontLeft.setDirection(DcMotor.Direction.REVERSE);
        frontRight.setDirection(DcMotor.Direction.FORWARD);
        backLeft.setDirection(DcMotor.Direction.FORWARD);
        backRight.setDirection(DcMotor.Direction.FORWARD);

        Pose2d resetPose = new Pose2d(0, 0, Math.toRadians(0)); // x, y, heading in radians
        
        MecanumDrive drive = new MecanumDrive(hardwareMap, resetPose);
        
        Pose2d initialEstimatedCurrentPose = new Pose2d(drive.localizer.getPose().position.x, drive.localizer.getPose().position.y, drive.localizer.getPose().heading.toDouble()); // x, y, heading in double radians
        
        waitForStart();

        while (opModeIsActive()) {

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
            telemetry.addData("Use joysticks to run robot centric drive ", "");
            telemetry.addData("Press A to reset pose ", "");
            telemetry.addData(" ", "");

            drive.updatePoseEstimate();

            Pose2d pose = drive.localizer.getPose();
            //telemetry.addData("Start Pose: ", resetPose.position.x + ", " + resetPose.position.y + ", " + Math.toRadians(resetPose.heading.toDouble()));
            telemetry.addData("Initial Estimated Pose: ", initialEstimatedCurrentPose.position.x + ", " + initialEstimatedCurrentPose.position.y + ", " + Math.toRadians(initialEstimatedCurrentPose.heading.toDouble()));
            telemetry.addData(" ", "");
            telemetry.addData("x", pose.position.x);
            telemetry.addData("y", pose.position.y);
            telemetry.addData("heading (deg)", Math.toDegrees(pose.heading.toDouble()));
            telemetry.addData("heading (rad)", Math.toRadians(pose.heading.toDouble()));
            telemetry.addData(" ", "");
            telemetry.addData("Current Pose2d (deg): ", pose.position.x + ", " + pose.position.y + ", " + Math.toDegrees(pose.heading.toDouble()));
            telemetry.addData("Current Pose2d (rad): ", pose.position.x + ", " + pose.position.y + ", " + Math.toRadians(pose.heading.toDouble()));

            telemetry.update();

            if (driver.wasJustPressed(GamepadKeys.Button.A)) {
                pose = resetPose;
                telemetry.addData("Reset Pose ", "");
                telemetry.update();
            }

            TelemetryPacket packet = new TelemetryPacket();
            packet.fieldOverlay().setStroke("#3F51B5");
            Drawing.drawRobot(packet.fieldOverlay(), pose);
            FtcDashboard.getInstance().sendTelemetryPacket(packet);

            idle();

        }

    }
}

