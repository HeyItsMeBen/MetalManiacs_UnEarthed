package org.firstinspires.ftc.teamcode.DriveCode.TestFiles;

import android.util.Size;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.hardware.camera.controls.ExposureControl;
import org.firstinspires.ftc.robotcore.external.hardware.camera.controls.GainControl;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Disabled
@TeleOp(name = "Camera Test", group = "Test")
public class CameraTest extends LinearOpMode {

    private VisionPortal visionPortal;
    private AprilTagProcessor aprilTag;

    // ── Tune these if needed ──────────────────────────────────────────────────
    private static final int    CAM_WIDTH      = 320;
    private static final int    CAM_HEIGHT     = 240;
    private static final int    EXPOSURE_MS    = 6;
    private static final int    GAIN           = 200;   // 50 comp field, 200 practice
    private static final double STREAM_TIMEOUT = 10.0;  // seconds to wait for stream
    // ─────────────────────────────────────────────────────────────────────────

    @Override
    public void runOpMode() {

        telemetry.addLine("Initializing camera...");
        telemetry.update();

        boolean cameraReady = initCamera();

        if (!cameraReady) {
            telemetry.addLine("CAMERA FAILED TO INITIALIZE.");
            telemetry.addLine("Check USB cable and port, then restart.");
            telemetry.update();
            // Still wait for start so you can read the error on the DS
            waitForStart();
            return;
        }

        telemetry.addLine("Camera initialized! Waiting for stream...");
        telemetry.update();

        // Wait for streaming with timeout
        ElapsedTime streamTimer = new ElapsedTime();
        while (opModeInInit() &&
                visionPortal.getCameraState() != VisionPortal.CameraState.STREAMING &&
                streamTimer.seconds() < STREAM_TIMEOUT) {
            sleep(50);
        }

        if (visionPortal.getCameraState() != VisionPortal.CameraState.STREAMING) {
            telemetry.addLine("CAMERA TIMED OUT — never reached STREAMING state.");
            telemetry.addLine("Likely a USB power issue. Try a powered USB hub.");
            telemetry.update();
            waitForStart();
            return;
        }

        // Apply manual exposure to reduce motion blur
        setManualExposure(EXPOSURE_MS, GAIN);

        telemetry.addLine("Camera streaming! Press Play to start detection loop.");
        telemetry.update();

        waitForStart();

        // ── Main loop ────────────────────────────────────────────────────────
        while (opModeIsActive()) {

            VisionPortal.CameraState state = visionPortal.getCameraState();

            telemetry.addData("Camera State", state);
            telemetry.addData("Resolution", CAM_WIDTH + "x" + CAM_HEIGHT);
            telemetry.addData("Exposure (ms)", EXPOSURE_MS);
            telemetry.addData("Gain", GAIN);

            if (state == VisionPortal.CameraState.STREAMING) {
                List<AprilTagDetection> detections = aprilTag.getDetections();
                telemetry.addData("AprilTags detected", detections.size());

                for (AprilTagDetection d : detections) {
                    if (d.metadata != null) {
                        telemetry.addLine(String.format(
                                "  ID %d | %s | Range=%.1f\" Bearing=%.1f°",
                                d.id, d.metadata.name,
                                d.ftcPose.range, d.ftcPose.bearing));
                    } else {
                        telemetry.addData("  ID (no metadata)", d.id);
                    }
                }

                if (detections.isEmpty()) {
                    telemetry.addLine("  No tags in view.");
                }

            } else {
                // Camera dropped out during the run
                telemetry.addLine("WARNING: Camera is no longer streaming!");
                telemetry.addLine("USB power issue suspected.");
            }

            telemetry.addLine("────────────────────────────");
            telemetry.addLine("Press X to close camera portal");
            if (gamepad1.x) {
                visionPortal.close();
                telemetry.addLine("Portal closed.");
                telemetry.update();
                break;
            }

            telemetry.update();
            sleep(50);
        }

        if (visionPortal != null) {
            visionPortal.close();
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private boolean initCamera() {
        try {
            WebcamName webcam = hardwareMap.tryGet(WebcamName.class, "Webcam");
            if (webcam == null) {
                telemetry.addLine("ERROR: 'Webcam 1' not found in hardware config.");
                telemetry.update();
                return false;
            }

            aprilTag = new AprilTagProcessor.Builder().build();
            aprilTag.setDecimation(2);

            visionPortal = new VisionPortal.Builder()
                    .setCamera(webcam)
                    .setCameraResolution(new Size(CAM_WIDTH, CAM_HEIGHT))
                    .addProcessor(aprilTag)
                    .build();

            return true;

        } catch (Exception e) {
            telemetry.addData("Camera init exception", e.getMessage());
            telemetry.update();
            return false;
        }
    }

    private void setManualExposure(int exposureMS, int gain) {
        if (visionPortal == null) return;
        try {
            ExposureControl exposure = visionPortal.getCameraControl(ExposureControl.class);
            if (exposure.getMode() != ExposureControl.Mode.Manual) {
                exposure.setMode(ExposureControl.Mode.Manual);
                sleep(50);
            }
            exposure.setExposure((long) exposureMS, TimeUnit.MILLISECONDS);
            sleep(20);
            GainControl gainControl = visionPortal.getCameraControl(GainControl.class);
            gainControl.setGain(gain);
        } catch (Exception e) {
            telemetry.addData("Exposure control failed", e.getMessage());
        }
    }
}