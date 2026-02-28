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

    private boolean transferJamDetected = false;
    private boolean intakeJamDetected = false;

    private double drumRPM = 0; // stores current transfer drum rotation speed (RPM)
    private double drumJamRPMThreshold = 100; // RPM below which the drum is considered jammed

    private double intakeRPM = 0; // stores current intake rotation speed (RPM)
    private double intakeJamRPMThreshold = 625; // RPM below which the intake is considered jammed

    private ElapsedTime intakeTimer = new ElapsedTime();
    private ElapsedTime drumJamTimer = new ElapsedTime();
    private ElapsedTime transferStartTimer = new ElapsedTime();
    private ElapsedTime intakeStartTimer = new ElapsedTime();
    private ElapsedTime ballsFedDebounceTimer = new ElapsedTime();
    private ElapsedTime ballsFedStopTimer = new ElapsedTime();
    private boolean ballsFedStopping = false;
    private static final double JAM_WARMUP_MS = 1500; // ms to wait after start before checking for jams
    private static final double BALLS_FED_DEBOUNCE_MS = 500; // min ms between ballsFed increments
    private static final double BALLS_FED_STOP_DELAY_MS = 1000; // ms to run after 3 balls fed before stopping
    double intakeStartTime=0;
    double intakeStartPower=0;
    double currentTime = 0;

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

        if (Math.abs(intakePower) >= 0.5) {
            intakePower = 0;
            transferPower = 0;
        } else {
            intakePower = targetSpeed;
            transferPower = targetSpeed*0.75f;
            transferStartTimer.reset();
            intakeStartTimer.reset();
            transferJamDetected = false;
            intakeJamDetected = false;
            ballsFedStopping = false;
        }
    }

    public void toggleReverse() {

        if (Math.abs(intakePower) >= targetSpeed) {
            intakePower = 0;
            transferPower = 0;

        } else {
            intakePower = -targetSpeed;
            transferPower = -targetSpeed;
        }
    }

    public double getCurrentTime(){
        return currentTime;
    }

    public void update() {
        // Update RPM readings
        drumRPM = transferDrum.getTransferDrumRPM();
        intakeRPM = intake.getIntakeMotorRPM();

        // Transfer drum jam detection (independent)
        // Only check after warmup period to allow motor to spin up
//        if (transferPower > 0 && transferStartTimer.milliseconds() > JAM_WARMUP_MS && drumRPM < drumJamRPMThreshold) {
//            if (!transferJamDetected) {
//                transferJamDetected = true;
//                drumJamTimer.reset(); // start 1-second countdown
//            } else if (drumJamTimer.milliseconds() >= 1500) {
//                transferPower = 0;
//            }
//        } else if (transferPower > 0) {
//            transferJamDetected = false; // clear flag if jam resolves
//        }

        // Intake jam detection (independent)
        // Only check after warmup period to allow motor to spin up
        if (intakePower > 0 && intakeStartTimer.milliseconds() > JAM_WARMUP_MS && intakeRPM < intakeJamRPMThreshold) {
            if (!intakeJamDetected) {
                intakeJamDetected = true;
                if (ballsFedDebounceTimer.milliseconds() >= BALLS_FED_DEBOUNCE_MS) {
                    ballsFed += 1;
                    ballsFedDebounceTimer.reset();
                }
                intakeTimer.reset(); // start 1-second countdown
            } else if (intakeTimer.milliseconds() >= 1000) {
                intakePower = 0;
            }
        } else if (intakePower > 0) {
            intakeJamDetected = false; // clear flag if jam resolves
        }

        if (ballsFed == 1){
            transferPower = 0;
        }

        if (ballsFed >= 3 && !ballsFedStopping) {
            ballsFedStopping = true;
            ballsFedStopTimer.reset(); // start 1-second delay before stopping
        }
        if (ballsFedStopping && ballsFedStopTimer.milliseconds() >= BALLS_FED_STOP_DELAY_MS) {
            intakePower = 0;
            transferPower = 0;
            ballsFed = 0;
            ballsFedStopping = false;
        }


        intake.setIntakePower(intakePower);
        transferDrum.runTransferDrum(transferPower);
    }

    public int getBallsFed() {
        return ballsFed;
    }
    public float getTransferPower(){
        return transferPower;
    }

    public void stopAll() {
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

    public void transferKickDown(){
        transferKick.setTransferKickDown();
    }

}
