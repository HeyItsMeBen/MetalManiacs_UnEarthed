package org.firstinspires.ftc.teamcode.DriveCode.DriveCodeClasses;

import com.qualcomm.robotcore.hardware.HardwareMap;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
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

    private boolean targetFound = false;
    private boolean shouldAutoAim = true;

    private long targetLostStartTime = 0;
    private boolean wasTargetFoundLastFrame = false;

    private static final long TARGET_LOST_DELAY_MS = 1000;

    private static final int DESIRED_TAG_ID = 24;
    private static final int DESIRED_TAG_ID2 = 20;

    public AutoAimTurretController(HardwareMap hardwareMap) {

        turret = new Turret(hardwareMap);
        autoAim = new AutoAim(Math.toRadians(15));

        initAprilTag(hardwareMap);
    }

    private void initAprilTag(HardwareMap hardwareMap) {

        aprilTag = new AprilTagProcessor.Builder().build();
        aprilTag.setDecimation(2);

        visionPortal = new VisionPortal.Builder()
                .setCamera(hardwareMap.get(WebcamName.class, "Webcam 1"))
                .addProcessor(aprilTag)
                .build();
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

    public double getDistanceToTag() {
        return autoAim.distanceToTagTelemetry;
    }

    public void resetTurret() {
        turret.resetInitial();
    }

    public void manualControl(boolean left, boolean right) {

        if (left) {
            turret.setMotorPower(-0.5);
        } else if (right) {
            turret.setMotorPower(0.5);
        } else {
            turret.setMotorPower(0);
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

            turret.setMotorPower(autoAim.turn);

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
                    turret.setMotorPower(0);
                }

            } else {
                turret.setMotorPower(0);
            }
        }
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
}
