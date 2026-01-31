package org.firstinspires.ftc.teamcode.AutoCode.Pathings.TrajectoryPaths.Red;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.SequentialAction;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.AutoCode.Pathings.PathingTrajectoriesRed;
import org.firstinspires.ftc.teamcode.AutoCode.Roadrunner.MecanumDrive;
import org.firstinspires.ftc.teamcode.Hardware.AutoAim;
import org.firstinspires.ftc.teamcode.Hardware.Flywheels;
import org.firstinspires.ftc.teamcode.Hardware.Intake;
import org.firstinspires.ftc.teamcode.Hardware.Transfer;
import org.firstinspires.ftc.teamcode.Hardware.Turret;
import org.firstinspires.ftc.teamcode.AutoCode.Pathings.AprilTagTurretAim;

@Config
@Autonomous(
        name = "[Competition] [Red] Start at Red Wall, Shoot from Zone One",
        group = "Autonomous"
)
public class RedWallZoneOne extends LinearOpMode {

    Intake intake;
    Flywheels flywheels;
    Transfer transfer;
    Turret turret;
    AutoAim autoAim;

    AprilTagTurretAim aprilTagTurretAim;
    ElapsedTime turretTimer;

    @Override
    public void runOpMode() {

        Pose2d startPose = new Pose2d(15, -60, Math.toRadians(0));
        MecanumDrive drive = new MecanumDrive(hardwareMap, startPose);

        intake = new Intake(hardwareMap);
        flywheels = new Flywheels(hardwareMap);
        transfer = new Transfer(hardwareMap);
        turret = new Turret(hardwareMap);
        autoAim = new AutoAim(Math.toRadians(15));

        aprilTagTurretAim = new AprilTagTurretAim(this, turret, autoAim, true, 24, 20);

        aprilTagTurretAim.init();
        aprilTagTurretAim.setManualExposure(6, 250);

        waitForStart();
        if (isStopRequested()) return;

        Actions.runBlocking(
                new SequentialAction(
                        PathingTrajectoriesRed.initialFiringFromWallZoneOne(
                                drive, startPose, intake, flywheels, transfer, telemetry
                        )
                )
        );

        turretTimer = new ElapsedTime();
        while (opModeIsActive() && turretTimer.milliseconds() < 1000) {
            aprilTagTurretAim.update();
        }
        aprilTagTurretAim.stopTurret();

        Actions.runBlocking(
                new SequentialAction(
                        PathingTrajectoriesRed.PatternCollection(
                                drive, drive.localizer.getPose(), "PPG", intake
                        )
                )
        );

        Actions.runBlocking(
                new SequentialAction(
                        PathingTrajectoriesRed.firingPositionZoneOne(
                                drive, drive.localizer.getPose(),
                                intake, flywheels, transfer, telemetry
                        )
                )
        );

        Actions.runBlocking(
                new SequentialAction(
                        PathingTrajectoriesRed.PatternCollection(
                                drive, drive.localizer.getPose(), "PGP", intake
                        )
                )
        );

        Actions.runBlocking(
                new SequentialAction(
                        PathingTrajectoriesRed.firingPositionZoneOne(
                                drive, drive.localizer.getPose(),
                                intake, flywheels, transfer, telemetry
                        )
                )
        );

        Actions.runBlocking(
                new SequentialAction(
                        PathingTrajectoriesRed.PatternCollection(
                                drive, drive.localizer.getPose(), "GPP", intake
                        )
                )
        );

        Actions.runBlocking(
                new SequentialAction(
                        PathingTrajectoriesRed.firingPositionZoneOne(
                                drive, drive.localizer.getPose(),
                                intake, flywheels, transfer, telemetry
                        )
                )
        );

        Actions.runBlocking(
                new SequentialAction(
                        PathingTrajectoriesRed.LongRangePark(
                                drive, drive.localizer.getPose(), flywheels, intake
                        )
                )
        );
    }
}



