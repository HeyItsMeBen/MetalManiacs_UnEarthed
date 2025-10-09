package org.firstinspires.ftc.teamcode.AutoCode;

import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.AutoCode.Roadrunner.MecanumDrive;

import org.firstinspires.ftc.robotcore. external.hardware.camera.BuiltinCameraDirection;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.teamcode.AutoCode.Roadrunner.tuning.TuningOpModes;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import java.util.List;

@Autonomous(name = "Auto Pathing", group = "Concept")
//@Disabled
public class Autonomous_Pathing extends LinearOpMode {

    int randomization = 0;
    String motif = " ";

    private static final boolean USE_WEBCAM = true;  // true for webcam, false for phone camera

    private AprilTagProcessor aprilTag;
    public boolean tagScanned = false;

    private VisionPortal visionPortal;

    @Override
    public void runOpMode() {

        Pose2d beginPose = new Pose2d(12, -60, Math.PI / 2);
        MecanumDrive drive = new MecanumDrive(hardwareMap, beginPose);

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
                                    .splineTo(new Vector2d(0, 30), Math.PI)
                                    .splineTo(new Vector2d(0, 60), 0)
                                    .build());

                } else if (motif.equals("PGP")) {
                    telemetry.addData(">", "Running PPG Pathing");

                    waitForStart();
                    Actions.runBlocking(
                            drive.actionBuilder(beginPose)
                                    .splineTo(new Vector2d(48, -11), 0)
                                    .waitSeconds(0.5f)
                                    .splineTo(new Vector2d(15, 20), Math.toRadians(45))
                                    .splineToLinearHeading(new Pose2d(37, 37, Math.toRadians(45)), Math.toRadians(0))
                                    .build());


                } else if (motif.equals("PPG")){
                    telemetry.addData(">", "Running PPG Pathing");

                    waitForStart();
                    Actions.runBlocking(
                            drive.actionBuilder(beginPose)
                                    .splineTo(new Vector2d(48, 13), 0)
                                    .waitSeconds(0.5f)
                                    .setTangent(Math.toRadians(180))
                                    .splineToLinearHeading(new Pose2d(37, 37, Math.toRadians(45)), Math.toRadians(45))
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
            tagScanned = true;
            if (randomization > 0) {
                telemetry.addLine("wow this code is amazing");
                //does nothing
            } else if (detection.id == 21) {
                telemetry.addLine("Motif 1: GPP ");
                randomization = 1;
                motif = "GPP";
            } else if (detection.id == 22) {
                telemetry.addLine("Motif 2: PGP ");
                randomization = 2;
                motif = "PGP";
            } else if (detection.id == 23) {
                telemetry.addLine("Motif 3: PPG ");
                randomization = 3;
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


    }   // end method telemetryAprilTag()

}  // end class

