/**
 *  <DRIVER MANUAL>
 *
 *  --DRIVER CONTROLS--
 *
 *  [MOVEMENT]
 *  LEFT STICK Y = forward / backward
 *  RIGHT STICK X = turn
 *  DPAD UP       = drive speed up
 *  DPAD DOWN     = drive speed down
 *  LEFT TRIGGER  = Slow / Precision Mode (0.3x speed)
 *  RIGHT TRIGGER = Turbo Mode (1.0x speed)
 *  LEFT STICK BUTTON  = toggle "Spin Cycle" dance (press again, or move a stick, to cancel)
 *  RIGHT STICK BUTTON = toggle Fine Tuning Mode (NONE -> RAISED -> LOWERED)
 *
 *  [ARM]
 *  RIGHT BUMPER (hold) = raise arm (Rumbles at limit)
 *  LEFT BUMPER (hold)  = lower arm (Rumbles at limit)
 *  DPAD LEFT           = arm to raised preset
 *  DPAD RIGHT          = arm to lowered preset
 *  X                   = in Fine Tuning: increment limit/preset
 *  Y                   = in Fine Tuning: decrement limit/preset
 *  (Hold X or Y to tune faster)
 *
 *  [SAFETY]
 *  ANTI-TIP: Drive speed is automatically capped when arm is raised high.
 *
 *  [HAND / GRIPPER]
 *  A = open gripper
 *  B = close gripper
 */
package org.firstinspires.ftc.teamcode.DriveCode;

import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.arcrobotics.ftclib.gamepad.GamepadKeys;
import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.VoltageSensor;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.teamcode.DriveCode.Tuning.ArmPIDTuner;

import java.util.List;

@TeleOp(name = "PushBot v4a DriveCode", group = "A - TeleOP")
public class PushbotCode extends OpMode {

    public ArmPIDTuner armTuning;
    public GamepadEx driver;
    List<LynxModule> allHubs;

    // Hardware - expansion hub motor port 0/1/2, servo port 0/1
    DcMotor leftDrive;
    DcMotor rightDrive;
    Servo leftClaw;
    Servo rightClaw;
    VoltageSensor voltageSensor;

    double driveSpeed = 1.0;
    double lastLeftPower = 0;
    double lastRightPower = 0;
    int armProfileTarget = 0;
    ElapsedTime loopTimer = new ElapsedTime();

    public static double DRIVE_SLEW_RATE = 3.0; // Power units per second
    public static double ARM_MOTION_RATE = 400.0; // Ticks per second

    public static final double DRIVE_SPEED_STEP = 0.1;
    public static final double DRIVE_SPEED_MIN = 0.1;
    public static final double DRIVE_SPEED_MAX = 1.0;

    public static final double HAND_OPEN = 0;
    public static final double HAND_CLOSED = 0.2;

    public static final double LEFT_HAND_OPEN = 0.2;
    public static final double LEFT_HAND_CLOSED = 0;

    public static int ARM_MIN = -500;
    public static int ARM_MAX = 500;
    public static int ARM_RAISED_PRESET = 500;
    public static int ARM_LOWERED_PRESET = -500;

    public static final int ARM_ANTI_TIP_THRESHOLD = 250;
    public static final double ANTI_TIP_SPEED_CAP = 0.4;

    // --- Dance mode ---

    private enum Dance { NONE, SPIN_CYCLE }

    private static final double STICK_CANCEL_THRESHOLD = 0.2;

    private Dance activeDance = Dance.NONE;
    private int danceStep = 0;
    private int lastAppliedDanceStep = -1;
    private final ElapsedTime danceTimer = new ElapsedTime();

    // --- Fine tuning mode ---

    private enum TuningMode { NONE, TUNE_RAISED, TUNE_LOWERED }
    private TuningMode currentTuningMode = TuningMode.NONE;

    private final ElapsedTime tuningRepeatTimer = new ElapsedTime();
    private int tuningRepeatCount = 0;
    private static final double TUNING_REPEAT_DELAY = 0.5;
    private static final double TUNING_REPEAT_INTERVAL = 0.05;

