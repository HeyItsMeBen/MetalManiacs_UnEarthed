package org.firstinspires.ftc.teamcode.DriveCode.TestFiles;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.arcrobotics.ftclib.gamepad.GamepadKeys;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.AutoCode.Roadrunner.Drawing;
import org.firstinspires.ftc.teamcode.AutoCode.Roadrunner.MecanumDrive;

@Disabled
@Config
@TeleOp(name = "Turret and Pinpoint Test", group = "TestFiles")
public class TurretAndPinpointTest extends LinearOpMode {

    public GamepadEx driver;

    private double VerticalWallX = 72;

    private double HorizontalWallY = 72;

    private DcMotor turret;

    @Override
    public void runOpMode() {

        driver = new GamepadEx(gamepad1);

        Pose2d resetPose = new Pose2d(0, 0, Math.toRadians(180)); // x, y, heading in radians

        MecanumDrive drive = new MecanumDrive(hardwareMap, resetPose);

        Pose2d initialEstimatedCurrentPose = new Pose2d(drive.localizer.getPose().position.x, drive.localizer.getPose().position.y, drive.localizer.getPose().heading.toDouble()); // x, y, heading in double radians

        Vector2d goalPosition = new Vector2d(52, 52); // x, y, heading in radians

        turret = hardwareMap.get(DcMotor.class, "turret");
        turret.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        turret.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        waitForStart();

        while (opModeIsActive()) {

            driver.readButtons();

            drive.updatePoseEstimate();

            Pose2d RobotPose = drive.localizer.getPose();

            telemetry.addData("Robot Information: ", "");
            telemetry.addData(" ", "");
            telemetry.addData("x", RobotPose.position.x);
            telemetry.addData("y", RobotPose.position.y);
            telemetry.addData("heading (deg)", Math.toDegrees(RobotPose.heading.toDouble()));
            telemetry.addData("heading (rad)", Math.toRadians(RobotPose.heading.toDouble()));
            telemetry.addData(" ", "");
            telemetry.addData("Current Pose2d (deg): ", RobotPose.position.x + ", " + RobotPose.position.y + ", " + Math.toDegrees(RobotPose.heading.toDouble()));
            telemetry.addData("Current Pose2d (rad): ", RobotPose.position.x + ", " + RobotPose.position.y + ", " + Math.toRadians(RobotPose.heading.toDouble()));
            telemetry.addData(" ", "");
            telemetry.addData("Goal Information: ", "");
            telemetry.addData(" ", "");
            telemetry.addData("Goal Vector Position: ", goalPosition.x + ", " + goalPosition.y);
            telemetry.addData(" ", "");
            telemetry.addData("x", goalPosition.x);
            telemetry.addData("y", goalPosition.y);
            telemetry.addData(" ", "");
            double distance = Math.sqrt(
                    (goalPosition.x - RobotPose.position.x)*(goalPosition.x - RobotPose.position.x)
                            + (goalPosition.y - RobotPose.position.y)*(goalPosition.y - RobotPose.position.y));
            telemetry.addData("Robot Distance From Goal: ", distance);

            telemetry.update();

            // Turret Point Code

            double LengthX = VerticalWallX - RobotPose.position.x;
            double LengthY = HorizontalWallY - RobotPose.position.y;

            long currentTurretHeading = (long) Math.atan2(LengthY,LengthX);
            turret.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            turret.setTargetPosition(Math.toIntExact(currentTurretHeading*200));

            TelemetryPacket packet = new TelemetryPacket();
            packet.fieldOverlay().setStroke("#3F51B5");
            Drawing.drawRobot(packet.fieldOverlay(), RobotPose);
            FtcDashboard.getInstance().sendTelemetryPacket(packet);

            idle();

        }

    }
}

