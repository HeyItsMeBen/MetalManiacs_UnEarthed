package org.firstinspires.ftc.teamcode.AutoCode.Pathings.Red;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.acmerobotics.roadrunner.Action;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.Roadrunner.MecanumDrive;

import org.firstinspires.ftc.robotcore. external.hardware.camera.BuiltinCameraDirection;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.teamcode.Systems.Transfer;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import org.firstinspires.ftc.teamcode.Systems.Intake;
import org.firstinspires.ftc.teamcode.Systems.Flywheels;

import java.util.List;

@Autonomous(name = "Competition Pathing: Auto Motif Red", group = "Auto Pathing")
//@Disabled
public class AutoPathingMotifRed extends LinearOpMode {

    int randomization = 0;
    String motif = " ";

    private static final boolean USE_WEBCAM = true;  // true for webcam, false for phone camera

    private AprilTagProcessor aprilTag;
    public boolean tagScanned = false;

    private VisionPortal visionPortal;

    Intake intake;
    Flywheels outtake;
    Transfer intakeHinge;
    Transfer outtakeHinge;

    double firing_position_x = 15;
    double firing_position_y = 15;

    @Override
    public void runOpMode() {

        Pose2d beginPose = new Pose2d(15, -60, Math.toRadians(-90));
        MecanumDrive drive = new MecanumDrive(hardwareMap, beginPose);

        intake = new Intake(hardwareMap);
        outtake = new Flywheels(hardwareMap);
        intakeHinge = new Transfer(hardwareMap);
        outtakeHinge = new Transfer(hardwareMap);

        initAprilTag();

        // Wait for the DS start button to be touched.
        telemetry.addData("DS preview on/off", "3 dots, Camera Stream");
        telemetry.addData(">", "Touch START to start OpMode");
        telemetry.update();
        waitForStart();

        if (opModeIsActive()) {
            while (opModeIsActive() && tagScanned == false) {

                telemetryAprilTag();

                if (motif.equals("GPP")) {
                    telemetry.addData(">", "Running PPG Pathing");

                    waitForStart();
                    Actions.runBlocking(
                            drive.actionBuilder(beginPose)

                                    .stopAndAdd(new runIntake(hardwareMap))

                                    .splineTo(new Vector2d(46, -35), 0)

                                    .waitSeconds(0.5f)
                                    .stopAndAdd(new stopIntake(hardwareMap))
                                    .setTangent(Math.toRadians(180))
                                    .splineTo(new Vector2d(20, 20), Math.toRadians(45))
                                    .strafeTo(new Vector2d(firing_position_x, firing_position_y))

                                    .stopAndAdd(new runFlywheels(hardwareMap))

                                    .stopAndAdd(new scoreBallSequence(hardwareMap))
                                    .stopAndAdd(new scoreBallSequence(hardwareMap))
                                    .stopAndAdd(new scoreBallSequence(hardwareMap))

                                    .stopAndAdd(new stopFlywheels(hardwareMap))

                                    .strafeTo(new Vector2d(15, -40))

                                    .build());

                } else if (motif.equals("PGP")) {
                    telemetry.addData(">", "Running PPG Pathing");

                    waitForStart();
                    Actions.runBlocking(
                            drive.actionBuilder(beginPose)

                                    .stopAndAdd(new runIntake(hardwareMap))

                                    .splineTo(new Vector2d(46, -11), 0)

                                    .waitSeconds(0.5f)
                                    .stopAndAdd(new stopIntake(hardwareMap))
                                    .strafeToLinearHeading(new Vector2d(firing_position_x, firing_position_y), Math.toRadians(225))

                                    .stopAndAdd(new runFlywheels(hardwareMap))

                                    .stopAndAdd(new scoreBallSequence(hardwareMap))
                                    .stopAndAdd(new scoreBallSequence(hardwareMap))
                                    .stopAndAdd(new scoreBallSequence(hardwareMap))

                                    .stopAndAdd(new stopFlywheels(hardwareMap))

                                    .strafeTo(new Vector2d(15, -40))

                                    .build());

                } else if (motif.equals("PPG")){
                    telemetry.addData(">", "Running PPG Pathing");

                    waitForStart();
                    Actions.runBlocking(
                            drive.actionBuilder(beginPose)

                                    .stopAndAdd(new runIntake(hardwareMap))

                                    .splineTo(new Vector2d(46, 13), 0)

                                    .waitSeconds(0.5f)
                                    .stopAndAdd(new stopIntake(hardwareMap))
                                    .strafeToLinearHeading(new Vector2d(firing_position_x, firing_position_y), Math.toRadians(225))

                                    .stopAndAdd(new runFlywheels(hardwareMap))

                                    .stopAndAdd(new scoreBallSequence(hardwareMap))
                                    .stopAndAdd(new scoreBallSequence(hardwareMap))
                                    .stopAndAdd(new scoreBallSequence(hardwareMap))

                                    .stopAndAdd(new stopFlywheels(hardwareMap))

                                    .strafeTo(new Vector2d(15, -40))

                                    .build());

                }

                // Share the CPU.
                sleep(20);
            }
        }

//         Save more CPU resources when camera is no longer needed.
        visionPortal.close();

    }   // end method runOpMode()