    /** One beat of a dance: drive powers to hold, how long to hold them, and optional arm/claw cues. */
    private static class DanceStep {
        final double leftPower;
        final double rightPower;
        final double duration;
        final Integer armTarget; // null = leave unchanged
        final Boolean clawOpen;  // null = leave unchanged

        DanceStep(double leftPower, double rightPower, double duration) {
            this(leftPower, rightPower, duration, null, null);
        }

        DanceStep(double leftPower, double rightPower, double duration, Integer armTarget, Boolean clawOpen) {
            this.leftPower = leftPower;
            this.rightPower = rightPower;
            this.duration = duration;
            this.armTarget = armTarget;
            this.clawOpen = clawOpen;
        }
    }

    // "Spin Cycle" - alternating spins finished off with a gripper snap
    private final DanceStep[] SPIN_CYCLE_DANCE = {
            new DanceStep(0.6, -0.6, 0.6),
            new DanceStep(-0.6, 0.6, 0.6),
            new DanceStep(0.6, -0.6, 0.6),
            new DanceStep(-0.6, 0.6, 0.6),
            new DanceStep(0, 0, 0.2, null, true),
            new DanceStep(0, 0, 0.2, null, false),
            new DanceStep(0, 0, 0.2, null, true),
            new DanceStep(0, 0, 0.2, null, false),
    };

    @Override
    public void init() {

        driver = new GamepadEx(gamepad1);

        leftDrive = hardwareMap.get(DcMotor.class, "leftDrive");
        rightDrive = hardwareMap.get(DcMotor.class, "rightDrive");
        leftClaw = hardwareMap.get(Servo.class, "leftClaw");
        rightClaw = hardwareMap.get(Servo.class, "rightClaw");

        armTuning = new ArmPIDTuner(hardwareMap);
        voltageSensor = hardwareMap.voltageSensor.iterator().next();

        leftDrive.setDirection(DcMotorSimple.Direction.REVERSE);

        leftClaw.setPosition(LEFT_HAND_CLOSED);
        rightClaw.setPosition(HAND_CLOSED);

        armProfileTarget = ArmPIDTuner.targetPosition;
        loopTimer.reset();

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        allHubs = hardwareMap.getAll(LynxModule.class);
        for (LynxModule hub : allHubs) {
            hub.setBulkCachingMode(LynxModule.BulkCachingMode.MANUAL);
        }
    }

