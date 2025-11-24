package org.firstinspires.ftc.teamcode.Old_Code;

import static java.lang.Thread.sleep;

import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.arcrobotics.ftclib.gamepad.GamepadKeys;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.Hardware.Intake;
import org.firstinspires.ftc.teamcode.Hardware.Flywheels;
import org.firstinspires.ftc.teamcode.Hardware.Transfer;

@Disabled
@TeleOp(name = "DriveCode without Autoaim", group = "Robot")
public class DriveCode extends OpMode {

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
    Flywheels outtake;
    Transfer intakeHinge;
    Transfer outtakeHinge;

    ElapsedTime timer;

    //set up variables
    private int intakePower = 0;
    private boolean flyWheelOn = false;
    private double outtakeSpeed = 3000;

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

    @Override
    public void init() {

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
        outtake = new Flywheels(hardwareMap);
        intakeHinge = new Transfer(hardwareMap);
        outtakeHinge = new Transfer(hardwareMap);

        //setup

        backLeftDrive.setDirection(DcMotor.Direction.FORWARD);
        frontLeftDrive.setDirection(DcMotor.Direction.FORWARD);
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

    }

    @Override
    public void loop() {
        telemetry.addLine("Press A to reset Yaw");
        telemetry.addLine("Press RIGHT STICK to toggle rotation mode");
        telemetry.addLine("Press Y to toggle outtake forward");
        telemetry.addLine("Hold left bumper for robot-relative drive");
        telemetry.addLine("Left stick = translation, Right stick = rotation/heading");

        // Toggles if outtake is forward
        if (driver.wasJustPressed(GamepadKeys.Button.Y)) {
            outtakeForward = !outtakeForward;
        }
        telemetry.addData("Outtake Forward", outtakeForward);

        // Reset yaw with A button
        if (driver.getButton(GamepadKeys.Button.A)) {
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
        // Can be reset by pressing B
        speedMultiplier += driver.getTrigger(GamepadKeys.Trigger.RIGHT_TRIGGER) * 0.2;
        speedMultiplier -= driver.getTrigger(GamepadKeys.Trigger.LEFT_TRIGGER) * 0.2;
        if (driver.getButton(GamepadKeys.Button.B)) {
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
            intakeHinge.intakeHingeStandby();
            if (Math.abs(intakePower) == 1) {
                intakePower = 0;
            } else {
                intakePower = 1;
            }
        } else if (driver.wasJustPressed(GamepadKeys.Button.LEFT_BUMPER)) {
            intakeHinge.intakeHingeStandby();
            if (Math.abs(intakePower) == 1) {
                intakePower = 0;
            } else {
                intakePower = -1;
            }
        }
        intake.setMotorPower(intakePower);

        double operatorLeftTrigger = operator.getTrigger(GamepadKeys.Trigger.LEFT_TRIGGER);
        double operatorRightTrigger = operator.getTrigger(GamepadKeys.Trigger.RIGHT_TRIGGER);

        if (operatorLeftTrigger > 0.1) {
            outtakeSpeed += operatorLeftTrigger * 500;
        }
        if (operatorRightTrigger > 0.1) {
            outtakeSpeed -= operatorRightTrigger * 500;
        }

        if (outtakeSpeed > 3000) {
            outtakeSpeed = 3000;
        }
        if (outtakeSpeed < 500) {
            outtakeSpeed = 500;
        }

        telemetry.addData("Outtake Speed", outtakeSpeed);

        //cycles the ball into positions for launch
        if (operator.wasJustPressed(GamepadKeys.Button.LEFT_BUMPER)) {

            outtakeHinge.outtakeHingeRelax();

            try {
                sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            intakeHinge.intakeHingeLift();

            try {
                sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            intakeHinge.intakeHingeStandby();

        }

        //Launches the ball
        if (operator.wasJustPressed(GamepadKeys.Button.RIGHT_BUMPER)) {

            intakeHinge.intakeHingeStandby();

            outtakeHinge.outtakeHingeRelax();

            outtake.setFlywheelVelocity(2350);      //turns on the flywheels

            while (outtake.getCurrentWheelVelocity("left") < 2300 && outtake.getCurrentWheelVelocity("right") < 2300) {
                telemetry.addData("Current Velocity: ", outtake.getCurrentWheelVelocity("left") + ", " + outtake.getCurrentWheelVelocity("right"));
                telemetry.update();
                try {
                    sleep(500);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            outtakeHinge.outtakeHingeFire();    //Sets the hinge to the position that holds the ball

            try {
                sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            outtake.setFlywheelVelocity(0);     //turns off the flywheels. We don't need it running because we just launched the ball

            outtakeHinge.outtakeHingeRelax();
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
}