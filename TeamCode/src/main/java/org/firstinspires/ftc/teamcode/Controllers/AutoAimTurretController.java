package org.firstinspires.ftc.teamcode.Controllers;

import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;
import org.firstinspires.ftc.teamcode.Hardware.AutoAim;
import org.firstinspires.ftc.teamcode.Hardware.Turret;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import java.util.List;

public class AutoAimTurretController {

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
    private float rampUpSpeed = 0.5f; // how fast turret should ramp up to target speed (in seconds)
    double turretStartTime=0;
    double turretStartPower=0;

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
    }

    private void initAprilTag(HardwareMap hardwareMap) {

        aprilTag = new AprilTagProcessor.Builder().build();
        aprilTag.setDecimation(2);

        visionPortal = new VisionPortal.Builder()
                .setCamera(hardwareMap.get(WebcamName.class, "Webcam 1"))
                .addProcessor(aprilTag)
                .build();
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

        double targetSeconds=rampUpSpeed-rampUpSpeed*(turretStartPower/turretPower); //should take 0.5 seconds to speed up
        double currentTime=System.currentTimeMillis()-turretStartTime;

        turret.setMotorPower(turretStartPower*((targetSeconds-currentTime)/targetSeconds)+turretPower*(currentTime/targetSeconds));
    }
    public void relocalize(boolean manualLeft, boolean manualRight) {

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

        targetFound = false;
        desiredTag = null;

        List<AprilTagDetection> detections = aprilTag.getDetections();

        for (AprilTagDetection detection : detections) {

            if (detection.metadata != null) {

                if (detection.id == DESIRED_TAG_ID ||
                    detection.id == DESIRED_TAG_ID2) {

                    targetFound = true;
                    desiredTag = detection;
                    break;
                }
            }
        }
    }

    public void stopTurret() {

        turretPower = 0;
        turretStartTime=System.currentTimeMillis();
        turretStartPower=turret.getTurretPower();
//        turret.setMotorPower(0);
    }
}