    @Override
    public void loop() {
        driver.readButtons();

        double dt = loopTimer.seconds();
        loopTimer.reset();

        double forward = -driver.getLeftY();
        double turn = driver.getRightX();

        // Dance mode toggle - re-pressing the stick button, or nudging a stick, cancels it
        if (driver.wasJustPressed(GamepadKeys.Button.LEFT_STICK_BUTTON)) {
            toggleDance(Dance.SPIN_CYCLE);
            gamepad1.rumble(0.5, 0.5, 200);
        } else if (driver.wasJustPressed(GamepadKeys.Button.RIGHT_STICK_BUTTON)) {
            // Cycle tuning mode: NONE -> RAISED -> LOWERED -> NONE
            switch (currentTuningMode) {
                case NONE:
                    currentTuningMode = TuningMode.TUNE_RAISED;
                    gamepad1.rumble(0.5, 0.5, 100);
                    break;
                case TUNE_RAISED:
                    currentTuningMode = TuningMode.TUNE_LOWERED;
                    gamepad1.rumble(1.0, 1.0, 100);
                    break;
                case TUNE_LOWERED:
                    currentTuningMode = TuningMode.NONE;
                    gamepad1.rumbleBlips(3);
                    break;
            }
        } else if (activeDance != Dance.NONE
                && (Math.abs(forward) > STICK_CANCEL_THRESHOLD || Math.abs(turn) > STICK_CANCEL_THRESHOLD)) {
            activeDance = Dance.NONE;
        }

        double leftPower;
        double rightPower;

        if (activeDance != Dance.NONE) {
            runDance();
            leftPower = leftDrive.getPower();
            rightPower = rightDrive.getPower();
        } else {
            // Drive speed adjust
            if (driver.wasJustPressed(GamepadKeys.Button.DPAD_UP)) {
                driveSpeed = Math.min(DRIVE_SPEED_MAX, driveSpeed + DRIVE_SPEED_STEP);
            } else if (driver.wasJustPressed(GamepadKeys.Button.DPAD_DOWN)) {
                driveSpeed = Math.max(DRIVE_SPEED_MIN, driveSpeed - DRIVE_SPEED_STEP);
            }

            // Apply Exponential Curve to joysticks for better precision
            forward = forward * Math.abs(forward);
            turn = turn * Math.abs(turn);

            // Trigger Scaling (Slow/Turbo)
            double currentScale = driveSpeed;
            if (driver.getTrigger(GamepadKeys.Trigger.LEFT_TRIGGER) > 0.1) {
                currentScale = 0.3; // Precision Mode
            } else if (driver.getTrigger(GamepadKeys.Trigger.RIGHT_TRIGGER) > 0.1) {
                currentScale = 1.0; // Turbo Mode
            }

            // Anti-Tip Safety: Cap speed when arm is high
            if (armTuning.getArmPosition() > ARM_ANTI_TIP_THRESHOLD) {
                currentScale = Math.min(currentScale, ANTI_TIP_SPEED_CAP);
            }

            // Drive
            double targetLeft = Range.clip(forward - turn, -1.0, 1.0) * currentScale;
            double targetRight = Range.clip(forward + turn, -1.0, 1.0) * currentScale;

            // Voltage Compensation
            double voltage = voltageSensor.getVoltage();
            double voltageComp = 12.0 / Math.max(voltage, 1.0);
            targetLeft *= voltageComp;
            targetRight *= voltageComp;

            // Slew Rate Limiter
            double maxChange = DRIVE_SLEW_RATE * dt;
            leftPower = lastLeftPower + Range.clip(targetLeft - lastLeftPower, -maxChange, maxChange);
            rightPower = lastRightPower + Range.clip(targetRight - lastRightPower, -maxChange, maxChange);

            lastLeftPower = leftPower;
            lastRightPower = rightPower;

            leftDrive.setPower(leftPower);
            rightDrive.setPower(rightPower);

            // Arm
            if (driver.getButton(GamepadKeys.Button.RIGHT_BUMPER)) {
                if (armProfileTarget >= ARM_MAX) {
                    gamepad1.rumble(0.1, 0.1, 50); // Small buzz at limit
                }
                armProfileTarget = Math.min(ARM_MAX, armProfileTarget + 5); // Faster manual adjust for profiling
            } else if (driver.getButton(GamepadKeys.Button.LEFT_BUMPER)) {
                if (armProfileTarget <= ARM_MIN) {
                    gamepad1.rumble(0.1, 0.1, 50); // Small buzz at limit
                }
                armProfileTarget = Math.max(ARM_MIN, armProfileTarget - 5);
            }

            if (driver.getButton(GamepadKeys.Button.DPAD_LEFT)) {
                armProfileTarget = ARM_RAISED_PRESET;
            } else if (driver.getButton(GamepadKeys.Button.DPAD_RIGHT)) {
                armProfileTarget = ARM_LOWERED_PRESET;
            }

            // Arm Motion Profiling: move static target towards desired profile target
            double maxArmChange = ARM_MOTION_RATE * dt;
            ArmPIDTuner.targetPosition = (int) (ArmPIDTuner.targetPosition + Range.clip(armProfileTarget - ArmPIDTuner.targetPosition, -maxArmChange, maxArmChange));

            // Fine Tuning Logic
            if (currentTuningMode != TuningMode.NONE) {
                boolean x = driver.getButton(GamepadKeys.Button.X);
                boolean y = driver.getButton(GamepadKeys.Button.Y);

                if (x || y) {
                    boolean justPressed = driver.wasJustPressed(GamepadKeys.Button.X) || driver.wasJustPressed(GamepadKeys.Button.Y);
                    double elapsed = tuningRepeatTimer.seconds();

                    if (justPressed || (elapsed > TUNING_REPEAT_DELAY && elapsed > (TUNING_REPEAT_DELAY + tuningRepeatCount * TUNING_REPEAT_INTERVAL))) {
                        if (justPressed) {
                            tuningRepeatTimer.reset();
                            tuningRepeatCount = 0;
                        } else {
                            tuningRepeatCount++;
                        }

                        int delta = x ? 1 : -1;
                        if (currentTuningMode == TuningMode.TUNE_RAISED) {
                            ARM_MAX += delta;
                            ARM_RAISED_PRESET += delta;
                        } else {
                            ARM_MIN += delta;
                            ARM_LOWERED_PRESET += delta;
                        }
                    }
                } else {
                    tuningRepeatTimer.reset();
                    tuningRepeatCount = 0;
                }
            }

            // Hand / Gripper
            if (driver.wasJustPressed(GamepadKeys.Button.A)) {
                leftClaw.setPosition(LEFT_HAND_OPEN);
                rightClaw.setPosition(HAND_OPEN);
            } else if (driver.wasJustPressed(GamepadKeys.Button.B)) {
                leftClaw.setPosition(LEFT_HAND_CLOSED);
                rightClaw.setPosition(HAND_CLOSED);
            }
        }

        armTuning.update();

        // Displays important information for driver
        telemetry.addLine("--- DRIVE ---");
        telemetry.addData("Voltage", "%.2fV (Comp: %.2f)", voltageSensor.getVoltage(), 12.0 / Math.max(voltageSensor.getVoltage(), 1.0));
        telemetry.addData("Speed Scale", "%.2f", driveSpeed);
        telemetry.addData("Forward / Turn", "%.2f / %.2f", forward, turn);
        telemetry.addData("Powers (L/R)", "%.2f / %.2f", leftPower, rightPower);

        telemetry.addLine("--- ARM & CLAW ---");
        telemetry.addData("Arm Position", armTuning.getArmPosition());
        telemetry.addData("Arm Target", ArmPIDTuner.targetPosition);
        telemetry.addData("Presets (Hi/Lo)", "%d / %d", ARM_RAISED_PRESET, ARM_LOWERED_PRESET);
        telemetry.addData("Limits (Hi/Lo)", "%d / %d", ARM_MAX, ARM_MIN);
        telemetry.addData("Claw (L/R)", "%.2f / %.2f", leftClaw.getPosition(), rightClaw.getPosition());

        telemetry.addLine("--- STATUS ---");
        if (activeDance != Dance.NONE) {
            telemetry.addData("Dance", "%s (Step %d, %.1fs)", activeDance, danceStep, danceTimer.seconds());
        } else {
            telemetry.addData("Dance", "None");
        }

        if (currentTuningMode != TuningMode.NONE) {
            telemetry.addData("Tuning Mode", "%s (Repeat: %d)", currentTuningMode, tuningRepeatCount);
        } else {
            telemetry.addData("Tuning Mode", "None");
        }
        telemetry.update();

        for (LynxModule hub : allHubs) {
            hub.clearBulkCache();
        }
    }

