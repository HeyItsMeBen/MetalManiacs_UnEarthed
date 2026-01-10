package org.firstinspires.ftc.teamcode.AutoCode.Testing;

import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

//basic imports like motors and opModes
import com.qualcomm.robotcore.eventloop.opmode.Disabled;

import org.firstinspires.ftc.robotcore.external.hardware.camera.BuiltinCameraDirection;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.teamcode.Hardware.Flywheels;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import java.util.List;


//sets mode to autonomous and makes the main class
@Disabled
@Autonomous(name = "AutoTesting", group = "Linear OpMode")
public class AutoTesting extends LinearOpMode {
    //defining variables
    //conveyerBelt belt = new conveyerBelt(hardwareMap);
    Flywheels flywheel;
    GoBildaPinpointDriver odo;
    RotationMatrices rotationMatrices;

    private DcMotor frontLeftDrive = null;  //  Used to control the left front drive wheel
    private DcMotor frontRightDrive = null;  //  Used to control the right front drive wheel
    private DcMotor backLeftDrive = null;  //  Used to control the left back drive wheel
    private DcMotor backRightDrive = null;  //  Used to control the right back drive wheel


    int randomization = 0;
    String motif = " ";

    private static final boolean USE_WEBCAM = true;  // true for webcam, false for phone camera

    private AprilTagProcessor aprilTag;
    public boolean tagScanned = false;

    double xCameraOffset=0; //sideways distance from webcam to robotCenter
    double yCameraOffset=0; // back/forth distance from webcam to robotCenter
    double yDistance=0;
    double tagYaw=0;
    double tagPitch=0;
    double tagRoll=0;
    double tagElevation=0;
    double cameraPitch=22.5;

    private VisionPortal visionPortal;
    @Override
    //This runs when the program is activated
    public void runOpMode() {
        flywheel = new Flywheels(hardwareMap);
        frontLeftDrive = hardwareMap.get(DcMotor.class, "frontLeft");
        frontRightDrive = hardwareMap.get(DcMotor.class, "frontRight");
        backLeftDrive = hardwareMap.get(DcMotor.class, "backLeft");
        backRightDrive = hardwareMap.get(DcMotor.class, "backRight");

        // To drive forward, most robots need the motor on one side to be reversed, because the axles point in opposite directions.
        // When run, this OpMode should start both motors driving forward. So adjust these two lines based on your first test drive.
        // Note: The settings here assume direct drive on left and right wheels.  Gear Reduction or 90 Deg drives may require direction flips
        backLeftDrive.setDirection(DcMotor.Direction.FORWARD);
        frontLeftDrive.setDirection(DcMotor.Direction.FORWARD);
        backRightDrive.setDirection(DcMotor.Direction.REVERSE);
        frontRightDrive.setDirection(DcMotor.Direction.REVERSE);
        /*odo = hardwareMap.get(GoBildaPinpointDriver.class, "pinpoint");

        odo.setOffsets(82.55, 0, DistanceUnit.INCH);
        odo.setEncoderResolution(GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_4_BAR_POD);
        odo.setEncoderDirections(GoBildaPinpointDriver.EncoderDirection.REVERSED, GoBildaPinpointDriver.EncoderDirection.FORWARD);

        odo.resetPosAndIMU();
        Pose2D startingPosition = new Pose2D(DistanceUnit.INCH, 0, 0, AngleUnit.RADIANS, 0);
        odo.setPosition(startingPosition);*/
        rotationMatrices = new RotationMatrices();

        initAprilTag();
        waitForStart();
        while (opModeIsActive()){
            telemetryAprilTag();
            if (tagScanned) {
                double[] tagEulerAngles=rotationMatrices.getActualYaw(Math.toRadians(tagYaw), Math.toRadians(tagPitch), Math.toRadians(tagRoll), Math.toRadians(cameraPitch));
                telemetry.addData("Tag Yaw", Math.toDegrees(tagEulerAngles[0]));
                telemetry.addData("Tag Pitch", Math.toDegrees(tagEulerAngles[1]));
                telemetry.addData("Tag Roll", Math.toDegrees(tagEulerAngles[2]));
            }
            telemetry.update();

            //Pose2D pos = odo.getPosition();
            //double heading = pos.getHeading(AngleUnit.RADIANS);

            /*telemetryAprilTag();
            double givenY=yDistance; //the x-y coordinates directly from camera will need to be adjusted and set to robotCenter. GivenY is distance from camera to basket.
            if (givenY!=0){
                //flywheel.getValues(givenY-yCameraOffset);
                cameraPitch=45;
                tagElevation=0;
                tagTilt=0;
                flywheel.calculateEverything(toMeters(givenY), Math.toRadians(tagTilt), Math.toRadians(tagElevation), Math.toRadians(cameraPitch));    //NOTE: before calculateEverything() you will NEED to rotate the robot so the aprilTag is directly in front of it (centered).

                //theses 4 values (wheelVelocity, launchAngle, and moveBackValue) will be the values we will use directly to control the robot.
                telemetry.addLine(String.format("velocity: " + flywheel.outtakeFlywheelValues.wheelVelocity));
                telemetry.addLine(String.format("launchAngle: " + flywheel.outtakeFlywheelValues.launchAngle));
                telemetry.addLine(String.format("moveBack: " + flywheel.outtakeFlywheelValues.moveBackValue));
                telemetry.addLine(String.format("moveBack: " + flywheel.outtakeFlywheelValues.moveBackValue));

                //other telemetry
                telemetry.addLine(String.format("\n\nballVelocity original: " + flywheel.outtakeFlywheelValues.ballVelocityOg));
                telemetry.addLine(String.format("\n\nballVelocity new: " + flywheel.outtakeFlywheelValues.ballVelocity));
                telemetry.addLine(String.format("distance to basket (X): " + flywheel.outtakeFlywheelValues.basketXTelemetryOg));
                telemetry.addLine(String.format("height of basket (Y): " + flywheel.outtakeFlywheelValues.basketYTelemetryOg));
                telemetry.addLine(String.format("height of vertex (Y): " + flywheel.outtakeFlywheelValues.vertexHeightTelemetryOg));

                telemetry.update();
                sleep(1000);
                //flywheel.setOuttakeVelocity(2900);
                sleep(30000);
            }*/
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
    private void telemetryAprilTag() {
        List<AprilTagDetection> currentDetections = aprilTag.getDetections();
        telemetry.addData("# AprilTags Detected", currentDetections.size());

        // Step through the list of detections and display info for each one.
        for (AprilTagDetection detection : currentDetections) {
            if (randomization > 0) {
                telemetry.addLine("wow this code is amazing");
                tagScanned=true;
                //does nothing
            } else if (detection.id > 20 && detection.id <25) {
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
                yDistance = detection.ftcPose.range;
                tagYaw = detection.ftcPose.yaw; //When robot rotates, it changes yaw. Js so u know which one yaw is.
                tagPitch = detection.ftcPose.pitch;
                tagRoll = detection.ftcPose.roll;
                tagElevation = detection.ftcPose.elevation;
            }
        }   // end for() loop

        // Add "key" information to telemetry
        telemetry.addLine("\nkey:\nXYZ = X (Right), Y (Forward), Z (Up) dist.");
        telemetry.addLine("PRY = Pitch, Roll & Yaw (XYZ Rotation)");
        telemetry.addLine("RBE = Range, Bearing & Elevation\n\n");
        //telemetry.update();
    }
    private double toMeters(double inches){
        return inches/39.3700787;
    }
}