package org.firstinspires.ftc.teamcode.DriveCode.Tuning;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.hardware.camera.BuiltinCameraDirection;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.hardware.camera.controls.ExposureControl;
import org.firstinspires.ftc.robotcore.external.hardware.camera.controls.GainControl;
import org.firstinspires.ftc.teamcode.Hardware.AutoAim;
import org.firstinspires.ftc.teamcode.Hardware.Flywheels;
import org.firstinspires.ftc.teamcode.Hardware.Intake;
import org.firstinspires.ftc.teamcode.Hardware.OuttakeHood;
import org.firstinspires.ftc.teamcode.Hardware.Transfer;
import org.firstinspires.ftc.teamcode.Hardware.Turret;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import java.util.List;
import java.util.concurrent.TimeUnit;

/*
Configurations:
0: frontLeft
1: backLeft
2: frontRight
3: backRight
 */

@Config
@Autonomous(name = "Hood Angle To Distance Regression", group = "Tuning")
public class AngleToDistance_Regression extends LinearOpMode {
    Flywheels flywheel;
    OuttakeHood hood;
    Transfer belt;
    AutoAim autoAim;
    Intake intake;
    Turret turret;

    public static float targetVelocity=500;
    public static double hoodAngle=50;

    //setting PID variables for later calculations
    double integralSum=0;
    public double lastError=0;


    final double tickPerRevolution=28;
    ElapsedTime timer = new ElapsedTime();  //keeps track of time. Used for PID calculations

    //j
    private static final boolean USE_WEBCAM = true;  // Set true to use a webcam, or false for a phone camera
    private static final int DESIRED_TAG_ID = 24;     // Choose the tag you want to approach or set to -1 for ANY tag.
    private static final int DESIRED_TAG_ID2 = 20;     // Choose the tag you want to approach or set to -1 for ANY tag.
    private VisionPortal visionPortal;               // Used to manage the video source.
    private AprilTagProcessor aprilTag;              // Used for managing the AprilTag detection process.
    private AprilTagDetection desiredTag = null;     // Used to hold the data for a detected AprilTag

    boolean targetFound     = false;    // Set to true when an AprilTag target is detected

    @Override
    public void runOpMode() throws InterruptedException{
        // Initialize the Apriltag Detection process
        initAprilTag();

        //telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
        flywheel = new Flywheels(hardwareMap);
        hood = new OuttakeHood(hardwareMap);
        belt = new Transfer(hardwareMap);
        intake = new Intake(hardwareMap);
        autoAim = new AutoAim(Math.toRadians(15));
        turret=new Turret(hardwareMap);

        //April tag stuff
        if (USE_WEBCAM) {
            setManualExposure(6, 250);  // Use low exposure time to reduce motion blur
        }
        waitForStart(); //waits until you start the program from the driver station
        while (opModeIsActive()){   //infinite loop
            scanForTags();
            if (targetFound) {
                autoAim.calculateEverything(desiredTag);
                hood.setAngle(hoodAngle);
                flywheel.setFlywheelVelocity(targetVelocity);
                sleep(2000);
                belt.runTransferDrum(0.6);
                intake.setIntakePower(0.8);
                telemetry.addData("Distance to GoalCenter (inches)", autoAim.launchPointToGoalCenterX_Distance_Inches);
                telemetry.addData("", "");
                telemetry.addData("Target Velocity (ticks per second)", targetVelocity);
                telemetry.addData("Current Velocity", flywheel.getFlywheelVelocity());
                telemetry.addData("", "");
                telemetry.addData("Current Hood Position", hood.getServoPosition());
                telemetry.addData("Current Hood Angle", hood.getAngle());
                telemetry.addData("Auto Aim.turn", autoAim.turn);
                telemetry.update();
            }

        }
    }


