package org.firstinspires.ftc.teamcode.Controllers;

import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Hardware.Intake;
import org.firstinspires.ftc.teamcode.Hardware.Transfer;

public class IntakeController {

    private float targetSpeed = .7f; // determines how fast the intake should run
    private float rampUpSpeed = 1; // how fast intake should ramp up to target speed (in seconds)
    private Intake intake;
    private Transfer transferDrum;
    private Transfer transferKick;

    private float intakePower = 0;
    private float transferPower = 0;

    private double drumRPM = 0; // stores current transfer drum rotation speed (RPM)
    double currentTime = 0;
    public double intakeRPM;

    // ── Jam detection (first run) ─────────────────────────────────────────────
    private static final double INTAKE_JAM_RPM_THRESHOLD = 600; // RPM below which intake is considered jammed
    private static final double JAM_WARMUP_MS = 1000;           // wait 1s after start before checking for jams
    private static final double JAM_SHUTOFF_MS = 0;          // hold jam for 1s before shutting off
    private boolean intakeJamDetected = false;
    private final ElapsedTime intakeStartTimer = new ElapsedTime();
    private final ElapsedTime intakeJamTimer = new ElapsedTime();

    // ── Post-jam slow-run sequence ────────────────────────────────────────────
    private static final double POST_JAM_SLOW_POWER = 0.25;
    private static final double POST_JAM_SLOW_MS = 1000;
    private final ElapsedTime postJamTimer = new ElapsedTime();

    // ── Velocity-drop detection (second run) ──────────────────────────────────
    private static final double VELOCITY_DROP_WARMUP_MS = 600;  // wait before watching for velocity drop
    private static final double VELOCITY_DROP_THRESHOLD = 150;  // RPM drop per sample that counts as a drop
    private double lastIntakeRPM = 0;

    // ── State machine ─────────────────────────────────────────────────────────
    private enum IntakeState {
        IDLE,
        FIRST_RUN,       // Normal intake — stops on sustained jam
        SLOW_RUN,        // Post-jam slow sequence for 1 second
        SECOND_RUN       // Restarts after slow-run — stops on velocity drop
    }
    private IntakeState intakeState = IntakeState.IDLE;

    private ElapsedTime transferStartTimer = new ElapsedTime();

    int ballsFed = 0;

    public IntakeController(Intake intake, Transfer transferDrum, Transfer transferKick) {
        this.intake = intake;
        this.transferDrum = transferDrum;
        this.transferKick = transferKick;
    }

    public boolean isIntakeRunning() {
        return Math.abs(intakePower) > 0.1;
    }

    public double getIntakePower() {
        return intake.getIntakePower();
    }

    public double getDrumRPM() {
        return drumRPM;
    }

    public void toggleIntake() {
        if (intakeState != IntakeState.IDLE) {
            // Any toggle while running cancels everything
            stopAll();
        } else {
            enterFirstRun();
        }
    }

    public void toggleReverse() {
        if (Math.abs(intakePower) >= targetSpeed && intakePower < 0) {
            stopAll();
        } else {
            stopAll();
            intakePower = -targetSpeed;
            transferPower = -targetSpeed;
            // Reverse is manual only — does not enter state machine
        }
    }

    // ── State transition helpers ──────────────────────────────────────────────

    private void enterFirstRun() {
        intakePower = targetSpeed;
        transferPower = 0;
        intakeJamDetected = false;
        intakeState = IntakeState.FIRST_RUN;
        intakeStartTimer.reset();
    }

    private void enterSlowRun() {
        intakePower = 0;
        transferPower = 0;
        intakeState = IntakeState.SLOW_RUN;
        postJamTimer.reset();
    }

    private void enterSecondRun() {
        intakePower = targetSpeed;
        transferPower = 0;
        lastIntakeRPM = 0; // reset so we don't compare against stale data
        intakeState = IntakeState.SECOND_RUN;
        intakeStartTimer.reset();
    }

    public double getCurrentTime() {
        return currentTime;
    }

    public void update() {
        // Update RPM readings
        drumRPM = transferDrum.getTransferDrumRPM();
        intakeRPM = intake.getIntakeMotorRPM();

        if (intakeRPM < VELOCITY_DROP_THRESHOLD) {
            // Velocity dropped sharply — balls are seated, stop everything
            intakePower = 0.25f;
        }


//        switch (intakeState) {
//
//            case IDLE:
//                break;
//
//            // ── FIRST RUN: stop on sustained jam ─────────────────────────────
//            case FIRST_RUN:
//                if (intakeStartTimer.milliseconds() > JAM_WARMUP_MS) {
//                    if (intakeRPM < INTAKE_JAM_RPM_THRESHOLD) {
//                        if (!intakeJamDetected) {
//                            intakeJamDetected = true;
//                            intakeJamTimer.reset();
//                        } else if (intakeJamTimer.milliseconds() >= JAM_SHUTOFF_MS) {
//                            // Jam confirmed — move to slow-run sequence
//                            enterSlowRun();
//                        }
//                    } else {
//                        intakeJamDetected = false;
//                    }
//                }
//                break;
//
//            // ── SLOW RUN: run intake + transfer slowly for 1 second ───────────
//            case SLOW_RUN:
//                intake.setIntakePower(POST_JAM_SLOW_POWER);
//                transferDrum.runTransferDrum(POST_JAM_SLOW_POWER);
//
//                if (postJamTimer.milliseconds() >= POST_JAM_SLOW_MS) {
////                    intakePower = 0;
////                    transferPower = 0;
//                    intakePower = 0.25f;
//                    intake.setIntakePower(0);
//                    transferDrum.runTransferDrum(0);
//                    enterSecondRun();
//                }
//                return; // Skip the power-set at the bottom while in slow run
//
//            // ── SECOND RUN: stop on velocity drop ────────────────────────────
//            case SECOND_RUN:
//                intake.setIntakePower(0.25);
////                if (intakeStartTimer.milliseconds() > VELOCITY_DROP_WARMUP_MS) {
////                    double rpmDrop = lastIntakeRPM - intakeRPM;
////                    if (rpmDrop > VELOCITY_DROP_THRESHOLD) {
////                        // Velocity dropped sharply — balls are seated, stop everything
////                        stopAll();
////                        return;
////                    }
////                }
////                lastIntakeRPM = intakeRPM;
//                break;
//        }

        intake.setIntakePower(intakePower);
        transferDrum.runTransferDrum(transferPower);
    }

    public int getBallsFed() {
        return ballsFed;
    }

    public float getTransferPower() {
        return transferPower;
    }

    public void stopAll() {
        intakePower = 0;
        transferPower = 0;
        intakeJamDetected = false;
        intakeState = IntakeState.IDLE;
        intake.setIntakePower(0);
        transferDrum.runTransferDrum(0);
    }

    public void runPower() {
        intake.setIntakePower(0.5);
        transferDrum.runTransferDrum(0.5);
    }

    public void transferKickUp() {
        transferKick.setTransferKickUp();
    }

    public void transferKickDown() {
        transferKick.setTransferKickDown();
    }
}