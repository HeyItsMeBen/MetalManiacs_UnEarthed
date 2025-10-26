package org.firstinspires.ftc.teamcode.DriveCode;

import static java.lang.Thread.sleep;

import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.arcrobotics.ftclib.gamepad.GamepadKeys;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.Systems.Hinge;
import org.firstinspires.ftc.teamcode.Systems.Intake;
import org.firstinspires.ftc.teamcode.Systems.Outtake;
import org.firstinspires.ftc.teamcode.Systems.Arm;

@TeleOp(name = "Competition DriveCode", group = "Robot")
public class DriveCode extends OpMode {

    // Driver Code
    public GamepadEx driver;
    public GamepadEx operator;

    DcMotor frontLeftDrive;
    DcMotor frontRightDrive;
    DcMotor backLeftDrive;
    DcMotor backRightDrive;

    Intake intake;
    Outtake outtake;
    Arm arm;
    Hinge hinge;

    ElapsedTime timer;

    private int intakePower = 0;
    private boolean flyWheelOn = false;
    double velocityPeak=0;
    boolean holdPosition_Arm=false;
    boolean armIsMoving=false;
    double armTarget=0;
    boolean opModeIsActive=true;

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
    private enum ArmState {
        IDLE,
        OUTTAKE_LIFT,
        OUTTAKE_WAIT_FLYWHEEL,
        OUTTAKE_FIRE,
        OUTTAKE_WAIT_FIRE,
        OUTTAKE_FINISH,
        ARM_DOWN_STEP1,
        ARM_DOWN_STEP2,
        ARM_DOWN_STEP3,
        ARM_DOWN_FINISH
    }

    private ArmState armState = ArmState.IDLE;
    private ElapsedTime stateTimer = new ElapsedTime();
    private double outtakeSpeed = 3000;

