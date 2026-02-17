package org.firstinspires.ftc.teamcode.AutoCode.Pathings;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.robotcore.external.hardware.camera.BuiltinCameraDirection;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.hardware.camera.controls.ExposureControl;
import org.firstinspires.ftc.robotcore.external.hardware.camera.controls.GainControl;
import org.firstinspires.ftc.teamcode.Hardware.AutoAim;
import org.firstinspires.ftc.teamcode.Hardware.Turret;
import org.firstinspires.ftc.teamcode.Hardware.Lights;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class AprilTagTurretAim {

    private final LinearOpMode opMode;
    private final Turret turret;
    private final AutoAim autoAim;
    private final Lights light;

    private VisionPortal visionPortal;
    private AprilTagProcessor aprilTag;

    private AprilTagDetection desiredTag;
    private boolean targetFound;

    private final boolean useWebcam;
    private final int tag1;
    private final int tag2;

    public AprilTagTurretAim(LinearOpMode opMode, Turret turret, AutoAim autoAim, boolean useWebcam, int tag1, int tag2, Lights lights) {
        this.opMode = opMode;
        this.turret = turret;
        this.autoAim = autoAim;
        this.light = lights;
        this.useWebcam = useWebcam;
        this.tag1 = tag1;
        this.tag2 = tag2;

    }

    public void init() {
        aprilTag = new AprilTagProcessor.Builder().build();
        aprilTag.setDecimation(2);

        VisionPortal.Builder builder = new VisionPortal.Builder()
                .addProcessor(aprilTag);

        if (useWebcam) {
            builder.setCamera(opMode.hardwareMap.get(WebcamName.class, "Webcam 1"));
        } else {
            builder.setCamera(BuiltinCameraDirection.BACK);
        }

        visionPortal = builder.build();
    }

    public void setManualExposure(int exposureMS, int gain) {
        if (visionPortal == null) return;

        while (opMode.opModeIsActive()
                && visionPortal.getCameraState() != VisionPortal.CameraState.STREAMING) {
            // wait
        }

        ExposureControl exposure = visionPortal.getCameraControl(ExposureControl.class);
        GainControl gainControl = visionPortal.getCameraControl(GainControl.class);

        if (exposure.getMode() != ExposureControl.Mode.Manual) {
            exposure.setMode(ExposureControl.Mode.Manual);
        }

        exposure.setExposure(exposureMS, TimeUnit.MILLISECONDS);
        gainControl.setGain(gain);
    }

    public void waitForStreaming() {
        if (visionPortal == null) return;

        while (opMode.opModeIsActive()
                && visionPortal.getCameraState() != VisionPortal.CameraState.STREAMING) {
            // just wait
        }
    }

    public double update() {
        scanForTags();

        if (targetFound) {
            autoAim.calculateEverything(desiredTag);
            turret.setMotorPower(autoAim.turn);
            light.setAprilTagStatus(true);
            return autoAim.launchPointToGoalCenterX_Distance_Inches;

        } else {
            turret.setMotorPower(0);
            return 0;
        }
    }

    public void stopTurret() {
        turret.setMotorPower(0);
    }

    private void scanForTags() {
        targetFound = false;
        desiredTag = null;

        List<AprilTagDetection> detections = aprilTag.getDetections();
        for (AprilTagDetection detection : detections) {
            if (detection.metadata == null) continue;

            if (tag1 < 0 || detection.id == tag1 || detection.id == tag2) {
                desiredTag = detection;
                targetFound = true;
                break;
            }
        }
    }

}
