/**
<DRIVER MANUAL FOR DUMMIES>
*No offense ;)
--DRIVER CONTROLS--
[MOVEMENT]
Y = reset Yaw
LEFT STICK = translation
RIGHT STICK = rotation
LEFT STICK DOWN = toggle field/robot centric drive mode
RIGHT STICK DOWN = toggle snap/relative rotation mode
DPAD UP = movement speed up
DPAD DOWN = movement speed down

[INTAKE]
LEFT BUMPER = reverse intake
RIGHT BUMPER = intake

[OUTTAKE]
RIGHT TRIGGER (hold) = charges up flywheels and launches
DPAD LEFT = turret left
DPAD RIGHT = turret right
X = reset turret position (reset at middle)
A = auto aim
 */

package org.firstinspires.ftc.teamcode.DriveCode;

import static java.lang.Thread.sleep;

import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.arcrobotics.ftclib.gamepad.GamepadKeys;
import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.hardware.camera.BuiltinCameraDirection;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.hardware.camera.controls.ExposureControl;
import org.firstinspires.ftc.robotcore.external.hardware.camera.controls.GainControl;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.Hardware.AutoAim;
import org.firstinspires.ftc.teamcode.Hardware.Flywheels;
import org.firstinspires.ftc.teamcode.Hardware.Intake;
import org.firstinspires.ftc.teamcode.Hardware.Lights;
import org.firstinspires.ftc.teamcode.Hardware.OuttakeHood;
import org.firstinspires.ftc.teamcode.Hardware.Transfer;
import org.firstinspires.ftc.teamcode.Hardware.Turret;
import org.firstinspires.ftc.teamcode.Hardware.VisionAssistLimelight;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import java.util.List;
import java.util.concurrent.TimeUnit;

