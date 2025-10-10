package org.firstinspires.ftc.teamcode.AutoCode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

//basic imports like motors and opModes
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.hardware.camera.BuiltinCameraDirection;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.teamcode.DriveCode.outtakeFlywheel;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import java.util.List;


//sets mode to autonomous and makes the main class

@Autonomous(name = "AutoTesting", group = "Linear OpMode")
public class AutoTesting extends LinearOpMode {
    //defining variables
    //conveyerBelt belt = new conveyerBelt(hardwareMap);
    outtakeFlywheel flywheel;
    double [] values={0,0,0};


    int randomization = 0;
    String motif = " ";

    private static final boolean USE_WEBCAM = true;  // true for webcam, false for phone camera

    private AprilTagProcessor aprilTag;
    public boolean tagScanned = false;

    private VisionPortal visionPortal;
    @Override
    //This runs when the program is activated
    public void runOpMode() {
        flywheel = new outtakeFlywheel(hardwareMap);
        initAprilTag();
        waitForStart();
        while (opModeIsActive()){
            double givenX=telemetryAprilTag();
            if (givenX!=0){
                givenX=telemetryAprilTag();
                values=flywheel.getValues(givenX);
                telemetry.addLine(String.format("values: " + values[0] + ", " + values[1] + ", " + values[2]));
                telemetry.update();
                sleep(1000);
            }
        }
    }
    /*public void intakeBall(){
        belt.setMotorPower(0);
        belt.setMotorPower(0.25);
        sleep(500);
        belt.setMotorPower(0);
    }
    public void outtakeBall(){
        belt.setMotorPower(0);
        belt.setMotorPower(0.25);
        sleep(500);
        belt.setMotorPower(0);
    }
     */


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
    private double telemetryAprilTag() {
        double xDistance=0;
        List<AprilTagDetection> currentDetections = aprilTag.getDetections();
        telemetry.addData("# AprilTags Detected", currentDetections.size());

        // Step through the list of detections and display info for each one.
        for (AprilTagDetection detection : currentDetections) {
            if (randomization > 0) {
                telemetry.addLine("wow this code is amazing");
                //does nothing
            } else if (detection.id == 24) {
                telemetry.addLine("Motif 1: GPP ");
                randomization = 1;
                tagScanned=true;
                motif = "GPP";
                //break; ?
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
            if (tagScanned){
                xDistance = detection.ftcPose.y;
            }
        }   // end for() loop

        // Add "key" information to telemetry
        telemetry.addLine("\nkey:\nXYZ = X (Right), Y (Forward), Z (Up) dist.");
        telemetry.addLine("PRY = Pitch, Roll & Yaw (XYZ Rotation)");
        telemetry.addLine("RBE = Range, Bearing & Elevation");
        telemetry.update();
        if (xDistance!=0){
            return xDistance;
        }
        else {
            return 0;
        }
    }
}