    @Override
    public void init() {

        driver = new GamepadEx(gamepad1);
        operator = new GamepadEx(gamepad2);

        frontLeftDrive = hardwareMap.get(DcMotor.class, "frontLeft");
        frontRightDrive = hardwareMap.get(DcMotor.class, "frontRight");
        backLeftDrive = hardwareMap.get(DcMotor.class, "backLeft");
        backRightDrive = hardwareMap.get(DcMotor.class, "backRight");

        intake = new Intake(hardwareMap);
        arm = new Arm(hardwareMap);
        outtake = new Outtake(hardwareMap);
        hinge = new Hinge(hardwareMap);

        arm.resetArmEncoders();

        backLeftDrive.setDirection(DcMotor.Direction.FORWARD);
        frontLeftDrive.setDirection(DcMotor.Direction.FORWARD);
        backRightDrive.setDirection(DcMotor.Direction.REVERSE);
        frontRightDrive.setDirection(DcMotor.Direction.REVERSE);

        imu = hardwareMap.get(IMU.class, "imu");
        RevHubOrientationOnRobot.LogoFacingDirection logoDirection = RevHubOrientationOnRobot.LogoFacingDirection.UP;
        RevHubOrientationOnRobot.UsbFacingDirection usbDirection = RevHubOrientationOnRobot.UsbFacingDirection.LEFT;

        RevHubOrientationOnRobot orientationOnRobot = new RevHubOrientationOnRobot(logoDirection, usbDirection);
        imu.initialize(new IMU.Parameters(orientationOnRobot));
        timer = new ElapsedTime();

        // Initialize target heading to current heading
        targetHeading = imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS);
    }

    @Override
    public void loop() {
        telemetry.addLine("Press A to reset Yaw");
        telemetry.addLine("Press RIGHT STICK to toggle rotation mode");
        telemetry.addLine("Hold left bumper for robot-relative drive");
        telemetry.addLine("Left stick = translation, Right stick = rotation/heading");

        // Reset yaw with A button
        if (driver.getButton(GamepadKeys.Button.A)){
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
        speedMultiplier += driver.getTrigger(GamepadKeys.Trigger.RIGHT_TRIGGER)*0.2;
        speedMultiplier -= driver.getTrigger(GamepadKeys.Trigger.LEFT_TRIGGER)*0.2;
        if(driver.getButton(GamepadKeys.Button.B)){
            speedMultiplier=0.5;
        }
        // Max speed is 1
        if(speedMultiplier>1){
            speedMultiplier=1;
        }
        // Min speed is 0.25
        if(speedMultiplier<0.25){
            speedMultiplier=0.25;
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
                targetHeading = -(Math.atan2(rightStickY, rightStickX) - Math.PI/2);

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
        if(useFieldCentricDrive){
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
        if (driver.wasJustPressed((GamepadKeys.Button.RIGHT_BUMPER))){
            if(Math.abs(intakePower) == 1){
                intakePower = 0;
            }else{
                intakePower = 1;
            }
        } else if (driver.wasJustPressed(GamepadKeys.Button.LEFT_BUMPER)) {
            if(Math.abs(intakePower) == 1){
                intakePower = 0;
            }else{
                intakePower = -1;
            }
        }
        intake.setMotorPower(intakePower);

        double operatorLeftTrigger = operator.getTrigger(GamepadKeys.Trigger.LEFT_TRIGGER);
        double operatorRightTrigger = operator.getTrigger(GamepadKeys.Trigger.RIGHT_TRIGGER);

        if(operatorLeftTrigger>0.1){
            outtakeSpeed += operatorLeftTrigger*500;
        }
        if(operatorRightTrigger > 0.1){
            outtakeSpeed -= operatorRightTrigger*500;
        }

        if(outtakeSpeed > 3000){
            outtakeSpeed = 3000;
        }
        if(outtakeSpeed<500){
            outtakeSpeed = 500;
        }

        telemetry.addData("Outtake Speed", outtakeSpeed);

        // Manual arm control (only when in IDLE state)
        if (armState == ArmState.IDLE) {
            double joystickInput = operator.getLeftY();

            // Only update target if joystick is being moved
            if (Math.abs(joystickInput) > 0.05) {
                armTarget += joystickInput * 20; // Reduced from 50 for smoother control
                holdPosition_Arm = true; // Enable PID when manually controlling
                timer.reset(); // Reset timer to keep PID active
            }

            // Clamp to bounds
            if(armTarget > 700){
                armTarget = 700;
            }
            if (armTarget < 100){
                armTarget = 100;
            }
        }
        telemetry.addData("Arm target", armTarget);
        telemetry.addData("Arm state", armState);

        // State machine for automated sequences
        switch (armState) {
            case IDLE:
                // Check for sequence triggers
                if (operator.wasJustPressed(GamepadKeys.Button.DPAD_UP)) {
                    armTarget = 500;
                    timer.reset();
                    holdPosition_Arm = true;
                }
                else if (operator.wasJustPressed(GamepadKeys.Button.DPAD_DOWN)) {
                    armState = ArmState.ARM_DOWN_STEP1;
                    armTarget = 300;
                    stateTimer.reset();
                }
                else if (operator.wasJustPressed(GamepadKeys.Button.RIGHT_BUMPER)) {
                    armState = ArmState.OUTTAKE_LIFT;
                    hinge.liftHinge(hinge.holdPosition);
                    outtake.setFlywheelVelocity((float) outtakeSpeed);
                    stateTimer.reset();
                }
                else if (operator.getButton(GamepadKeys.Button.DPAD_RIGHT)) {
                    armTarget = 0;
                    arm.raiseArmManual(0.25);
                    ElapsedTime timer1 = new ElapsedTime();
                    while (timer1.milliseconds() < 2000){
                        // This is still blocking, but only for calibration
                        // Consider making this non-blocking too if it causes issues
                    }
                    arm.resetArmEncoders();
                }
                break;

            case ARM_DOWN_STEP1:
                if (stateTimer.milliseconds() >= 1000) {
                    armState = ArmState.ARM_DOWN_STEP2;
                    armTarget = 100;
                    stateTimer.reset();
                }
                break;

            case ARM_DOWN_STEP2:
                if (stateTimer.milliseconds() >= 500) {
                    armState = ArmState.ARM_DOWN_STEP3;
                    armTarget = 0;
                    stateTimer.reset();
                }
                break;

            case ARM_DOWN_STEP3:
                if (stateTimer.milliseconds() >= 100) {
                    armState = ArmState.ARM_DOWN_FINISH;
                    timer.reset();
                    holdPosition_Arm = false;
                    stateTimer.reset();
                }
                break;

            case ARM_DOWN_FINISH:
                armState = ArmState.IDLE;
                break;

            case OUTTAKE_LIFT:
                if (stateTimer.milliseconds() >= 1000) {
                    armState = ArmState.OUTTAKE_FIRE;
                    hinge.liftHinge(hinge.firePosition);
                    stateTimer.reset();
                }
                break;

            case OUTTAKE_FIRE:
                if (stateTimer.milliseconds() >= 1000) {
                    armState = ArmState.OUTTAKE_FINISH;
                    velocityPeak = outtake.getCurrentWheelRPM();
                    outtake.setFlywheelVelocity(0);
                    hinge.liftHinge(hinge.holdPosition);
                    stateTimer.reset();
                }
                break;

            case OUTTAKE_FINISH:
                armState = ArmState.IDLE;
                break;
        }

        // Continuous PID control for arm (runs every loop)
        if (holdPosition_Arm || timer.milliseconds() < 2000){
            arm.raiseArmManual(arm.setArmTarget(armTarget));
        } else {
            arm.raiseArmManual(0);
        }

        driver.readButtons();
        operator.readButtons();
        telemetry.update();
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

    public void stop(){
        opModeIsActive=false;
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

    public void sleepWhileRunningArmPID(double milliseconds){
        ElapsedTime sleepTimer;
        sleepTimer = new ElapsedTime();
        while (sleepTimer.milliseconds()<milliseconds && opModeIsActive){
            arm.raiseArmManual(arm.setArmTarget(armTarget));
        }
    }
}