    private void initAprilTag() {

        // Create the AprilTag processor.
        aprilTag = new AprilTagProcessor.Builder()

                .build();

        VisionPortal.Builder builder = new VisionPortal.Builder();

        // Set the camera (webcam vs. built-in RC phone camera).
        if (USE_WEBCAM) {
            builder.setCamera(hardwareMap.get(WebcamName.class, "Webcam 1"));
        } else {
            builder.setCamera(BuiltinCameraDirection.BACK);
        }

        builder.addProcessor(aprilTag);

        // Build the Vision Portal, using the above settings.
        visionPortal = builder.build();

    }   // end method initAprilTag()


    private void telemetryAprilTag() {

        List<AprilTagDetection> currentDetections = aprilTag.getDetections();
        telemetry.addData("# AprilTags Detected", currentDetections.size());

        // Step through the list of detections and display info for each one.
        for (AprilTagDetection detection : currentDetections) {
            if (randomization > 0) {
                telemetry.addLine("wow this code is amazing");
                //does nothing
            } else if (detection.id == 21) {
                telemetry.addLine("Motif 1: GPP ");
                randomization = 1;
                tagScanned=true;
                motif = "GPP";
                //break; ?
            } else if (detection.id == 22) {
                telemetry.addLine("Motif 2: PGP ");
                randomization = 2;
                tagScanned=true;
                motif = "PGP";
            } else if (detection.id == 23) {
                telemetry.addLine("Motif 3: PPG ");
                randomization = 3;
                tagScanned=true;
                motif = "PPG";
            } else {
                telemetry.addLine("No motif found: unknown");
                randomization = 0;
                motif = "none";
            }
            telemetry.addLine("Motif: " + motif);
            telemetry.addLine("Randomization: " + randomization);
            if (detection.metadata != null) {
                telemetry.addLine(String.format("\n==== (ID %d) %s", detection.id, detection.metadata.name));
                telemetry.addLine(String.format("XYZ %6.1f %6.1f %6.1f  (inch)", detection.ftcPose.x, detection.ftcPose.y, detection.ftcPose.z));
                telemetry.addLine(String.format("PRY %6.1f %6.1f %6.1f  (deg)", detection.ftcPose.pitch, detection.ftcPose.roll, detection.ftcPose.yaw));
                telemetry.addLine(String.format("RBE %6.1f %6.1f %6.1f  (inch, deg, deg)", detection.ftcPose.range, detection.ftcPose.bearing, detection.ftcPose.elevation));
            } else {
                telemetry.addLine(String.format("\n==== (ID %d) Unknown", detection.id));
                telemetry.addLine(String.format("Center %6.0f %6.0f   (pixels)", detection.center.x, detection.center.y));
            }
        }   // end for() loop

        // Add "key" information to telemetry
        telemetry.addLine("\nkey:\nXYZ = X (Right), Y (Forward), Z (Up) dist.");
        telemetry.addLine("PRY = Pitch, Roll & Yaw (XYZ Rotation)");
        telemetry.addLine("RBE = Range, Bearing & Elevation");
        telemetry.update();

    }   // end method telemetryAprilTag()

    public class runIntake implements Action {
        public runIntake(HardwareMap hMap) {}
        public boolean run(@NonNull TelemetryPacket telemtryPacket)  {

            intakeHinge.intakeHingeStandby();

            sleep(250);

            intake.setMotorPower(0.5);

            return false;
        }
    }

    public class stopIntake implements Action {
        public stopIntake(HardwareMap hMap) {}
        public boolean run(@NonNull TelemetryPacket telemtryPacket)  {

            intake.setMotorPower(0);

            return false;
        }
    }

    public class runFlywheels implements Action {
        public runFlywheels(HardwareMap hMap) {
        }
        public boolean run(@NonNull TelemetryPacket telemetryPacket) {

            outtake.setFlywheelVelocity(2350);

            while (outtake.getCurrentWheelVelocity("left") < 2300) {
                sleep(500);
            }

            return false;
        }
    }

    public class stopFlywheels implements Action {
        public stopFlywheels(HardwareMap hMap) {
        }
        public boolean run(@NonNull TelemetryPacket telemetryPacket) {

            outtake.setFlywheelVelocity(0);

            return false;
        }
    }

    public class scoreBallSequence implements Action {
        public scoreBallSequence(HardwareMap hMap) {}
        public boolean run(@NonNull TelemetryPacket telemetryPacket) {

            outtakeHinge.outtakeHingeFire();
            intakeHinge.intakeHingeStandby();

            sleep(500);

            outtakeHinge.outtakeHingeRelax();

            sleep(500);

            intakeHinge.intakeHingeLift();

            sleep(500);

            return false;
        }
    }

}  // end class