//package org.firstinspires.ftc.teamcode.AutoCode.Pathings.TrajectoryPaths.Red;
//import com.acmerobotics.dashboard.config.Config;
//import com.acmerobotics.roadrunner.Pose2d;
//import com.acmerobotics.roadrunner.SequentialAction;
//import com.acmerobotics.roadrunner.ftc.Actions;
//import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
//import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
//import com.qualcomm.robotcore.util.ElapsedTime;
//
//import org.firstinspires.ftc.robotcore.external.hardware.camera.BuiltinCameraDirection;
//import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
//import org.firstinspires.ftc.robotcore.external.hardware.camera.controls.ExposureControl;
//import org.firstinspires.ftc.robotcore.external.hardware.camera.controls.GainControl;
//import org.firstinspires.ftc.teamcode.AutoCode.Pathings.PathingTrajectoriesRed;
//import org.firstinspires.ftc.teamcode.AutoCode.Roadrunner.MecanumDrive;
//import org.firstinspires.ftc.teamcode.Hardware.AutoAim;
//import org.firstinspires.ftc.teamcode.Hardware.Flywheels;
//import org.firstinspires.ftc.teamcode.Hardware.Intake;
//import org.firstinspires.ftc.teamcode.Hardware.Transfer;
//import org.firstinspires.ftc.teamcode.Hardware.Turret;
//import org.firstinspires.ftc.vision.VisionPortal;
//import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
//import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;
//
//import java.util.List;
//import java.util.concurrent.TimeUnit;
//
//@Config
//@Autonomous(name = "[Competition] [Red] Start at Red Wall, Shoot from Zone One", group = "Autonomous")
//public class RedWallZoneOne extends LinearOpMode {
//
//    Intake intake;
//    Flywheels flywheels;
//    Transfer transfer;
//    Turret turret;
//    AutoAim autoAim;
//
//    ElapsedTime turretTimer;
//
//    private static final boolean USE_WEBCAM = true;  // Set true to use a webcam, or false for a phone camera
//    private static final int DESIRED_TAG_ID = 24;     // Choose the tag you want to approach or set to -1 for ANY tag.
//    private static final int DESIRED_TAG_ID2 = 20;     // Choose the tag you want to approach or set to -1 for ANY tag.
//    private VisionPortal visionPortal;               // Used to manage the video source.
//    private AprilTagProcessor aprilTag;              // Used for managing the AprilTag detection process.
//    private AprilTagDetection desiredTag = null;     // Used to hold the data for a detected AprilTag
//
//    boolean targetFound     = false;    // Set to true when an AprilTag target is detected
//
//    @Override
//    public void runOpMode() {
//        // Initialize the Apriltag Detection process
//        initAprilTag();
//
//        Pose2d startPose = new Pose2d(15, -60, Math.toRadians(0)); // x, y, heading in radians
//        MecanumDrive drive = new MecanumDrive(hardwareMap, startPose);
//
//        intake = new Intake(hardwareMap);
//        flywheels = new Flywheels(hardwareMap);
//        transfer = new Transfer(hardwareMap);
//        turret = new Turret(hardwareMap);
//        autoAim = new AutoAim(Math.toRadians(15));
//
//        //April tag stuff
//        if (USE_WEBCAM) {
//            setManualExposure(6, 250);  // Use low exposure time to reduce motion blur
//        }
//
//        waitForStart();
//
//        Actions.runBlocking(
//                new SequentialAction(
//                        PathingTrajectoriesRed.initialFiringFromWallZoneOne(drive, startPose, intake, flywheels, transfer, telemetry)
//                )
//        );
//        turretTimer = new ElapsedTime();
//        while (turretTimer.milliseconds()<1000) {
//            scanForTags();
//            if (targetFound) {
//                autoAim.calculateEverything(desiredTag);
//                turret.setMotorPower(autoAim.turn);
//            } else {
//                turret.setMotorPower(0);
//            }
//        }
//        turret.setMotorPower(0);
//
//        Actions.runBlocking(
//                new SequentialAction(
//                        PathingTrajectoriesRed.PatternCollection(drive, drive.localizer.getPose(), "PPG", intake)
//                )
//        );
//
////        Actions.runBlocking(
////                new SequentialAction(
////                        PathingTrajectoriesRed.openChannel(drive, drive.localizer.getPose(), intake)
////                )
////        );
//
//        Actions.runBlocking(
//                new SequentialAction(
//                        PathingTrajectoriesRed.firingPositionZoneOne(drive, drive.localizer.getPose(), intake, flywheels, transfer, telemetry)
//                )
//        );
//
//        Actions.runBlocking(
//                new SequentialAction(
//                        PathingTrajectoriesRed.PatternCollection(drive, drive.localizer.getPose(), "PGP", intake)
//                )
//        );
//
//        Actions.runBlocking(
//                new SequentialAction(
//                        PathingTrajectoriesRed.firingPositionZoneOne(drive, drive.localizer.getPose(), intake, flywheels, transfer, telemetry)
//                )
//        );
//
//        Actions.runBlocking(
//                new SequentialAction(
//                        PathingTrajectoriesRed.PatternCollection(drive, drive.localizer.getPose(), "GPP", intake)
//                )
//        );
//
//        Actions.runBlocking(
//                new SequentialAction(
//                        PathingTrajectoriesRed.firingPositionZoneOne(drive, drive.localizer.getPose(), intake, flywheels, transfer, telemetry)
//                )
//        );
//
//        Actions.runBlocking(
//                new SequentialAction(
//                        PathingTrajectoriesRed.LongRangePark(drive, drive.localizer.getPose(), flywheels, intake)
//                )
//        );
//    }
//    public void scanForTags(){  //Checks if april tags are on screen, and if so, it sets the desiredTag object to that tag
//        targetFound = false;
//        desiredTag  = null;
//
//        // Step through the list of detected tags and look for a matching tag
//        List<AprilTagDetection> currentDetections = aprilTag.getDetections();
//        for (AprilTagDetection detection : currentDetections) {
//            // Look to see if we have size info on this tag.
//            if (detection.metadata != null) {
//                //  Check to see if we want to track towards this tag.
//                if ((DESIRED_TAG_ID < 0) || (detection.id == DESIRED_TAG_ID || detection.id == DESIRED_TAG_ID2)) {
//                    // Yes, we want to use this tag.
//                    targetFound = true;
//                    desiredTag = detection;
//                    break;  // don't look any further.
//                } else {
//                    // This tag is in the library, but we do not want to track it right now.
//                    telemetry.addData("Skipping", "Tag ID %d is not desired", detection.id);
//                }
//            } else {
//                // This tag is NOT in the library, so we don't have enough information to track to it.
//                telemetry.addData("Unknown", "Tag ID %d is not in TagLibrary", detection.id);
//            }
//        }
//    }
//    private void initAprilTag() {   //Sets up the april tag and camera stuff. Gets it ready for use.
//        // Create the AprilTag processor by using a builder.
//        aprilTag = new AprilTagProcessor.Builder().build();
//
//        // Adjust Image Decimation to trade-off detection-range for detection-rate.
//        // e.g. Some typical detection data using a Logitech C920 WebCam
//        // Decimation = 1 ..  Detect 2" Tag from 10 feet away at 10 Frames per second
//        // Decimation = 2 ..  Detect 2" Tag from 6  feet away at 22 Frames per second
//        // Decimation = 3 ..  Detect 2" Tag from 4  feet away at 30 Frames Per Second
//        // Decimation = 3 ..  Detect 5" Tag from 10 feet away at 30 Frames Per Second
//        // Note: Decimation can be changed on-the-fly to adapt during a match.
//        aprilTag.setDecimation(2);
//
//        // Create the vision portal by using a builder.
//        if (USE_WEBCAM) {
//            visionPortal = new VisionPortal.Builder()
//                    .setCamera(hardwareMap.get(WebcamName.class, "Webcam 1"))
//                    .addProcessor(aprilTag)
//                    .build();
//        } else {
//            visionPortal = new VisionPortal.Builder()
//                    .setCamera(BuiltinCameraDirection.BACK)
//                    .addProcessor(aprilTag)
//                    .build();
//        }
//    }
//    private void setManualExposure(int exposureMS, int gain) {   //not exactly sure what this does. It sets up the camera's setting or something
//        // Wait for the camera to be open, then use the controls
//
//        if (visionPortal == null) {
//            return;
//        }
//
//        // Make sure camera is streaming before we try to set the exposure controls
//        if (visionPortal.getCameraState() != VisionPortal.CameraState.STREAMING) {
//            telemetry.addData("Camera", "Waiting");
//            telemetry.update();
//            while (opModeIsActive() && (visionPortal.getCameraState() != VisionPortal.CameraState.STREAMING)) {
//                //sleep(20);
//            }
//            telemetry.addData("Camera", "Ready");
//            telemetry.update();
//        }
//
//        // Set camera controls unless we are stopping.
//        if (opModeIsActive())
//        {
//            ExposureControl exposureControl = visionPortal.getCameraControl(ExposureControl.class);
//            if (exposureControl.getMode() != ExposureControl.Mode.Manual) {
//                exposureControl.setMode(ExposureControl.Mode.Manual);
//            }
//            exposureControl.setExposure((long)exposureMS, TimeUnit.MILLISECONDS);
//            GainControl gainControl = visionPortal.getCameraControl(GainControl.class);
//            gainControl.setGain(gain);
//        }
//    }
//}
