package org.firstinspires.ftc.teamcode.DriveCode.Tuning;

import static android.os.SystemClock.sleep;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.arcrobotics.ftclib.controller.PIDController;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.hardware.camera.controls.ExposureControl;
import org.firstinspires.ftc.robotcore.external.hardware.camera.controls.GainControl;
import org.firstinspires.ftc.teamcode.Hardware.AutoAim;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Config
@TeleOp(name = "Turret Camera-Aim Dashboard Tuner", group = "Tuning")
public class TurretCameraAimDashboardTuner extends LinearOpMode {

    public boolean opModeIsActive=true;
    public static double TURN_GAIN   =  0.01 ;   //  Turn Control "Gain".  e.g. Ramp up to 25% power at a 25 degree error. (0.25 / 25.0)
    public static double MAX_AUTO_TURN  = 0.9;   //  Clip the turn speed to this max value (adjust for your robot)

    // Motor
    private DcMotorEx turretMotor;
    private PIDController controller;
    AutoAim autoAim;

    //vision objects
    private VisionPortal visionPortal;
    private AprilTagProcessor aprilTag;
    private AprilTagDetection desiredTag = null;

    //Vision
    private int DESIRED_TAG_ID = 24;
    private boolean targetFound = false;
    private static final boolean USE_WEBCAM =true;
    private boolean cameraAvailable = false;

    @Override
    public void runOpMode() throws InterruptedException {

        // Hardware init
        turretMotor = hardwareMap.get(DcMotorEx.class, "turret");
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
        autoAim = new AutoAim(Math.toRadians(15));
        turretMotor.setDirection(DcMotorSimple.Direction.FORWARD);
        turretMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        turretMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        initAprilTag(hardwareMap);
        //April tag stuff
        if (USE_WEBCAM) {
            //DO NOT SET GAIN TO ANYTHING HIGHER THAN 255 (it'll go dark)
            setManualExposure(6, 100);  // Use low exposure time to reduce motion blur
        }

        waitForStart();
        while (opModeIsActive()) {
            scanForTags();
            if (targetFound){
                autoAim.TURN_GAIN=TURN_GAIN;
                autoAim.MAX_AUTO_TURN=MAX_AUTO_TURN;
                autoAim.calculateEverything(desiredTag);
                turretMotor.setPower(autoAim.turn);
                telemetry.addData("Target Power", -autoAim.turn);
                telemetry.update();
            } else {
                turretMotor.setPower(0);
            }
        }
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
}
