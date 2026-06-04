package org.firstinspires.ftc.teamcode.Controllers;

import com.acmerobotics.roadrunner.Pose2d;

import static android.os.SystemClock.sleep;

import com.acmerobotics.roadrunner.Vector2d;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.hardware.camera.controls.ExposureControl;
import org.firstinspires.ftc.robotcore.external.hardware.camera.controls.GainControl;
import org.firstinspires.ftc.teamcode.AutoCode.Roadrunner.MecanumDrive;
import org.firstinspires.ftc.teamcode.Hardware.AutoAim;
import org.firstinspires.ftc.teamcode.Hardware.Turret;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class AutoAimTurretController {
    private HardwareMap hMap;

    //Mechanisms
    private Turret turret;
    public AutoAim autoAim;

    //Position-tracking objects
    private MecanumDrive drive;
    private Pose2d initialEstimatedCurrentPose;
    private Vector2d goalPosition;
    public Pose2d robPos;

    //vision objects
    private VisionPortal visionPortal;
    private AprilTagProcessor aprilTag;
    private AprilTagDetection desiredTag = null;
    //private GoBildaPinpointDriver odo;

    //Vision
    private int DESIRED_TAG_ID = 24;
    private boolean shouldAutoAim = true;

    //target lost or found variables
    private boolean targetFound = false;
    private boolean wasTargetFoundLastFrame = false;
    private long targetLostStartTime = 0;
    private static final long TARGET_LOST_DELAY_MS = 4000;


    public boolean opModeIsActive=true;
    private static final boolean USE_WEBCAM =true;
    private boolean cameraAvailable = false;
    public boolean isRed=true;

    //turret
    float turretPower = 0;
    public double turretAngleTelemetry=0;
    double lastTurretAngle=0;

    //ramp-up code + timer
    private float rampUpSpeed = 0.1f; // how fast turret should ramp up to target speed (in seconds)
    double turretStartTime=0;
    double turretStartPower=0;
    double currentTime = 0;

    //unused localization variables
    boolean localized=false;
    double lastLocalized =0;

    public AutoAimTurretController(HardwareMap hardwareMap, Pose2d givenRobotPosition, String givenTeamColor) {

        //Set team color
        if (givenTeamColor.equals("Blue") || givenTeamColor.equals("blue")){
            isRed=false;
            DESIRED_TAG_ID=20;
            goalPosition = new Vector2d(-52, 52); // x, y, heading in radians
        } else {
            isRed=true;
            DESIRED_TAG_ID=24;
            goalPosition = new Vector2d(52, 52); // x, y, heading in radians
        }

        turret = new Turret(hardwareMap);
        autoAim = new AutoAim(Math.toRadians(0));
        drive = new MecanumDrive(hardwareMap, givenRobotPosition);

        initAprilTag(hardwareMap);

        //unused pinpoint code
//        odo = hardwareMap.get(GoBildaPinpointDriver.class, "pinpoint");
//
//        odo.setOffsets(82.55, 0, DistanceUnit.INCH);
//        odo.setEncoderResolution(GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_4_BAR_POD);
//        odo.setEncoderDirections(GoBildaPinpointDriver.EncoderDirection.REVERSED, GoBildaPinpointDriver.EncoderDirection.FORWARD);
//
//        odo.resetPosAndIMU();
//        Pose2D startingPosition = new Pose2D(DistanceUnit.INCH, 0, 0, AngleUnit.RADIANS, 0);
//        odo.setPosition(startingPosition);
        hMap = hardwareMap;
        //drive = new MecanumDrive(hMap, new Pose2d(0,0,Math.toRadians(90)));

        initialEstimatedCurrentPose = new Pose2d(drive.localizer.getPose().position.x, drive.localizer.getPose().position.y, drive.localizer.getPose().heading.toDouble()); // x, y, heading in double radians

        //April tag stuff
        if (USE_WEBCAM) {
            //DO NOT SET GAIN TO ANYTHING HIGHER THAN 255 (it'll go dark)
            setManualExposure(6, 100);  // Use low exposure time to reduce motion blur
        }
    }

    //Retrieve information
    public double getCurrentTime(){
        return currentTime;
    }

    public double getTurretPower(){
        return turret.getTurretPower();
    }

    public float getNeededPower(){
        //convert to milliseconds (variable named poorly but too lazy to change)
        double targetSeconds= rampUpSpeed * 1000; //should take 0.5 seconds to speed up
        currentTime=System.currentTimeMillis()-turretStartTime;
//        currentTime=System.currentTimeMillis();
        if (currentTime < targetSeconds){
            turret.setMotorPower(turretStartPower*((targetSeconds-currentTime)/targetSeconds)+turretPower*(currentTime/targetSeconds));
        }else{
            turret.setMotorPower(turretPower);
        }

        return turretPower;
    }
    public double getDistanceToGoalInches() {
        return autoAim.launchPointToGoalCenterX_Distance_Inches;
    }

    public double getDistanceToGoalMeters() {
        return autoAim.launchPointToGoalCenterX_Distance_Meters;
    }
    public boolean isAutoAiming() {
        return shouldAutoAim;
    }
    public boolean isTargetFound() {
        return targetFound;
    }


    //controls
    public void toggleAutoAim() {
        shouldAutoAim = !shouldAutoAim;
    }
    public void resetTurret() {
        turret.resetInitial();
    }
    public void manualControl(boolean left, boolean right) {

        if (left) {
            turretPower = -0.5f;
            turretStartTime=System.currentTimeMillis();
            turretStartPower=turret.getTurretPower();
            turret.setMotorPower(-0.5);
        } else if (right) {
            turretPower = 0.5f;
            turretStartTime=System.currentTimeMillis();
            turretStartPower=turret.getTurretPower();
            turret.setMotorPower(0.5);
        } else {
            turretPower = 0;
            turretStartTime=System.currentTimeMillis();
            turretStartPower=turret.getTurretPower();
            turret.setMotorPower(0);
        }
    }
    public void turnToCenter() {
//        turret.resetPosition();
//        turret.runTowardTargetDistance(750);
        turret.rotateToPosition(750);

    }

    public void setTurretPower(double Power) {
        turret.setMotorPower(Power);
    }
    public void stopTurret() {
        turretPower = 0;
        turretStartTime=System.currentTimeMillis();
        turretStartPower=turret.getTurretPower();
//        turret.setMotorPower(0);
    }
    public void changeColorTo(String teamColor){
        if (teamColor.equals("Blue") || teamColor.equals("blue")){
            isRed=false;
            DESIRED_TAG_ID=20;
        } else {
            isRed=true;
            DESIRED_TAG_ID=24;
        }
    }

    //"Update" method variations
    @Deprecated
    public void update(boolean manualLeft, boolean manualRight) {

        scanForTags();

        if (!shouldAutoAim) {   //If we should not auto-aim, then run manual controls and skip everything else
            manualControl(manualLeft, manualRight);
            wasTargetFoundLastFrame = false;
            return;
        }

        if (targetFound) {  //Auto-aims using camera when tag is visible

            autoAim.calculateEverything(desiredTag);

            turret.setMotorPower(autoAim.turn);
            turretPower = (float) autoAim.turn;
            turretStartTime=System.currentTimeMillis();
            turretStartPower=turret.getTurretPower();

            wasTargetFoundLastFrame = true;

        } else {    //If tag is not visible...

            if (wasTargetFoundLastFrame) {
                targetLostStartTime = System.currentTimeMillis();
                wasTargetFoundLastFrame = false;
            }

            long timeSinceLost =
                    System.currentTimeMillis() - targetLostStartTime;

            if (timeSinceLost >= TARGET_LOST_DELAY_MS) { //After waiting for a few seconds, start auto-aiming using pinpoint instead of the camera.

                if (!turret.isAtTargetPosition(0)) {
                    turret.rotateTowardsTarget(0);
                } else {
                    turretPower = 0;
                    turret.setMotorPower(0);
                    turretStartTime=System.currentTimeMillis();
                    turretStartPower=turret.getTurretPower();
                }

            } else {    //If we haven't waited long enough, then don't move the turret. Just wait.
                turretPower = 0;
//                turret.setMotorPower(0);
                turretStartTime=System.currentTimeMillis();
                turretStartPower=turret.getTurretPower();
            }
        }
    }
    @Deprecated
    public void update2(boolean manualLeft, boolean manualRight) {  //the actual update method that we currently use (Regionals)

        //turret control
        if (!shouldAutoAim) {
            manualControl(manualLeft, manualRight);
            //wasTargetFoundLastFrame = false;
            return;
        }

//        Pose2D pos = odo.getPosition();
//        double heading = pos.getHeading(AngleUnit.RADIANS);


        //scans and calculates
        scanForTags();
        if (targetFound) {
            autoAim.calculateEverything(desiredTag);
            turret.setMotorPower(-autoAim.turn);
        } else {

            drive.updatePoseEstimate();
            Pose2d RobotPose = drive.localizer.getPose();
            //Pose2d RobotPose = new Pose2d(new Vector2d(12, -45), Math.toRadians(0));
            robPos=RobotPose;

            autoAim.calculateEverythingWithoutCamera(RobotPose, isRed);
            turretAngleTelemetry=autoAim.turretAngle;
            turret.runTowardsTargetAngle(autoAim.turretAngle);
        }
    }
    public void updateWithTimeout(boolean manualLeft, boolean manualRight) {  //method that only uses pinpoint to aim if the tag has been lost for over (x) seconds

        //turret control
        if (!shouldAutoAim) {
            manualControl(manualLeft, manualRight);
            return;
        }



        //scans and calculates
        scanForTags();
        if (targetFound) {
            autoAim.calculateEverything(desiredTag);
            turret.setMotorPower(-autoAim.turn);
            targetLostStartTime=System.currentTimeMillis();
            wasTargetFoundLastFrame = true;
            lastTurretAngle=turret.getTurretAngle();
        }
        long timeSinceLost =
            System.currentTimeMillis() - targetLostStartTime;
        if (timeSinceLost > TARGET_LOST_DELAY_MS) {
            drive.updatePoseEstimate();
            Pose2d RobotPose = drive.localizer.getPose();
            //Pose2d RobotPose = new Pose2d(new Vector2d(12, -45), Math.toRadians(0));
            robPos=RobotPose;

            autoAim.calculateEverythingWithoutCamera(RobotPose, isRed);
            turretAngleTelemetry=autoAim.turretAngle;
            turret.runTowardsTargetAngle(autoAim.turretAngle);
        } else if (!targetFound) {
            turret.runTowardsTargetAngle(lastTurretAngle+Math.toRadians(autoAim.headingError));
        }
    }
    public void updateWithLocalization(boolean manualLeft, boolean manualRight) {  //untested method

        //turret control
        if (!shouldAutoAim) {
            manualControl(manualLeft, manualRight);
            //wasTargetFoundLastFrame = false;
            return;
        }

//        Pose2D pos = odo.getPosition();
//        double heading = pos.getHeading(AngleUnit.RADIANS);


        //scans and calculates
        scanForTags();
        if (targetFound) {
            drive = new MecanumDrive(hMap, autoAim.getRelocalizedPose(desiredTag, isRed));
        }
        drive.updatePoseEstimate();
        Pose2d RobotPose = drive.localizer.getPose();
        robPos=RobotPose;

        autoAim.calculateEverythingWithoutCamera(RobotPose, isRed);

        turretAngleTelemetry=autoAim.turretAngle;
        turret.runTowardsTargetAngle(autoAim.turretAngle);
    }

//    else {
//        if (wasTargetFoundLastFrame) {
//            targetLostStartTime = System.currentTimeMillis();
//            wasTargetFoundLastFrame = false;
//        }
//
//        long timeSinceLost =
//                System.currentTimeMillis() - targetLostStartTime;
//
//        if (timeSinceLost >= TARGET_LOST_DELAY_MS) {
//
//            if (!turret.isAtTargetPosition(0)) {
//                turret.rotateTowardsTarget(0);
//            } else {
//                drive.updatePoseEstimate();
//                Pose2d RobotPose = drive.localizer.getPose();
//                //Pose2d RobotPose = new Pose2d(new Vector2d(12, -45), Math.toRadians(0));
//                robPos=RobotPose;
//
//                autoAim.calculateEverythingWithoutCamera(RobotPose, isRed);
//                turretAngleTelemetry=autoAim.turretAngle;
//                turret.runTowardsTargetAngle(autoAim.turretAngle);
////                    turretPower = 0;
//////                    turret.setMotorPower(0);
////                    turretStartTime=System.currentTimeMillis();
////                    turretStartPower=turret.getTurretPower();
//            }
//
//        } else {
//            turretPower = 0;
////                turret.setMotorPower(0);
//            turretStartTime=System.currentTimeMillis();
//            turretStartPower=turret.getTurretPower();
//        }
//    }

    public void updateTurnGivenPosition(Pose2d providedPose) {

        scanForTags();
        if (targetFound) {
            autoAim.calculateEverything(desiredTag);
            turret.setMotorPower(-autoAim.turn);

        } else {

            Pose2d RobotPose = providedPose;
            robPos = providedPose;

            autoAim.calculateEverythingWithoutCamera(RobotPose, isRed);
            turretAngleTelemetry=autoAim.turretAngle;
            turret.runTowardsTargetAngle(autoAim.turretAngle);
        }
    }

    //Vision
    private void scanForTags() {
        if (!cameraAvailable || aprilTag == null) {
            targetFound = false;
            desiredTag = null;
            return;
        }

        try {
            targetFound = false;
            desiredTag = null;

            List<AprilTagDetection> detections = aprilTag.getDetections();
            for (AprilTagDetection detection : detections) {
                if (detection.metadata != null) {
                    if (detection.id == DESIRED_TAG_ID) {
                        targetFound = true;
                        desiredTag = detection;
                        break;
                    }
                }
            }
        } catch (Exception e) {
            cameraAvailable = false;
            targetFound = false;
            desiredTag = null;
        }
    }
    private void initAprilTag(HardwareMap hardwareMap) {
        try {
            WebcamName webcam = hardwareMap.tryGet(WebcamName.class, "Webcam 1");
            if (webcam == null) {
                cameraAvailable = false;
                return;
            }

            aprilTag = new AprilTagProcessor.Builder().build();
            aprilTag.setDecimation(2);

            visionPortal = new VisionPortal.Builder()
                    .setCamera(webcam)
                    .addProcessor(aprilTag)
                    //.setCameraResolution(new android.util.Size(1280, 720))
                    .build();

            cameraAvailable = true;

        } catch (Exception e) {
            cameraAvailable = false;
        }
    }
    public boolean isCameraAvailable() {
        return cameraAvailable;
    }
    private void setManualExposure(int exposureMS, int gain) {
        if (!cameraAvailable || visionPortal == null) return;

        if (visionPortal.getCameraState() != VisionPortal.CameraState.STREAMING) {
            ElapsedTime cameraTimer = new ElapsedTime();
            while (opModeIsActive &&
                    visionPortal.getCameraState() != VisionPortal.CameraState.STREAMING &&
                    cameraTimer.seconds() < 5.0) {
                sleep(20);
            }
            // If still not streaming after timeout, mark camera as unavailable
            if (visionPortal.getCameraState() != VisionPortal.CameraState.STREAMING) {
                cameraAvailable = false;
                return;
            }
        }

        if (opModeIsActive) {
            try {
                ExposureControl exposureControl = visionPortal.getCameraControl(ExposureControl.class);
                if (exposureControl.getMode() != ExposureControl.Mode.Manual) {
                    exposureControl.setMode(ExposureControl.Mode.Manual);
                }
                exposureControl.setExposure((long) exposureMS, TimeUnit.MILLISECONDS);
                GainControl gainControl = visionPortal.getCameraControl(GainControl.class);
                gainControl.setGain(gain);
            } catch (Exception e) {
                cameraAvailable = false;
            }
        }
    }
    //Shutdown
    public void closeWebcam() {
        if (visionPortal != null) {
            visionPortal.close();
        }
    }
    
    public void shutdown() {
        opModeIsActive = false;
        stopTurret();
        if (visionPortal != null) {
            visionPortal.close();  // This is the critical line
        }
    }
    private double toInches(double meters){
        return meters*39.3700787;
    }
}
