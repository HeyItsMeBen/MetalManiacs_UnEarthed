package org.firstinspires.ftc.teamcode.AutoCode.Pathings.TrajectoryPaths.Red;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.SequentialAction;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.robotcore.external.hardware.camera.BuiltinCameraDirection;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.hardware.camera.controls.ExposureControl;
import org.firstinspires.ftc.robotcore.external.hardware.camera.controls.GainControl;
import org.firstinspires.ftc.teamcode.AutoCode.Pathings.PathingTrajectoriesRed;
import org.firstinspires.ftc.teamcode.AutoCode.Roadrunner.MecanumDrive;
import org.firstinspires.ftc.teamcode.Hardware.AutoAim;
import org.firstinspires.ftc.teamcode.Hardware.Flywheels;
import org.firstinspires.ftc.teamcode.Hardware.Intake;
import org.firstinspires.ftc.teamcode.Hardware.Transfer;
import org.firstinspires.ftc.teamcode.Hardware.Turret;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Config
@Autonomous(name = "[Competition] [Red] Start at Red Goal, Shoot from Zone One", group = "Autonomous")
public class RedGoalZoneOne extends LinearOpMode {
    // Initialize the Apriltag Detection process
    Intake intake;
    Flywheels flywheels;
    Transfer transfer;
    Turret turret;
    AutoAim autoAim;

    private static final boolean USE_WEBCAM = true;  // Set true to use a webcam, or false for a phone camera
    private static final int DESIRED_TAG_ID = 24;     // Choose the tag you want to approach or set to -1 for ANY tag.
    private static final int DESIRED_TAG_ID2 = 20;     // Choose the tag you want to approach or set to -1 for ANY tag.
    private VisionPortal visionPortal;               // Used to manage the video source.
    private AprilTagProcessor aprilTag;              // Used for managing the AprilTag detection process.
    private AprilTagDetection desiredTag = null;     // Used to hold the data for a detected AprilTag

    boolean targetFound     = false;    // Set to true when an AprilTag target is detected

    @Override
    public void runOpMode() {
        // Initialize the Apriltag Detection process

        Pose2d startPose = new Pose2d(52, 52, Math.toRadians(315));
        MecanumDrive drive = new MecanumDrive(hardwareMap, startPose);

        intake = new Intake(hardwareMap);
        flywheels = new Flywheels(hardwareMap);
        transfer = new Transfer(hardwareMap);

        waitForStart();

        Actions.runBlocking(
                new SequentialAction(
                        PathingTrajectoriesRed.initialFiringFromGoalZoneOne(drive, startPose, intake, flywheels, transfer, telemetry)
                )
        );

        Actions.runBlocking(
                new SequentialAction(
                        PathingTrajectoriesRed.PatternCollection(drive, drive.localizer.getPose(), "PPG", intake)
                )
        );

//        Actions.runBlocking(
//                new SequentialAction(
//                        PathingTrajectoriesRed.openChannel(drive, drive.localizer.getPose(), intake)
//                )
//        );

        Actions.runBlocking(
                new SequentialAction(
                        PathingTrajectoriesRed.firingPositionZoneOne(drive, drive.localizer.getPose(), intake, flywheels, transfer, telemetry)
                )
        );

        Actions.runBlocking(
                new SequentialAction(
                        PathingTrajectoriesRed.PatternCollection(drive, drive.localizer.getPose(), "PGP", intake)
                )
        );

        Actions.runBlocking(
                new SequentialAction(
                        PathingTrajectoriesRed.firingPositionZoneOne(drive, drive.localizer.getPose(), intake, flywheels, transfer, telemetry)
                )
        );

        Actions.runBlocking(
                new SequentialAction(
                        PathingTrajectoriesRed.PatternCollection(drive, drive.localizer.getPose(), "GPP", intake)
                )
        );

        Actions.runBlocking(
                new SequentialAction(
                        PathingTrajectoriesRed.firingPositionZoneOne(drive, drive.localizer.getPose(), intake, flywheels, transfer, telemetry)
                )
        );

        Actions.runBlocking(
                new SequentialAction(
                        PathingTrajectoriesRed.park(drive, drive.localizer.getPose(), flywheels, intake)
                )
        );
    }
}