@TeleOp(name = "[Competition] DriveCode - Use this drive code", group = "Robot")
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
    Transfer transferWheels;
    Transfer trapdoor;
    Lights lights;
    Turret turret;
    OuttakeHood hood;
    AutoAim autoAim;
    VisionAssistLimelight visionAssist;

    ElapsedTime timer;
    ElapsedTime intakeTimer;
    ElapsedTime loopTimer;
    ElapsedTime lightTimer;

    //set up variables
    private float intakePower = 0;
    boolean outtakeOn = false;
    private double outtakePower = 0;
    private boolean flyWheelOn = false;
    private float outtakeSpeed = 3000;

    boolean opModeIsActive = true;
    public String teamColor = "Red";

    // IMU for getting robot heading
    IMU imu;


    double oldTime;

    //Variables for field-centric rotation control
    private double targetHeading = 0;  // The direction we want to face (in radians)
    private boolean useSnapRotation = true;  // Toggle between snap-to-heading and normal rotation
    private boolean useFieldCentricDrive = true;  // Toggle between field-centric and robot-relative drive
    private double rotationDeadzone = 0.1;  // Ignore small stick movements

    // Simple P controller for rotation (you can upgrade to PID later)
    private double rotationKp = 4.0;  // Higher = faster snap! Try 3.0-6.0 for instant
    private double rotationMaxSpeed = 1.0;  // Maximum rotation speed (set to 1.0 for full power)
    private double speedMultiplier = 1;
    public boolean outtakeForward = false;  //determines which side the controller treats as the front of the bot
    private double extraOuttakeSpeed=0;

    private double maintainOuttakeSpeed=1386;    //750


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
    boolean wasTargetFound  = false;
    boolean isCorrectingBoundary = false;
    double  drive           = 0;        // Desired forward power/speed (-1 to +1)
    double  strafe          = 0;        // Desired strafe power/speed (-1 to +1)
    double  turn            = 0;        // Desired turning power/speed (-1 to +1)
    final double DESIRED_DISTANCE = 64; //  this is how close the camera should get to the target (inches)

    private enum LaunchState {
        IDLE,
        SPINNING_UP,
        WAITING_AFTER_SPINUP,
        FEEDING_BALL,
        WAITING_BETWEEN_BALLS
    }

    private LaunchState launchState = LaunchState.IDLE;
    private ElapsedTime launchTimer = new ElapsedTime();
    private int ballsFed = 0;
    private double outtakeSpeedBeforeDrop = 0;
    private double targetSpeed = 0;
    private ElapsedTime targetLostTimer = new ElapsedTime();
    private boolean wasTargetFoundLastFrame = false;
    private static final double TARGET_LOST_DELAY = 1.0; // 1 second delay in seconds
    public boolean shouldAutoAim = true;
    double hoodAngle;
    boolean flywheelIsReady=false;

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
        transferWheels = new Transfer(hardwareMap);
        trapdoor = new Transfer(hardwareMap);
        lights = new Lights(hardwareMap);
        turret = new Turret(hardwareMap);
        hood = new OuttakeHood(hardwareMap);
        autoAim = new AutoAim(Math.toRadians(15));
        visionAssist = new VisionAssistLimelight(hardwareMap, 3);
        intakeTimer = new ElapsedTime();
        lightTimer = new ElapsedTime();

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

        ElapsedTime timer = new ElapsedTime();

        telemetry.addData("Status", "IMU Calibrating... Do NOT move robot!");
        telemetry.update();
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            // Handle exception
        }

        timer = new ElapsedTime();  //creates timer object. Used for measuring time
        loopTimer = new ElapsedTime();  //creates timer for loop timing (needed for slew rate)

        // Initialize target heading to current heading
        targetHeading = imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS);

        //April tag stuff
        if (USE_WEBCAM) {
            //setManualExposure(6, 50);  // Use low exposure time to reduce motion blur
        }

        // Bulk reading for Optimization
        List<LynxModule> allHubs = hardwareMap.getAll(LynxModule.class);
        for (LynxModule hub : allHubs) {
            hub.setBulkCachingMode(LynxModule.BulkCachingMode.MANUAL);
        }
        // Bulk reading for Optimization

    }

    @Override
    public void loop() {

        if (driver.wasJustPressed(GamepadKeys.Button.START)){
            teamColor = teamColor.equals("Red") ? "Blue" : teamColor.equals("Blue") ? "Red" : teamColor;
        }

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
//        speedMultiplier += driver.getTrigger(GamepadKeys.Trigger.RIGHT_TRIGGER) * 0.2;
//        speedMultiplier -= driver.getTrigger(GamepadKeys.Trigger.LEFT_TRIGGER) * 0.2;

        if (driver.getButton(GamepadKeys.Button.DPAD_UP)){
            speedMultiplier += 0.5;
        }
        if (driver.getButton(GamepadKeys.Button.DPAD_DOWN)){
            speedMultiplier -= 0.5;
        }

        // Max speed is 1
        if (speedMultiplier > 1) {
            speedMultiplier = 1;
        }
        // Min speed is 0.25
        if (speedMultiplier < 0) {
            speedMultiplier = 0.1;
        }

        // Get stick inputs
        double forward = -driver.getLeftY();
        double right = -driver.getLeftX();
        double rightStickX = -driver.getRightX();
        double rightStickY = -driver.getRightY();

        // Calculate rotation control
        double rotate;
//        boolean visionAssistEnabled = driver.getButton(GamepadKeys.Button.DPAD_UP);

        telemetry.addLine("-----Robot Information-----");
//        telemetry.addLine("Movement");
        telemetry.addData("Speed multiplier", speedMultiplier);
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

            // Combine driver rotation + vision assist
//            double visionTurn = visionAssist.getTurnCorrection(visionAssistEnabled);
//            rotate += visionTurn;

//            telemetry.addData("Vision Assist", visionAssistEnabled);
//            telemetry.addData("Vision Turn", visionTurn);
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
//        double currentHeading = imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS);
//        telemetry.addData("Current Heading", Math.toDegrees(currentHeading));

//        telemetry.addLine();
//        telemetry.addLine("Turret");
        telemetry.addData("Auto aiming", shouldAutoAim);
        telemetry.addData("Sees april tag", targetFound);
//        telemetry.addData("Turret rotation", turret.getTurretPosition());
//        telemetry.addData("Active flywheel speed (t/s)", flywheels.getFlywheelSpeedRaw());
        telemetry.addData("Goal distance", toFeet(toInches(autoAim.distanceToTagTelemetry)));

        //practically irrelevant data
//        telemetry.addLine("");
//        telemetry.addLine("Extra:");
//        telemetry.addData("Extra outtake speed", extraOuttakeSpeed);
//        telemetry.addData("Outtake forward", outtakeForward);
//        telemetry.addLine("");


        // Listen for button presses

        // Intake control
        if (driver.wasJustPressed(GamepadKeys.Button.RIGHT_BUMPER)) {
            if (Math.abs(intakePower) == 1) {
                intakePower = 0;
                transferWheels.setTransferPower(0);
            } else {
                intakePower = 1;
                intakeTimer.reset();
            }
        } else if (driver.wasJustPressed(GamepadKeys.Button.LEFT_BUMPER)) {
            if (Math.abs(intakePower) == 1) {
                intakePower = 0;
                transferWheels.setTransferPower(0);
            } else {
                intakePower = -1;
                transferWheels.setTransferPower(-1);
            }
        }
        if (intakePower==1 && intakeTimer.milliseconds()>1000 && intake.getVelocityRPM()<500){
            intakePower = 0.25f;
        }
        intake.setIntakePower(intakePower);

        if (driver.getTrigger(GamepadKeys.Trigger.RIGHT_TRIGGER) > 0.1) {
            // Trigger is being held - run launch sequence

            switch (launchState) {
                case IDLE:
                    // Start the launch sequence
//                    drive(0, 0, 0, 0);
//                    turret.setMotorPower(0);
                    if (targetFound) {
                        targetSpeed = flywheels.launchFromDistance(toFeet(toInches(autoAim.distanceToTagTelemetry)), extraOuttakeSpeed);
                    } else {
                        targetSpeed = maintainOuttakeSpeed;
                        flywheels.setFlywheelSpeedRaw(maintainOuttakeSpeed);
                    }
                    launchTimer.reset();
                    launchState = LaunchState.SPINNING_UP;
                    ballsFed = 0;
                    break;

                case SPINNING_UP:
                    // Wait for flywheels to reach target speed
//                    drive(0, 0, 0, 0);  // Keep stopping the drive
//                    turret.setMotorPower(0);  // Keep turret stopped

                    if (flywheels.getFlywheelSpeedRaw() >= targetSpeed * 0.9) {
                        // Flywheels are ready!
                        launchTimer.reset();
                        launchState = LaunchState.WAITING_AFTER_SPINUP;
                    } else if (launchTimer.milliseconds() > 1200) {
                        // Timeout - proceed anyway
                        launchTimer.reset();
                        launchState = LaunchState.WAITING_AFTER_SPINUP;
                    }
                    break;

                case WAITING_AFTER_SPINUP:
                    // 500ms stabilization period
//                    drive(0, 0, 0, 0);
//                    turret.setMotorPower(0);

                    if (launchTimer.milliseconds() > 500) {
                        // Start feeding first ball
                        outtakeSpeedBeforeDrop = flywheels.getFlywheelRPM();
                        if (toFeet(toInches(autoAim.distanceToTagTelemetry)) > 8.0){
                            maintainOuttakeSpeed = flywheels.getFlywheelSpeedRaw() * 1;
                        } else {
                            maintainOuttakeSpeed = flywheels.getFlywheelSpeedRaw() * .9;
                        }
//                            if (ballsFed > 0) {
//                                intake.setIntakePower(1);
//                            }
//                            transferWheels.setTransferPower(1);
                        launchTimer.reset();
                        launchState = LaunchState.FEEDING_BALL;
                    }
                    break;

                case FEEDING_BALL:
                    // Feed ball and detect when it launches
//                    drive(0, 0, 0, 0);
//                    turret.setMotorPower(0);
                    if (ballsFed>0){
                        intake.setIntakePower(1);
                    } else {
                        intake.setIntakePower(0);
                    }
                    transferWheels.setTransferPower(1);

                    if (flywheels.getFlywheelRPM() < outtakeSpeedBeforeDrop - 100) {
                        // Ball detected! Stop transfer and wait
                        transferWheels.setTransferPower(0);
                        intake.setIntakePower(0);
                        ballsFed++;
                        launchTimer.reset();

                        if (ballsFed < 3) {
                            launchState = LaunchState.WAITING_BETWEEN_BALLS;
                        } else {
                            // All balls fed - done!
                            launchState = LaunchState.IDLE;
                        }
                    } else if (launchTimer.milliseconds() > 1800) {
                        // Timeout - no ball detected, abort
                        transferWheels.setTransferPower(0);
                        intake.setIntakePower(0);
                        launchState = LaunchState.IDLE;
                    }
                    break;

                case WAITING_BETWEEN_BALLS:
                    // 1500ms delay between balls
//                    drive(0, 0, 0, 0);
//                    turret.setMotorPower(0);
                    // Wait 500 milliseconds for outtake to speed up
                    if (toFeet(toInches(autoAim.distanceToTagTelemetry)) > 8.0){
                        if (launchTimer.milliseconds() > 1000) { // Originally 1500
                            // Ready for next ball
                            outtakeSpeedBeforeDrop = flywheels.getFlywheelRPM();
                            maintainOuttakeSpeed = flywheels.getFlywheelSpeedRaw()*1;
                            launchState = LaunchState.FEEDING_BALL;
                            launchTimer.reset();
                        }
                    }else if (launchTimer.milliseconds() > 500) { // Originally 1500
                        // Ready for next ball
                        outtakeSpeedBeforeDrop = flywheels.getFlywheelRPM();
                        maintainOuttakeSpeed = flywheels.getFlywheelSpeedRaw()*.9;
                        launchState = LaunchState.FEEDING_BALL;
                        launchTimer.reset();

                    }

                    break;
            }

        } else {
            // Trigger released - reset to idle if we were launching
            if (launchState != LaunchState.IDLE) {
                transferWheels.setTransferPower(0);
                intake.setIntakePower(0);
                launchState = LaunchState.IDLE;
            }
            flywheels.setFlywheelSpeedRaw(maintainOuttakeSpeed);
        }
        if (driver.isDown(GamepadKeys.Button.B) && driver.wasJustPressed(GamepadKeys.Button.DPAD_DOWN)){
            extraOuttakeSpeed-=25;
        } else if (driver.isDown(GamepadKeys.Button.B) && driver.wasJustPressed(GamepadKeys.Button.DPAD_UP)){
            extraOuttakeSpeed+=25;
        } else if (driver.isDown(GamepadKeys.Button.B) && driver.wasJustPressed(GamepadKeys.Button.DPAD_RIGHT)){
            extraOuttakeSpeed=0;
        }
        if (!shouldAutoAim){
            maintainOuttakeSpeed=1540;  //if not auto-aiming, set the default to higher
        } else {
            //maintainOuttakeSpeed=750;   //if auto-aiming, set the default to the normal value
        }
        // Add telemetry to see what's happening
//        telemetry.addLine();
//        telemetry.addLine("-----LAUNCH STATUS-----");
//        telemetry.addData("State", launchState);
//        telemetry.addData("Balls Fed", ballsFed);
//        telemetry.addData("Timer (ms)", launchTimer.milliseconds());
//        telemetry.addData("Current Speed (t/s)", flywheels.getFlywheelSpeedRaw());
//        telemetry.addData("Target Speed (t/s)", targetSpeed);
//        telemetry.addLine();
//
//        if (operator.wasJustPressed((GamepadKeys.Button.RIGHT_BUMPER))) {
//            extraOuttakeSpeed += 25;    //0.466666667
//        } else if (operator.wasJustPressed((GamepadKeys.Button.LEFT_BUMPER))) {
//            extraOuttakeSpeed -= 25;
//        } else if (operator.wasJustPressed((GamepadKeys.Button.A))) {
//            outtakeSpeed = 2350;
//            extraOuttakeSpeed = 0;
//        }

        //clamps speed to bounds
//        if (outtakeSpeed > 6000) {
//            outtakeSpeed = 6000;
//        } else if (outtakeSpeed < 2300) {
//            outtakeSpeed = 2300;
//        }

        //Auto-aims and moves the turret
        scanForTags();
        if (driver.wasJustPressed(GamepadKeys.Button.A)){
            shouldAutoAim = !shouldAutoAim;
        }

        if(driver.wasJustPressed(GamepadKeys.Button.X)){
            turret.resetInitial();
        }


        if (!shouldAutoAim){
            if (driver.wasJustPressed(GamepadKeys.Button.DPAD_RIGHT)){
                turret.setMotorPower(0.5);
            } else if (driver.wasJustPressed(GamepadKeys.Button.DPAD_LEFT)){
                turret.setMotorPower(-0.5);
            } else {
                turret.setMotorPower(0);
            }
//            turret.setMotorPower(-operator.getLeftX()*0.5);
            isCorrectingBoundary = false;
            wasTargetFoundLastFrame = false; // Reset tracking when manual control

        } else {
            if (targetFound) {
                autoAim.calculateEverything(desiredTag);
                isCorrectingBoundary = false;
                turret.setMotorPower(autoAim.turn);
//                telemetry.addLine();

                // Reset the timer whenever we see the target
                if (!wasTargetFoundLastFrame) {
                    targetLostTimer.reset();
                }
                wasTargetFoundLastFrame = true;

            } else {    //moves the turret to standby position if not tags are found
                // Target is NOT found
                if (wasTargetFoundLastFrame) {
                    // Target was JUST lost - start the timer
                    targetLostTimer.reset();
                    wasTargetFoundLastFrame = false;
                }

                // Check if enough time has passed since losing the target
                if (targetLostTimer.seconds() >= TARGET_LOST_DELAY) {
                    // Wait period is over - return to position 0
                    if (!turret.isAtTargetPosition(0)) {
                        turret.rotateTowardsTarget(0);
                    } else {
                        turret.setMotorPower(0);
                    }
                } else {
                    // Still within wait period - hold position
                    turret.setMotorPower(0);
//                    telemetry.addData("Target lost, waiting", "%.1f seconds",
//                            TARGET_LOST_DELAY - targetLostTimer.seconds());
                }
            }
        }

        // LEDS
        boolean intakeOn = (intake.getIntakePower() >= 0.5);
        if (lightTimer.milliseconds()>500) {
            lightTimer.reset();
            lights.updateStatus(targetFound, intakeOn, teamColor);
        }

        driver.readButtons();
        operator.readButtons();

        // Frequency

        double newTime = getRuntime();
        double loopTime = newTime - oldTime;
        double frequency = 1/loopTime;
        oldTime = newTime;

        telemetry.addData("LoopTime:", frequency);
        telemetry.update();

        // Frequency

        // Bulk reading for Optimization
        List<LynxModule> allHubs = hardwareMap.getAll(LynxModule.class);
        for (LynxModule hub : allHubs) {
            hub.clearBulkCache();
        }
        // Bulk reading for Optimization

    }

    public void stop() { //when we stop the program with the driver station, this method runs. It's a built-in method, similar to and init() and loop()
        opModeIsActive = false;   //tells the rest of the code that the program has been stopped. We use it as a conditional in while() loops, to make sure these loops stop running immediately when the we end the program. We don't want the arm to keep moving after we press stop, for example.
        lights.Light_Off();
    }

    // Safe motor power setter to prevent floodgate cutout issues
    void setSafePower(DcMotor motor, double targetPower) {
        final double SLEW_RATE = 0.5;
        double currentPower = motor.getPower();
        double desiredChange = targetPower - currentPower;
        double limitedChange = Math.max(-SLEW_RATE, Math.min(desiredChange, SLEW_RATE));
        motor.setPower(currentPower + limitedChange);
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

    // Robot-relative drive method - NOW USES SAFE POWER SETTING
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

        // Use setSafePower instead of direct setPower to prevent floodgate cutout
        setSafePower(frontLeftDrive, maxSpeed * (frontLeftPower / maxPower));
        setSafePower(frontRightDrive, maxSpeed * (frontRightPower / maxPower));
        setSafePower(backLeftDrive, maxSpeed * (backLeftPower / maxPower));
        setSafePower(backRightDrive, maxSpeed * (backRightPower / maxPower));
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
//                    telemetry.addData("Skipping", "Tag ID %d is not desired", detection.id);
                }
            } else {
                // This tag is NOT in the library, so we don't have enough information to track to it.
//                telemetry.addData("Unknown", "Tag ID %d is not in TagLibrary", detection.id);
            }
        }
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
                //sleep(20);
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
            }
            exposureControl.setExposure((long)exposureMS, TimeUnit.MILLISECONDS);
            GainControl gainControl = visionPortal.getCameraControl(GainControl.class);
            gainControl.setGain(gain);
        }
    }
    private double toInches(double meters){
        return meters*39.3700787;
    }
    private double toFeet(double inches){
        return inches / 12;
    }
}