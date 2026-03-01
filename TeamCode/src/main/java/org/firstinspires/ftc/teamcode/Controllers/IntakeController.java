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

    // Jam detection
    private static final double INTAKE_JAM_RPM_THRESHOLD = 200; // RPM below which intake is considered jammed
    private static final double JAM_WARMUP_MS = 1000;           // wait 1s after start before checking for jams
    private static final double JAM_SHUTOFF_MS = 0;          // hold jam for 1s before shutting off
    private boolean intakeJamDetected = false;
    private final ElapsedTime intakeStartTimer = new ElapsedTime();
    private final ElapsedTime intakeJamTimer = new ElapsedTime();

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
            intakeJamDetected = false;
            intakeStartTimer.reset();
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

        // Intake jam detection
        // Only check after warmup period to allow the motor to spin up
        if (intakePower > 0 && intakeStartTimer.milliseconds() > JAM_WARMUP_MS) {
            if (intakeRPM < INTAKE_JAM_RPM_THRESHOLD) {
                if (!intakeJamDetected) {
                    // Jam just started — begin the shutoff countdown
                    intakeJamDetected = true;
                    intakeJamTimer.reset();
                } else if (intakeJamTimer.milliseconds() >= JAM_SHUTOFF_MS) {
                    // Jam has persisted for 1 second — shut off the intake
                    intakePower = 0;
                    transferPower = 0;
                }
            } else {
                // RPM recovered — clear the jam flag
                intakeJamDetected = false;
            }
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
