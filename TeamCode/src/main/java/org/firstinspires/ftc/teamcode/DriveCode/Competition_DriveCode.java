package org.firstinspires.ftc.teamcode.DriveCode;

import static java.lang.Thread.sleep;

import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.arcrobotics.ftclib.gamepad.GamepadKeys;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.hardware.camera.BuiltinCameraDirection;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.hardware.camera.controls.ExposureControl;
import org.firstinspires.ftc.robotcore.external.hardware.camera.controls.GainControl;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.Hardware.Flywheels;
import org.firstinspires.ftc.teamcode.Hardware.Intake;
import org.firstinspires.ftc.teamcode.Hardware.Lights;
import org.firstinspires.ftc.teamcode.Hardware.Transfer;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import java.util.List;
import java.util.concurrent.TimeUnit;

@TeleOp(name = "Competition DriveCode", group = "Robot")
public class Competition_DriveCode extends OpMode {

    // Driver Code
    public GamepadEx driver;
    public GamepadEx operator;

    //create motor variables
    DcMotor frontLeftDrive;
    DcMotor frontRightDrive;
    DcMotor backLeftDrive;
    DcMotor backRightDrive;

    //create mechanism variables
    Intake intake;
    Flywheels flywheels;
    Transfer belt;
    Transfer trapdoor;
    Lights lights;

    ElapsedTime timer;

    //set up variables
    private int intakePower = 0;
    private boolean flyWheelOn = false;
    private float outtakeSpeed = 2350;

    boolean opModeIsActive = true;

    // IMU for getting robot heading
    IMU imu;

    // NEW: Variables for field-centric rotation control
    private double targetHeading = 0;  // The direction we want to face (in radians)
    private boolean useSnapRotation = true;  // Toggle between snap-to-heading and normal rotation
    private boolean useFieldCentricDrive = true;  // Toggle between field-centric and robot-relative drive
    private double rotationDeadzone = 0.1;  // Ignore small stick movements

    // Simple P controller for rotation (you can upgrade to PID later)
    private double rotationKp = 4.0;  // Higher = faster snap! Try 3.0-6.0 for instant
    private double rotationMaxSpeed = 1.0;  // Maximum rotation speed (set to 1.0 for full power)
    private double speedMultiplier = 1;
    public boolean outtakeForward = false;  //determines which side the controller treats as the front of the bot


    //autoAim stuff
    final double SPEED_GAIN  =  0.1  ;   //  Forward Speed Control "Gain". e.g. Ramp up to 50% power at a 25 inch error.   (0.50 / 25.0)
    final double STRAFE_GAIN =  0.1 ;   //  Strafe Speed Control "Gain".  e.g. Ramp up to 37% power at a 25 degree Yaw error.   (0.375 / 25.0)
    final double TURN_GAIN   =  0.05 ;   //  Turn Control "Gain".  e.g. Ramp up to 25% power at a 25 degree error. (0.25 / 25.0)

    final double MAX_AUTO_SPEED = 0.5;   //  Clip the approach speed to this max value (adjust for your robot)
    final double MAX_AUTO_STRAFE= 0.5;   //  Clip the strafing speed to this max value (adjust for your robot)
    final double MAX_AUTO_TURN  = 0.3;   //  Clip the turn speed to this max value (adjust for your robot)

    private static final boolean USE_WEBCAM = true;  // Set true to use a webcam, or false for a phone camera
    private static final int DESIRED_TAG_ID = 24;     // Choose the tag you want to approach or set to -1 for ANY tag.
    private static final int DESIRED_TAG_ID2 = 20;     // Choose the tag you want to approach or set to -1 for ANY tag.
    private VisionPortal visionPortal;               // Used to manage the video source.
    private AprilTagProcessor aprilTag;              // Used for managing the AprilTag detection process.
    private AprilTagDetection desiredTag = null;     // Used to hold the data for a detected AprilTag

