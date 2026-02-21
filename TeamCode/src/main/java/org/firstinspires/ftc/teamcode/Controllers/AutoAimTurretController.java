package org.firstinspires.ftc.teamcode.Controllers;

import static android.os.SystemClock.sleep;

import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.hardware.camera.controls.ExposureControl;
import org.firstinspires.ftc.robotcore.external.hardware.camera.controls.GainControl;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;
import org.firstinspires.ftc.teamcode.Hardware.AutoAim;
import org.firstinspires.ftc.teamcode.Hardware.Turret;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class AutoAimTurretController {
    private boolean cameraAvailable = false;
    private Turret turret;
    private AutoAim autoAim;

    private VisionPortal visionPortal;
    private AprilTagProcessor aprilTag;

    private AprilTagDetection desiredTag = null;
    private GoBildaPinpointDriver odo;

    private boolean targetFound = false;
    private boolean shouldAutoAim = true;

    private long targetLostStartTime = 0;
    private boolean wasTargetFoundLastFrame = false;

    private static final long TARGET_LOST_DELAY_MS = 1000;

    private static final int DESIRED_TAG_ID = 24;
    private static final int DESIRED_TAG_ID2 = 20;

    boolean localized=false;
    double lastLocalized =0;

    public double turretAngleTelemetry=0;

    float turretPower = 0;
    // Ramp up code
    private float rampUpSpeed = 0.1f; // how fast turret should ramp up to target speed (in seconds)
    double turretStartTime=0;
    double turretStartPower=0;
    double currentTime = 0;

    public boolean opModeIsActive=true;
    private static final boolean USE_WEBCAM =true;

    public AutoAimTurretController(HardwareMap hardwareMap) {

        turret = new Turret(hardwareMap);
        autoAim = new AutoAim(Math.toRadians(0));

        initAprilTag(hardwareMap);

        odo = hardwareMap.get(GoBildaPinpointDriver.class, "pinpoint");

        odo.setOffsets(82.55, 0, DistanceUnit.INCH);
        odo.setEncoderResolution(GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_4_BAR_POD);
        odo.setEncoderDirections(GoBildaPinpointDriver.EncoderDirection.REVERSED, GoBildaPinpointDriver.EncoderDirection.FORWARD);

        odo.resetPosAndIMU();
        Pose2D startingPosition = new Pose2D(DistanceUnit.INCH, 0, 0, AngleUnit.RADIANS, 0);
        odo.setPosition(startingPosition);

        //April tag stuff
        if (USE_WEBCAM) {
            //NOTE: gain is 50 for comp field, but 200 for practice field
            setManualExposure(6, 200);  // Use low exposure time to reduce motion blur
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
                    .setCameraResolution(new android.util.Size(320, 240))
                    .build();

            cameraAvailable = true;

        } catch (Exception e) {
            cameraAvailable = false;
        }
    }

//    public void init() {
//        aprilTag = new AprilTagProcessor.Builder().build();
//        aprilTag.setDecimation(2);
//
//        VisionPortal.Builder builder = new VisionPortal.Builder()
//                .addProcessor(aprilTag);
//
//        if (useWebcam) {
//            builder.setCamera(opMode.hardwareMap.get(WebcamName.class, "Webcam 1"));
//        } else {
//            builder.setCamera(BuiltinCameraDirection.BACK);
//        }
//
//        visionPortal = builder.build();
//    }

    public double getCurrentTime(){
        return currentTime;
    }

    public double getTurretPower(){
        return turret.getTurretPower();
    }

    public float getNeededPower(){
        return turretPower;
    }

    public void toggleAutoAim() {
        shouldAutoAim = !shouldAutoAim;
    }

    public boolean isAutoAiming() {
        return shouldAutoAim;
    }

    public boolean isTargetFound() {
        return targetFound;
    }

    public double getDistanceToGoalInches() {
        return autoAim.launchPointToGoalCenterX_Distance_Inches;
    }

    public double getDistanceToGoalMeters() {
        return autoAim.launchPointToGoalCenterX_Distance_Meters;
    }

    public void resetTurret() {
        turret.resetInitial();
    }

    public void manualControl(boolean left, boolean right) {

        if (left) {
            turretPower = -0.5f;
            turretStartTime=System.currentTimeMillis();
            turretStartPower=turret.getTurretPower();
//            turret.setMotorPower(-0.5);
        } else if (right) {
            turretPower = 0.5f;
            turretStartTime=System.currentTimeMillis();
            turretStartPower=turret.getTurretPower();
//            turret.setMotorPower(0.5);
        } else {
            turretPower = 0;
            turretStartTime=System.currentTimeMillis();
            turretStartPower=turret.getTurretPower();
//            turret.setMotorPower(0);
        }
    }

    public void update(boolean manualLeft, boolean manualRight) {

        scanForTags();

        if (!shouldAutoAim) {
            manualControl(manualLeft, manualRight);
            wasTargetFoundLastFrame = false;
            return;
        }

        if (targetFound) {

            autoAim.calculateEverything(desiredTag);

//            turret.setMotorPower(autoAim.turn);
            turretPower = (float) autoAim.turn;
            turretStartTime=System.currentTimeMillis();
            turretStartPower=turret.getTurretPower();

            wasTargetFoundLastFrame = true;

        } else {

            if (wasTargetFoundLastFrame) {
                targetLostStartTime = System.currentTimeMillis();
                wasTargetFoundLastFrame = false;
            }

            long timeSinceLost =
                    System.currentTimeMillis() - targetLostStartTime;

            if (timeSinceLost >= TARGET_LOST_DELAY_MS) {

                if (!turret.isAtTargetPosition(0)) {
                    turret.rotateTowardsTarget(0);
                } else {
                    turretPower = 0;
//                    turret.setMotorPower(0);
                    turretStartTime=System.currentTimeMillis();
                    turretStartPower=turret.getTurretPower();
                }

            } else {
                turretPower = 0;
//                turret.setMotorPower(0);
                turretStartTime=System.currentTimeMillis();
                turretStartPower=turret.getTurretPower();
            }
        }
    }
    public void relocalize(boolean manualLeft, boolean manualRight) {

        //convert to milliseconds (variable named poorly but too lazy to change)
        double targetSeconds= rampUpSpeed * 1000; //should take 0.5 seconds to speed up
        currentTime=System.currentTimeMillis()-turretStartTime;
//        currentTime=System.currentTimeMillis();
        if (currentTime < targetSeconds){
            turret.setMotorPower(turretStartPower*((targetSeconds-currentTime)/targetSeconds)+turretPower*(currentTime/targetSeconds));
        }else{
            turret.setMotorPower(turretPower);
        }

        //turret control
        if (!shouldAutoAim) {
            manualControl(manualLeft, manualRight);
            wasTargetFoundLastFrame = false;
            return;
        }

        //scans every 0.5 seconds or so
        if (!localized){
            scanForTags();
            if (targetFound) {
                autoAim.calculateEverything(desiredTag);
                odo.setPosition(new Pose2D(DistanceUnit.INCH, 75-autoAim.xpos, 78-autoAim.ypos, AngleUnit.RADIANS,autoAim.botAngleThing+Math.toRadians(desiredTag.ftcPose.bearing)));

                wasTargetFoundLastFrame = true;

            }
            localized=true;
            lastLocalized =System.currentTimeMillis();
        } else if (System.currentTimeMillis()- lastLocalized >500) {
            localized=false;
        }
        Pose2D pos = odo.getPosition();
        double heading = pos.getHeading(AngleUnit.RADIANS);
        double newX = Math.sqrt(Math.pow((75 - autoAim.xpos), 2) + Math.pow((78 - autoAim.ypos), 2));
        double turretAngle = Math.atan((78 - autoAim.ypos) / (75 - autoAim.xpos)) - heading;
        turretAngleTelemetry=turretAngle;

        turret.runTowardsTargetAngle(turretAngle);
        //turret.setMotorPower(0.5);
    }

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
                    if (detection.id == DESIRED_TAG_ID || detection.id == DESIRED_TAG_ID2) {
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

    public void stopTurret() {

        turretPower = 0;
        turretStartTime=System.currentTimeMillis();
        turretStartPower=turret.getTurretPower();
//        turret.setMotorPower(0);
    }

    public void shutdown() {
        opModeIsActive = false;
        stopTurret();
        if (visionPortal != null) {
            visionPortal.close();  // This is the critical line
        }
    }
}