    private void toggleDance(Dance dance) {
        if (activeDance == dance) {
            activeDance = Dance.NONE;
        } else {
            activeDance = dance;
            danceStep = 0;
            lastAppliedDanceStep = -1;
            danceTimer.reset();
        }
    }

    /** Advances the active dance's step sequencer by one loop tick without blocking. */
    private void runDance() {
        DanceStep[] sequence = SPIN_CYCLE_DANCE;

        if (danceStep >= sequence.length) {
            activeDance = Dance.NONE;
            leftDrive.setPower(0);
            rightDrive.setPower(0);
            return;
        }

        DanceStep step = sequence[danceStep];

        if (danceStep != lastAppliedDanceStep) {
            lastAppliedDanceStep = danceStep;
            danceTimer.reset();

            if (step.armTarget != null) {
                ArmPIDTuner.targetPosition = step.armTarget;
            }
            if (step.clawOpen != null) {
                leftClaw.setPosition(step.clawOpen ? LEFT_HAND_OPEN : LEFT_HAND_CLOSED);
                rightClaw.setPosition(step.clawOpen ? HAND_OPEN : HAND_CLOSED);
            }
        }

        leftDrive.setPower(step.leftPower);
        rightDrive.setPower(step.rightPower);

        if (danceTimer.seconds() >= step.duration) {
            danceStep++;
        }
    }

    @Override
    public void stop() {
        leftDrive.setPower(0);
        rightDrive.setPower(0);
        armTuning.stopMotor();
    }
}