    boolean targetFound     = false;    // Set to true when an AprilTag target is detected
    double  drive           = 0;        // Desired forward power/speed (-1 to +1)
    double  strafe          = 0;        // Desired strafe power/speed (-1 to +1)
    double  turn            = 0;        // Desired turning power/speed (-1 to +1)
    final double DESIRED_DISTANCE = 64; //  this is how close the camera should get to the target (inches)

    @Override
    public void init() {
        // Initialize the Apriltag Detection process
        initAprilTag();

        //create driver objects
        driver = new GamepadEx(gamepad1);
        operator = new GamepadEx(gamepad2);

        //create and set the motor objects
        frontLeftDrive = hardwareMap.get(DcMotor.class, "frontLeft");
        frontRightDrive = hardwareMap.get(DcMotor.class, "frontRight");
        backLeftDrive = hardwareMap.get(DcMotor.class, "backLeft");
        backRightDrive = hardwareMap.get(DcMotor.class, "backRight");

        //create the mechanism objects
        intake = new Intake(hardwareMap);
        flywheels = new Flywheels(hardwareMap);
        belt = new Transfer(hardwareMap);
        trapdoor = new Transfer(hardwareMap);
        lights = new Lights(hardwareMap);

        //setup
        backLeftDrive.setDirection(DcMotor.Direction.FORWARD);
        frontLeftDrive.setDirection(DcMotor.Direction.REVERSE);
        backRightDrive.setDirection(DcMotor.Direction.REVERSE);
        frontRightDrive.setDirection(DcMotor.Direction.REVERSE);

        imu = hardwareMap.get(IMU.class, "imu");
        RevHubOrientationOnRobot.LogoFacingDirection logoDirection = RevHubOrientationOnRobot.LogoFacingDirection.UP;
        RevHubOrientationOnRobot.UsbFacingDirection usbDirection = RevHubOrientationOnRobot.UsbFacingDirection.LEFT;

        RevHubOrientationOnRobot orientationOnRobot = new RevHubOrientationOnRobot(logoDirection, usbDirection);
        imu.initialize(new IMU.Parameters(orientationOnRobot));
        timer = new ElapsedTime();  //creates timer object. Used for measuring time

        // Initialize target heading to current heading
        targetHeading = imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS);

