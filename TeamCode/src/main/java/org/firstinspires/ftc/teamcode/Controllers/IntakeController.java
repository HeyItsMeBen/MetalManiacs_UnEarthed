package org.firstinspires.ftc.teamcode.Controllers;

import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Hardware.Intake;
import org.firstinspires.ftc.teamcode.Hardware.Transfer;

public class IntakeController {

    //Mechanisms
    private Intake intake;
    private Transfer transferDrum;
    private Transfer transferKick;

    //intake and transfer setup
    private float targetSpeed = .7f; // determines how fast the intake should run
    private float rampUpSpeed = 1; // how fast intake should ramp up to target speed (in seconds)
    private float intakePower = 0;
    private float transferPower = 0;

    // Transfer (drum) jam detection
    private static final double TRANSFER_JAM_RPM_THRESHOLD = 800; // rpm above which transfer is considered jammed
    private static final double TRANSFER_JAM_WARMUP_MS = 300;   // allow some spin-up time before checking
    private static final double TRANSFER_JAM_SHUTOFF_MS = 500;  // if jam persists this long, stop the drum
    private boolean transferJamDetected = false;
    private final ElapsedTime transferStartTimer = new ElapsedTime();
    private final ElapsedTime transferJamTimer = new ElapsedTime();
    // Track whether the transfer was commanded in the previous loop to detect rising edge
    private boolean transferCommandedLast = false;

    private double drumRPM = 0; // stores current transfer drum rotation speed (RPM)
    double currentTime = 0;
    public double intakeRPM;

    //Intake Jam detection
    private static final double INTAKE_JAM_RPM_THRESHOLD = 200; //RPM below which intake is considered jammed
    private static final double JAM_WARMUP_MS = 1000;           // wait 1s after start before checking for jams
    private static final double JAM_SHUTOFF_MS = 0;          // hold jam for 1s before shutting off
    private boolean intakeJamDetected = false;
    private final ElapsedTime intakeStartTimer = new ElapsedTime();
    private final ElapsedTime intakeJamTimer = new ElapsedTime();
    public boolean  isJammed = false;

    int ballsFed = 0;

    //constructor
    public IntakeController(Intake intake, Transfer transferDrum, Transfer transferKick) {
        this.intake = intake;
        this.transferDrum = transferDrum;
        this.transferKick = transferKick;
    }

    //Intake Control
    public void toggleIntake() {

        if (Math.abs(intakePower) >= 0.5) {
            intakePower = 0;
            transferPower = 0;
        } else {
            intakePower = targetSpeed;
            if (!isJammed){
                transferPower = targetSpeed; // transfer runs slower to prevent jams
            }
            intakeJamDetected = false;
            intakeStartTimer.reset();
            // Starting the transfer: clear transfer jam state and reset its timer
            transferJamDetected = false;
            transferStartTimer.reset();
            transferCommandedLast = true; // consider transfer commanded after toggling on
        }
    }

    public void toggleReverse() {

        if (Math.abs(intakePower) >= targetSpeed) {
            intakePower = 0;
            transferPower = 0;

        } else {
            isJammed = false;
            intakePower = -targetSpeed;
            transferPower = -targetSpeed;
            // Clearing transfer jam state when we explicitly reverse/start transfer
            transferJamDetected = false;
            transferStartTimer.reset();
            transferCommandedLast = true;
        }
    }

    //primary update method
    public void update(boolean intakeForward, boolean intakeReverse) {
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
                    // Jam has persisted long enough — shut off the intake
                    intakePower = 0;
                    transferPower = 0;
                }
            } else {
                // RPM recovered — clear the jam flag
                intakeJamDetected = false;
            }
        }

        // Transfer drum jam detection
        // Determine the commanded transfer power this loop (what we intend to run)
        float commandedTransferPower;
        if (intakeForward) {
            isJammed = false;
            commandedTransferPower = 0.5f;
        } else if (intakeReverse) {
            isJammed = false;
            commandedTransferPower = -0.5f;
        } else {
            commandedTransferPower = transferPower;
        }

        // Detect rising edge so warmup timer starts when we begin commanding the drum
        if (Math.abs(commandedTransferPower) > 0.1f && !transferCommandedLast) {
            transferStartTimer.reset();
            transferCommandedLast = true;
        }

        // If we stop commanding the drum, clear the timer and state
        if (Math.abs(commandedTransferPower) <= 0.1f) {
            transferStartTimer.reset();
            transferCommandedLast = false;
            transferJamDetected = false;
        }

        // Only check for transfer jams when the drum is being commanded to spin
        if (Math.abs(commandedTransferPower) > 0.1f && drumRPM > 0) {
            // If we've passed the warmup window, begin monitoring RPM
            if (transferStartTimer.milliseconds() > TRANSFER_JAM_WARMUP_MS) {
                if (drumRPM < TRANSFER_JAM_RPM_THRESHOLD) {
                    if (!transferJamDetected) {
                        // first observation of a low RPM — start timer
                        transferJamDetected = true;
                        isJammed = true;
                        transferJamTimer.reset();
                    } else if (transferJamTimer.milliseconds() >= TRANSFER_JAM_SHUTOFF_MS) {
                        // jam persisted — stop the drum
                        transferPower = 0;
                    }
                } else {
                    //RPM recovered — clear jam detection
                    transferJamDetected = false;
                }
            } // else still warming up — don't check yet
        }


        intake.setIntakePower(intakePower);
        if (transferJamDetected) {
            // If transfer is jammed, always keep it stopped
            transferDrum.runTransferDrum(0);
        } else if (intakeForward) {
            transferDrum.runTransferDrum(0.5);
        } else if (intakeReverse){
            transferDrum.runTransferDrum(-0.5);
        } else {
            transferDrum.runTransferDrum(transferPower);
        }
    }

    //Return data about transfer and intake
    public double getIntakePower() {
        return intake.getIntakePower();
    }
    public float getTransferPower(){
        return transferPower;
    }
    public double getIntakeRPM(){
        return intakeRPM;
    }
    public double getTransferRPM(){
        return drumRPM;
    }
    public int getBallsFed() {
        return ballsFed;
    }
    public boolean isIntakeRunning() {
        return Math.abs(intakePower) > 0.1;
    }
    public boolean isTransferJamDetected() { return transferJamDetected; }
    public double getCurrentTime(){
        return currentTime;
    }

    //Transfer kick controls
    public void transferKickUp() {
        transferKick.setTransferKickUp();
    }

    public void transferKickDown(){
        transferKick.setTransferKickDown();
    }

    //other methods
    public void stopAll() {
        intake.setIntakePower(0);
        transferDrum.runTransferDrum(0);
    }

    public void runPower() {
        intake.setIntakePower(0.5);
        transferDrum.runTransferDrum(0.5);
    }
}