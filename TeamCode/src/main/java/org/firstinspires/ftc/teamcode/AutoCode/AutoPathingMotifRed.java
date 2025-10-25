package org.firstinspires.ftc.teamcode.AutoCode;

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
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import org.firstinspires.ftc.teamcode.Systems.Arm;
import org.firstinspires.ftc.teamcode.Systems.Intake;
import org.firstinspires.ftc.teamcode.Systems.Outtake;
import org.firstinspires.ftc.teamcode.Systems.Hinge;

import java.util.List;

@Disabled
@Autonomous(name = "Auto Pathing Motif Red", group = "Concept")
//@Disabled
public class AutoPathingMotifRed extends LinearOpMode {

    int randomization = 0;
    String motif = " ";

    private static final boolean USE_WEBCAM = true;  // true for webcam, false for phone camera

    private AprilTagProcessor aprilTag;
    public boolean tagScanned = false;

    private VisionPortal visionPortal;

    Intake Intake;
    Arm Aim;
    Outtake Flywheel;
    Hinge hinge;

    @Override
    public void runOpMode() {

        Pose2d beginPose = new Pose2d(12, -60, Math.toRadians(-90));
        MecanumDrive drive = new MecanumDrive(hardwareMap, beginPose);

        Intake = new Intake(hardwareMap);
        Aim = new Arm(hardwareMap);
        Flywheel = new Outtake(hardwareMap);
        hinge = new Hinge(hardwareMap);

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
                                    .strafeToLinearHeading(new Vector2d(12, -50), Math.toRadians(-90))
                                    .strafeToLinearHeading(new Vector2d(12, -45), Math.toRadians(90))

                                    .splineTo(new Vector2d(46, -11-24), 0)

                                    //run intake to pick up balls

                                    .waitSeconds(0.5f)
                                    .setTangent(Math.toRadians(180))
                                    .splineTo(new Vector2d(37, 37), Math.toRadians(45))

                                    //lift and launch all three balls

                                    .build());

                } else if (motif.equals("PGP")) {
                    telemetry.addData(">", "Running PPG Pathing");

                    waitForStart();
                    Actions.runBlocking(
                            drive.actionBuilder(beginPose)
                                    .strafeToLinearHeading(new Vector2d(12, -50), Math.toRadians(-90))
                                    .strafeToLinearHeading(new Vector2d(12, -45), Math.toRadians(90))

                                    .splineTo(new Vector2d(46, -11), 0)

                                    //run intake to pick up balls

                                    .waitSeconds(0.5f)
                                    .setTangent(Math.toRadians(180))
                                    .splineTo(new Vector2d(37, 37), Math.toRadians(45))

                                    //lift and launch all three balls

                                    .build());


                } else if (motif.equals("PPG")){
                    telemetry.addData(">", "Running PPG Pathing");

                    waitForStart();
                    Actions.runBlocking(
                            drive.actionBuilder(beginPose)
                                    .strafeToLinearHeading(new Vector2d(12, -50), Math.toRadians(-90))
                                    .strafeToLinearHeading(new Vector2d(12, -45), Math.toRadians(90))


                                    .splineTo(new Vector2d(48, 13), 0)

                                    //run intake to pick up balls

                                    .waitSeconds(0.5f)
                                    .setTangent(Math.toRadians(180))
                                    .splineTo(new Vector2d(37, 37), Math.toRadians(45))

                                    //lift and launch all three balls

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
            Intake.setMotorPower(0.5);
            sleep(3);
            Intake.setMotorPower(0);
            return false;
        }
    }

    /*public class aimArm implements Action {
        public aimArm(HardwareMap hMap) {}
        public boolean run(@NonNull TelemetryPacket telemtryPacket)  {
            Aim.setArmTarget(1);
            Aim.stopMotor();
            return false;
        }
    }

    public class launchBall implements Action {
        public launchBall(HardwareMap hMap) {}
        public boolean run(@NonNull TelemetryPacket telemetryPacket) {
            Flywheel.fire(1);
            Flywheel.fire(0);
            return false;
        }
    }*/

    public class liftHinge implements Action {
        public liftHinge(HardwareMap hMap) {}
        public boolean run(@NonNull TelemetryPacket telemetryPacket) {
            hinge.liftHinge(10);
            return false;
        }
    }

    public class lowerHinge implements Action {
        public lowerHinge(HardwareMap hMap) {}
        public boolean run(@NonNull TelemetryPacket telemetryPacket) {
            hinge.liftHinge(0);
            return false;
        }
    }
    public class scoreBall implements Action {
        public scoreBall(HardwareMap hMap) {}
        public boolean run(@NonNull TelemetryPacket telemetryPacket) {
            //raise arm. Uncomment these arm lines if you want the arm to move
            //Aim.moveArmTo(400, 1); //400 is the position, measured in encoder counts. This line sets the arm to a position, and then waits a second for the arm to get there.
            //Aim.stopMotor();

            Flywheel.setFlywheelVelocity(2900);  //sets flywheel Velocity to 2900 rpm, and gives it 1 second to speed up.
            sleep(1000);

            //fires the ball, and brings the hinge back to waiting position
            hinge.liftHinge(0.6f);  //pushes the ball into the flywheel. Idk what value it's supposed to be.
            sleep(1000);
            hinge.liftHinge(0.3f);  //puts the hinge back, so it can hold another ball. Idk what value it's supposed to be.
            return false;
        }
    }

}  // end class