        //April tag stuff
        if (USE_WEBCAM)
            setManualExposure(6, 250);  // Use low exposure time to reduce motion blur
    }

    @Override
    public void loop() {
        telemetry.addLine("Press Y to reset Yaw");
        telemetry.addLine("Press RIGHT STICK to toggle rotation mode");
        telemetry.addLine("Press Xx to toggle outtake forward");
        telemetry.addLine("Hold left bumper for robot-relative drive");
        telemetry.addLine("Left stick = translation, Right stick = rotation/heading");

        // Toggles if outtake is forward
        if (driver.wasJustPressed(GamepadKeys.Button.X)) {
            outtakeForward = !outtakeForward;
        }
        telemetry.addData("Outtake Forward", outtakeForward);

        // Reset yaw with Y button
        if (driver.getButton(GamepadKeys.Button.Y)) {
            imu.resetYaw();
            targetHeading = 0;  // Reset target heading too
        }

        // Toggle rotation mode with right stick click
        if (driver.wasJustPressed(GamepadKeys.Button.RIGHT_STICK_BUTTON)) {
            useSnapRotation = !useSnapRotation;
        }

        // Toggle drive mode with left stick click
        if (driver.wasJustPressed(GamepadKeys.Button.LEFT_STICK_BUTTON)) {
            useFieldCentricDrive = !useFieldCentricDrive;
        }


        // Speed multiplier adjustable via right and left triggers
        // Can be reset by pressing Y
        speedMultiplier += driver.getTrigger(GamepadKeys.Trigger.RIGHT_TRIGGER) * 0.2;
        speedMultiplier -= driver.getTrigger(GamepadKeys.Trigger.LEFT_TRIGGER) * 0.2;
        if (driver.getButton(GamepadKeys.Button.A)) {
            speedMultiplier = 0.5;
        }
        // Max speed is 1
        if (speedMultiplier > 1) {
            speedMultiplier = 1;
        }
        // Min speed is 0.25
        if (speedMultiplier < 0.25) {
            speedMultiplier = 0.25;
        }
        telemetry.addData("Speed Multiplier", speedMultiplier);

        // Get stick inputs
        double forward = -driver.getLeftY();
        double right = -driver.getLeftX();
        double rightStickX = -driver.getRightX();
        double rightStickY = -driver.getRightY();

        // Calculate rotation control
        double rotate;

        if (useSnapRotation) {
            // SNAP-TO-HEADING MODE
            // Check if right stick is being pushed (outside deadzone)
            double rightStickMagnitude = Math.hypot(rightStickX, rightStickY);

            if (rightStickMagnitude > rotationDeadzone) {
                // RIGHT STICK IS ACTIVE - Use field-centric rotation (snap to angle)

                // Calculate the target heading from right stick position
                // atan2 gives us the angle the stick is pointing
                // Negate entire result to flip rotation direction
                double baseHeading = -(Math.atan2(rightStickY, rightStickX) - Math.PI / 2);

                // Add 180 degrees if outtake is forward
                targetHeading = outtakeForward ? AngleUnit.normalizeRadians(baseHeading + Math.PI) : baseHeading;

                // Calculate rotation power to reach target heading
                rotate = snapToHeading(targetHeading);

                telemetry.addLine("MODE: Snap-to-Heading");
                telemetry.addData("Target Heading", Math.toDegrees(targetHeading));
            } else {
                // RIGHT STICK IS NEUTRAL - No rotation command
                rotate = 0;
                telemetry.addLine("MODE: Snap-to-Heading (Idle)");
            }
        } else {
            // NORMAL ROTATION MODE - Just use right stick X for rotation
            rotate = rightStickX;
            telemetry.addLine("MODE: Normal Rotation");
        }

        // Choose drive mode based on toggle
        if (useFieldCentricDrive) {
            // Field-centric drive
            driveFieldRelative(forward, right, rotate, speedMultiplier);
            telemetry.addLine("DRIVE: Field-Centric");
        } else {
            // Robot-relative drive
            drive(forward, right, rotate, speedMultiplier);
            telemetry.addLine("DRIVE: Robot-Relative");
        }

        // Display current heading
        double currentHeading = imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS);
        telemetry.addData("Current Heading", Math.toDegrees(currentHeading));

        // [Rest of your original code for intake, arm, outtake, etc.]

        // Manual intake control
        if (driver.wasJustPressed((GamepadKeys.Button.RIGHT_BUMPER))) {
            if (Math.abs(intakePower) == 1) {
                intakePower = 0;
            } else {
                intakePower = 1;
            }
        } else if (driver.wasJustPressed(GamepadKeys.Button.LEFT_BUMPER)) {
            if (Math.abs(intakePower) == 1) {
                intakePower = 0;
            } else {
                intakePower = -1;
            }
        }
        intake.setMotorPower(intakePower);
        belt.setMotorPower(intakePower);

        if (operator.getTrigger(GamepadKeys.Trigger.LEFT_TRIGGER) > 0.1) {
            outtakeSpeed += 250;
        } else if (operator.getTrigger(GamepadKeys.Trigger.RIGHT_TRIGGER) > 0.1) {
            outtakeSpeed -= 250;
        } else if (operator.wasJustPressed((GamepadKeys.Button.A))) {
            outtakeSpeed = 2350;
        }

        if (outtakeSpeed > 3000) {
            outtakeSpeed = 3000;
        } else if (outtakeSpeed < 2300) {
            outtakeSpeed = 2300;
        }

        telemetry.addData("Outtake Speed", outtakeSpeed);

        //Launches the balls while right bumper is held
        if (operator.isDown(GamepadKeys.Button.RIGHT_BUMPER)) {

//            intake.setMotorPower(-1);
            belt.setMotorPower(1);
            flywheels.setFlywheelVelocity(outtakeSpeed);

            try {
                sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            trapdoor.trapdoorOpen();

            try {
                sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

//            intakeHinge.intakeHingeStandby();

        }else{
            flywheels.setFlywheelVelocity(0);
            trapdoor.trapdoorClose();
        }

        //Launches the ball
//        if (operator.wasJustPressed(GamepadKeys.Button.RIGHT_BUMPER)) {
//
//            outtakeHinge.outtakeHingeRelax();
//
//            if (flyWheelOn) {
//                for (int t = 0; t < 20 && outtake.getCurrentWheelVelocity("left") < (outtakeSpeed - 150) && outtake.getCurrentWheelVelocity("right") < (outtakeSpeed - 150); t++) {
//                    telemetry.addData("Current Velocity: ", outtake.getCurrentWheelVelocity("left") + ", " + outtake.getCurrentWheelVelocity("right"));
//                    telemetry.update();
//                    try {
//                        sleep(250);
//                    } catch (InterruptedException e) {
//                        throw new RuntimeException(e);
//                    }
//                }
//            }
//
//            outtakeHinge.outtakeHingeFire();    //Sets the hinge to the position that holds the ball
//
//            try {
//                sleep(500);
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
//
//            outtakeHinge.outtakeHingeRelax();
//        }

        if (operator.isDown((GamepadKeys.Button.DPAD_UP))) {
            trapdoor.changeHingePosition(0.05);
            telemetry.addLine("TRAPDOOR ADJUSTED");
        } else if (operator.isDown(GamepadKeys.Button.DPAD_DOWN)) {
            trapdoor.changeHingePosition(-0.05);
            telemetry.addLine("TRAPDOOR ADJUSTED");
        }

//        if (operator.wasJustPressed((GamepadKeys.Button.B))) {
//            outtakeHinge.outtakeHingeRelax();
//            if (flyWheelOn) {
//                outtake.setFlywheelVelocity(0);      //turns off the flywheels
//                flyWheelOn = false;
//            } else {
//                outtake.setFlywheelVelocity(outtakeSpeed);      //turns on the flywheels
//                flyWheelOn = true;
//            }
//        }

        //Auto-aims
        if (driver.isDown(GamepadKeys.Button.B)) {
            scanForTags();
            if (targetFound) {
                // Determine heading, range and Yaw (tag image rotation) error so we can use them to control the robot automatically.
                double  rangeError      = (desiredTag.ftcPose.range - DESIRED_DISTANCE);
                double  headingError    = -desiredTag.ftcPose.bearing;
                double  yawError        = desiredTag.ftcPose.yaw + 0.1; //changed to help with heading offset
                runPIDStuff(rangeError, headingError, yawError);    //calculates and sends power to the wheels
            }
        }

        driver.readButtons();
        operator.readButtons();
        telemetry.update();
    }

    public void stop() { //when we stop the program with the driver station, this method runs. It's a built-in method, similar to and init() and loop()
        opModeIsActive = false;   //tells the rest of the code that the program has been stopped. We use it as a conditional in while() loops, to make sure these loops stop running immediately when the we end the program. We don't want the arm to keep moving after we press stop, for example.
    }

    // Calculates rotation angle based on the initial set yaw angle
    // Will instantly snap to the direction of the joy stick
    // Example: Moving the joystick up will rotate the robot until its facing forward.
    private double snapToHeading(double targetHeading) {
        // Get current robot heading
        double currentHeading = imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS);

        // Calculate error (how far we are from target)
        double error = targetHeading - currentHeading;

        // IMPORTANT: Normalize error to shortest path
        // This ensures we rotate the shorter direction
        // Example: If we need to go from 170° to -170°, we should rotate 20°, not 340°
        error = AngleUnit.normalizeRadians(error);

        // Calculate rotation power using proportional control
        // Larger error = faster rotation
        double rotatePower = error * rotationKp;

        // Clamp the rotation power to maximum speed
        rotatePower = Math.max(-rotationMaxSpeed, Math.min(rotationMaxSpeed, rotatePower));

        // Add a small deadband to stop oscillation when close to target
        if (Math.abs(error) < 0.02) {  // About 1 degree
            rotatePower = 0;
        }

        return rotatePower;
    }

    // Field-relative drive method
    private void driveFieldRelative(double forward, double right, double rotate, double speedMultiplier) {
        double theta = Math.atan2(forward, right);
        double r = Math.hypot(right, forward);
        theta = AngleUnit.normalizeRadians(theta -
                imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS));
        double newForward = r * Math.sin(theta);
        double newRight = r * Math.cos(theta);
        drive(newForward, newRight, rotate, speedMultiplier);
    }

    // Robot-relative drive method
    public void drive(double forward, double right, double rotate, double speedMultiplier) {
        double frontLeftPower = forward + right + rotate;
        double frontRightPower = forward - right - rotate;
        double backRightPower = forward + right - rotate;
        double backLeftPower = forward - right + rotate;

        double maxPower = 1.0;
        double maxSpeed = .75 * speedMultiplier;

        maxPower = Math.max(maxPower, Math.abs(frontLeftPower));
        maxPower = Math.max(maxPower, Math.abs(frontRightPower));
        maxPower = Math.max(maxPower, Math.abs(backRightPower));
        maxPower = Math.max(maxPower, Math.abs(backLeftPower));

        frontLeftDrive.setPower(maxSpeed * (frontLeftPower / maxPower));
        frontRightDrive.setPower(maxSpeed * (frontRightPower / maxPower));
        backLeftDrive.setPower(maxSpeed * (backLeftPower / maxPower));
        backRightDrive.setPower(maxSpeed * (backRightPower / maxPower));
    }
    //auto-aim methods
    public void scanForTags(){  //Checks if april tags are on screen, and if so, it sets the desiredTag object to that tag
        targetFound = false;
        desiredTag  = null;

        // Step through the list of detected tags and look for a matching tag
        List<AprilTagDetection> currentDetections = aprilTag.getDetections();
        for (AprilTagDetection detection : currentDetections) {
            // Look to see if we have size info on this tag.
            if (detection.metadata != null) {
                //  Check to see if we want to track towards this tag.
                if ((DESIRED_TAG_ID < 0) || (detection.id == DESIRED_TAG_ID || detection.id == DESIRED_TAG_ID2)) {
                    // Yes, we want to use this tag.
                    targetFound = true;
                    desiredTag = detection;
                    break;  // don't look any further.
                } else {
                    // This tag is in the library, but we do not want to track it right now.
                    telemetry.addData("Skipping", "Tag ID %d is not desired", detection.id);
                }
            } else {
                // This tag is NOT in the library, so we don't have enough information to track to it.
                telemetry.addData("Unknown", "Tag ID %d is not in TagLibrary", detection.id);
            }
        }

        // Tell the driver what we see, and what to do.
        if (targetFound) {
            telemetry.addData("\n>","HOLD Left-Bumper to Drive to Target\n");
            telemetry.addData("Found", "ID %d (%s)", desiredTag.id, desiredTag.metadata.name);
            telemetry.addData("Range",  "%5.1f inches", desiredTag.ftcPose.range);
            telemetry.addData("Bearing","%3.0f degrees", desiredTag.ftcPose.bearing);
            telemetry.addData("Yaw","%3.0f degrees", desiredTag.ftcPose.yaw);
        } else {
            telemetry.addData("\n>","Drive using joysticks to find valid target\n");
        }
    }
    public void runPIDStuff(double rangeError, double headingError, double yawError){   //Calculate the power needed for driving/strafing/turning

        // If Left Bumper is being pressed, AND we have found the desired target, Drive to target Automatically .
        if (targetFound) {
            // Use the speed and turn "gains" to calculate how we want the robot to move.
            drive  = Range.clip(rangeError * SPEED_GAIN, -MAX_AUTO_SPEED, MAX_AUTO_SPEED);
            turn   = Range.clip(headingError * TURN_GAIN, -MAX_AUTO_TURN, MAX_AUTO_TURN) ;
            strafe = Range.clip(-yawError * STRAFE_GAIN, -MAX_AUTO_STRAFE, MAX_AUTO_STRAFE);

            telemetry.addData("Auto","Drive %5.2f, Strafe %5.2f, Turn %5.2f ", drive, strafe, turn);
        }
        telemetry.update();

        // Apply desired axes motions to the drivetrain.
        moveRobot(drive, strafe, turn);
    }
    public void moveRobot(double x, double y, double yaw) { //calculates and sends the power needed for each motor
        // Calculate wheel powers.
        double frontLeftPower    =  x - y - yaw;
        double frontRightPower   =  x + y + yaw;
        double backLeftPower     =  x + y - yaw;
        double backRightPower    =  x - y + yaw;

        // Normalize wheel powers to be less than 1.0
        double max = Math.max(Math.abs(frontLeftPower), Math.abs(frontRightPower));
        max = Math.max(max, Math.abs(backLeftPower));
        max = Math.max(max, Math.abs(backRightPower));

        if (max > 1.0) {
            frontLeftPower /= max;
            frontRightPower /= max;
            backLeftPower /= max;
            backRightPower /= max;
        }

        // Send powers to the wheels.
        frontLeftDrive.setPower(frontLeftPower);
        frontRightDrive.setPower(frontRightPower);
        backLeftDrive.setPower(backLeftPower);
        backRightDrive.setPower(backRightPower);
    }
    private void initAprilTag() {   //Sets up the april tag and camera stuff. Gets it ready for use.
        // Create the AprilTag processor by using a builder.
        aprilTag = new AprilTagProcessor.Builder().build();

        // Adjust Image Decimation to trade-off detection-range for detection-rate.
        // e.g. Some typical detection data using a Logitech C920 WebCam
        // Decimation = 1 ..  Detect 2" Tag from 10 feet away at 10 Frames per second
        // Decimation = 2 ..  Detect 2" Tag from 6  feet away at 22 Frames per second
        // Decimation = 3 ..  Detect 2" Tag from 4  feet away at 30 Frames Per Second
        // Decimation = 3 ..  Detect 5" Tag from 10 feet away at 30 Frames Per Second
        // Note: Decimation can be changed on-the-fly to adapt during a match.
        aprilTag.setDecimation(2);

        // Create the vision portal by using a builder.
        if (USE_WEBCAM) {
            visionPortal = new VisionPortal.Builder()
                    .setCamera(hardwareMap.get(WebcamName.class, "Webcam 1"))
                    .addProcessor(aprilTag)
                    .build();
        } else {
            visionPortal = new VisionPortal.Builder()
                    .setCamera(BuiltinCameraDirection.BACK)
                    .addProcessor(aprilTag)
                    .build();
        }
    }
    private void setManualExposure(int exposureMS, int gain) {   //not exactly sure what this does. It sets up the camera's setting or something
        // Wait for the camera to be open, then use the controls

        if (visionPortal == null) {
            return;
        }

        // Make sure camera is streaming before we try to set the exposure controls
        if (visionPortal.getCameraState() != VisionPortal.CameraState.STREAMING) {
            telemetry.addData("Camera", "Waiting");
            telemetry.update();
            while (opModeIsActive && (visionPortal.getCameraState() != VisionPortal.CameraState.STREAMING)) {
                //sleep1(20);
            }
            telemetry.addData("Camera", "Ready");
            telemetry.update();
        }

        // Set camera controls unless we are stopping.
        if (opModeIsActive)
        {
            ExposureControl exposureControl = visionPortal.getCameraControl(ExposureControl.class);
            if (exposureControl.getMode() != ExposureControl.Mode.Manual) {
                exposureControl.setMode(ExposureControl.Mode.Manual);
                //sleep1(50);
            }
            exposureControl.setExposure((long)exposureMS, TimeUnit.MILLISECONDS);
            //sleep1(20);
            GainControl gainControl = visionPortal.getCameraControl(GainControl.class);
            gainControl.setGain(gain);
            //sleep1(20);
        }
    }
}