    //auto Aim methods
    public void scanForTags(){  //Checks if april tags are on screen, and if so, it sets the desiredTag object to that tag
        targetFound = false;
        desiredTag  = null;

        // Step through the list of detected tags and look for a matching tag
        List<AprilTagDetection> currentDetections = aprilTag.getDetections();
        for (AprilTagDetection detection : currentDetections) {
            // Look to see if we have size info on this tag.
            if (detection.metadata != null) {
                //  Check to see if we want to track towards this tag.
                if ((DESIRED_TAG_ID < 0) || (detection.id == DESIRED_TAG_ID || detection.id == DESIRED_TAG_ID2)) {
                    // Yes, we want to use this tag.
                    targetFound = true;
                    desiredTag = detection;
                    break;  // don't look any further.
                } else {
                    // This tag is in the library, but we do not want to track it right now.
                    telemetry.addData("Skipping", "Tag ID %d is not desired", detection.id);
                }
            } else {
                // This tag is NOT in the library, so we don't have enough information to track to it.
                telemetry.addData("Unknown", "Tag ID %d is not in TagLibrary", detection.id);
            }
        }

        // Tell the driver what we see, and what to do.
        if (targetFound) {
            telemetry.addData("\n>","HOLD Left-Bumper to Drive to Target\n");
            telemetry.addData("Found", "ID %d (%s)", desiredTag.id, desiredTag.metadata.name);
            telemetry.addData("Range",  "%5.1f inches", desiredTag.ftcPose.range);
            telemetry.addData("Bearing","%3.0f degrees", desiredTag.ftcPose.bearing);
            telemetry.addData("Yaw","%3.0f degrees", desiredTag.ftcPose.yaw);
        } else {
            telemetry.addData("\n>","Drive using joysticks to find valid target\n");
        }
    }
    private void initAprilTag() {   //Sets up the april tag and camera stuff. Gets it ready for use.
        // Create the AprilTag processor by using a builder.
        aprilTag = new AprilTagProcessor.Builder().build();

        // Adjust Image Decimation to trade-off detection-range for detection-rate.
        // e.g. Some typical detection data using a Logitech C920 WebCam
        // Decimation = 1 ..  Detect 2" Tag from 10 feet away at 10 Frames per second
        // Decimation = 2 ..  Detect 2" Tag from 6  feet away at 22 Frames per second
        // Decimation = 3 ..  Detect 2" Tag from 4  feet away at 30 Frames Per Second
        // Decimation = 3 ..  Detect 5" Tag from 10 feet away at 30 Frames Per Second
        // Note: Decimation can be changed on-the-fly to adapt during a match.
        aprilTag.setDecimation(2);

        // Create the vision portal by using a builder.
        if (USE_WEBCAM) {
            visionPortal = new VisionPortal.Builder()
                    .setCamera(hardwareMap.get(WebcamName.class, "Webcam 1"))
                    .addProcessor(aprilTag)
                    .build();
        } else {
            visionPortal = new VisionPortal.Builder()
                    .setCamera(BuiltinCameraDirection.BACK)
                    .addProcessor(aprilTag)
                    .build();
        }
    }
    private void setManualExposure(int exposureMS, int gain) {   //not exactly sure what this does. It sets up the camera's setting or something
        // Wait for the camera to be open, then use the controls

        if (visionPortal == null) {
            return;
        }

        // Make sure camera is streaming before we try to set the exposure controls
        if (visionPortal.getCameraState() != VisionPortal.CameraState.STREAMING) {
            telemetry.addData("Camera", "Waiting");
            telemetry.update();
            while (opModeIsActive() && (visionPortal.getCameraState() != VisionPortal.CameraState.STREAMING)) {
                //sleep1(20);
            }
            telemetry.addData("Camera", "Ready");
            telemetry.update();
        }

        // Set camera controls unless we are stopping.
        if (opModeIsActive())
        {
            ExposureControl exposureControl = visionPortal.getCameraControl(ExposureControl.class);
            if (exposureControl.getMode() != ExposureControl.Mode.Manual) {
                exposureControl.setMode(ExposureControl.Mode.Manual);
                //sleep1(50);
            }
            exposureControl.setExposure((long)exposureMS, TimeUnit.MILLISECONDS);
            //sleep1(20);
            GainControl gainControl = visionPortal.getCameraControl(GainControl.class);
            gainControl.setGain(gain);
            //sleep1(20);
        }
    }
}