package org.firstinspires.ftc.teamcode.Controllers;

import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Hardware.Flywheels;
import org.firstinspires.ftc.teamcode.Hardware.OuttakeHood;
import org.firstinspires.ftc.teamcode.Hardware.Transfer;
import org.firstinspires.ftc.teamcode.Hardware.Intake;

public class FlywheelController {

    //Mechanisms
    private Flywheels flywheels;
    private Transfer transferDrum;
    private Transfer transferKick;
    private Intake intake;
    private OuttakeHood hood;
    private IntakeController intakeController;

    //data
    private int ballsFed = 0;
    private double outtakeSpeedBeforeDrop = 0;

    //speed control
    private double maintainOuttakeSpeed = 500;  //Only used in the very beginning. Before tag has ever been seen.
    public double extraOuttakeSpeed = 0;        //extra outtake speed for manual controls
    private double targetSpeed = 1200;
    private float rampUpSpeed = 3;

    //timers
    private ElapsedTime launchTimer = new ElapsedTime();
    private ElapsedTime powerUpTimer = new ElapsedTime();
    double currentTime = 0;
    double flywheelStartTime=0;
    double flywheelStartPower=0;
    private double transferCycleDelayMs = 1000; // time between balls being cycled
    //other
    public boolean shouldRumble = false;
    public enum LaunchState {   //List of all launch states
        IDLE,
        SPINNING_UP,
        WAITING_AFTER_SPINUP,
        FEEDING_BALL,
        WAITING_BETWEEN_BALLS
    }

    private LaunchState launchState = LaunchState.IDLE;

    //constructor
    public FlywheelController(Flywheels flywheels,
                              Transfer transferDrum, Transfer transferKick,
                              Intake intake, OuttakeHood hood, IntakeController intakeController) {

        this.flywheels = flywheels;
        this.transferDrum = transferDrum;
        this.transferKick = transferKick;
        this.intake = intake;
        this.hood = hood;
        this.intakeController = intakeController;
    }

    //Retrieve information
    public LaunchState getState() {
        return launchState;
    }

    public double getTargetSpeed() {
        return targetSpeed;
    }

    public double getMaintainSpeed() {
        return maintainOuttakeSpeed;
    }
    public boolean isBusy() {
        return launchState != LaunchState.IDLE;
    }

    public boolean isIdle() {
        return launchState == LaunchState.IDLE;
    }

    //controls
    public void setExtraSpeed(double extra) {
        extraOuttakeSpeed = extra;
    }

    public void rampUp() {

        targetSpeed = 800;

        double targetSeconds = rampUpSpeed *1000; // convert rampUpSpeed to milliseconds
        currentTime=System.currentTimeMillis()-flywheelStartTime;
        if (currentTime<targetSeconds){
            flywheels.setFlywheelVelocity(flywheelStartPower*((targetSeconds-currentTime)/targetSeconds)+targetSpeed*(currentTime/targetSeconds));
        } else {
            flywheels.setFlywheelVelocity(targetSpeed);
        }

    }

    //update method
    public void update(boolean triggerPressed,
                       double distanceToTag,
                       boolean tagVisible) {

        if (triggerPressed) {

            intake.setIntakePower(.7);
            intakeController.isJammed = false;

            switch (launchState) {

                case IDLE:

                    if (distanceToTag!=0) { //If the tag has been seen and distance is calculated, set the target speed.
                        targetSpeed = flywheels.getVelocityFromDistance(distanceToTag);
                        flywheelStartTime=System.currentTimeMillis();
                        flywheelStartPower= flywheels.getFlywheelVelocity();
                    } else {    //otherwise, stay at the slow default speed
                        targetSpeed = maintainOuttakeSpeed;
                        flywheelStartTime=System.currentTimeMillis();
                        flywheelStartPower= flywheels.getFlywheelVelocity();
                    }

                    powerUpTimer.reset();
                    launchTimer.reset();
                    launchState = LaunchState.SPINNING_UP;
                    ballsFed = 0;
                    break;

                case SPINNING_UP:

//                    rampUp();
                    if (flywheels.getFlywheelVelocity() >= targetSpeed * 0.9
                            || powerUpTimer.seconds() >= rampUpSpeed + 1.5) {

                        launchTimer.reset();
                        launchState = LaunchState.WAITING_AFTER_SPINUP;
                    }
                    break;

                case WAITING_AFTER_SPINUP:

                    if (launchTimer.milliseconds() > 500) {

                        outtakeSpeedBeforeDrop =
                                flywheels.getFlywheelVelocity();

                        maintainOuttakeSpeed =
                                flywheels.getFlywheelVelocity();

                        launchTimer.reset();
                        launchState =
                                LaunchState.FEEDING_BALL;
                    }
                    break;

                case FEEDING_BALL:

                    if (ballsFed > 0) {
                        intake.setIntakePower(1);
                    }

//                    if (ballsFed >= 2){
//                        transferKick.setTransferKickUp();
//                    }

                    transferDrum.runTransferDrum(1);

                    if (flywheels.getFlywheelVelocity()
                            < outtakeSpeedBeforeDrop - 100) {   //waits for an artifact to launch

                        transferDrum.runTransferDrum(0);    //shut off wheels to prevent more launches, until the flywheel has gotten back up to speed.
                        intake.setIntakePower(0);

                        ballsFed++;
                        shouldRumble = true;
                        launchTimer.reset();

                        if (ballsFed < 3) {
                            launchState =
                                    LaunchState.WAITING_BETWEEN_BALLS;  //allows the flywheel to get back up to speed
                        } else {
                            launchState = LaunchState.IDLE;
                        }

                    } else if (launchTimer.milliseconds() > 1800) { //go to idle mode if no balls were launched for 1.8 seconds. Idk why we have this, but keep it anyways for now.

                        transferDrum.runTransferDrum(0);
                        intake.setIntakePower(0);
                        launchState = LaunchState.IDLE;
                    }
                    break;

                case WAITING_BETWEEN_BALLS:
//                    transferDrum.runTransferDrum(0);
                    transferKick.setTransferKickDown();
                    if (launchTimer.milliseconds() > transferCycleDelayMs) {
                        outtakeSpeedBeforeDrop =
                                flywheels.getFlywheelVelocity();

                        maintainOuttakeSpeed =
                                flywheels.getFlywheelVelocity();

                        launchState =
                                LaunchState.FEEDING_BALL;

                        launchTimer.reset();
                    }
                    break;
            }

        } else {    //do this if trigger is not pressed
            if (tagVisible && distanceToTag != 0) {
                targetSpeed = flywheels.getVelocityFromDistance(distanceToTag);
            }

            if (launchState != LaunchState.IDLE) {

                transferDrum.runTransferDrum(0);
                intake.setIntakePower(0);

                launchState = LaunchState.IDLE;
            }

        }
        flywheels.setFlywheelVelocity(targetSpeed+extraOuttakeSpeed);   //send the calculated power to flywheels

    }

    public void updateWithServoKickForAuto(boolean triggerPressed,
                       double distanceToTag,
                       boolean tagVisible) {

        if (triggerPressed) {

            switch (launchState) {

                case IDLE:

                    if (tagVisible) {
                        targetSpeed = flywheels.getVelocityFromDistance(distanceToTag);
                        flywheelStartTime = System.currentTimeMillis();
                        flywheelStartPower = flywheels.getFlywheelVelocity();
                    } else {
                        targetSpeed = maintainOuttakeSpeed;
                        flywheelStartTime = System.currentTimeMillis();
                        flywheelStartPower = flywheels.getFlywheelVelocity();
                    }

                    powerUpTimer.reset();
                    launchTimer.reset();
                    launchState = LaunchState.SPINNING_UP;
                    ballsFed = 0;
                    break;

                case SPINNING_UP:

                    rampUp();
                    if (flywheels.getFlywheelVelocity() >= targetSpeed * 0.9
                            || powerUpTimer.seconds() >= rampUpSpeed + 1.5) {

                        launchTimer.reset();
                        launchState = LaunchState.WAITING_AFTER_SPINUP;
                    }
                    break;

                case WAITING_AFTER_SPINUP:

                    if (launchTimer.milliseconds() > 500) {

                        outtakeSpeedBeforeDrop =
                                flywheels.getFlywheelVelocity();

                        maintainOuttakeSpeed =
                                flywheels.getFlywheelVelocity();

                        launchTimer.reset();
                        launchState =
                                LaunchState.FEEDING_BALL;
                    }
                    break;

                case FEEDING_BALL:

                    if (ballsFed > 0) {
                        intake.setIntakePower(1);
                    }

                    //Use servo kick for last artifact
                    if (ballsFed >= 2) {
                        transferKick.setTransferKickUp();
                        intake.setIntakePower(0);
                    }

                    //transferDrum.runTransferDrum(1);

                    if (flywheels.getFlywheelVelocity()
                            < outtakeSpeedBeforeDrop - 100) {

                        transferDrum.runTransferDrum(0);
                        intake.setIntakePower(0);

                        ballsFed++;
                        launchTimer.reset();

                        if (ballsFed < 3) {
                            launchState =
                                    LaunchState.WAITING_BETWEEN_BALLS;
                        } else {
                            launchState = LaunchState.IDLE;
                        }

                    } else if (launchTimer.milliseconds() > 1800) {

                        transferDrum.runTransferDrum(0);
                        intake.setIntakePower(0);
                        launchState = LaunchState.IDLE;
                    }
                    break;

                case WAITING_BETWEEN_BALLS:
                    transferKick.setTransferKickDown();
                    if (launchTimer.milliseconds() > transferCycleDelayMs) {
                        outtakeSpeedBeforeDrop =
                                flywheels.getFlywheelVelocity();

                        maintainOuttakeSpeed =
                                flywheels.getFlywheelVelocity();

                        launchState =
                                LaunchState.FEEDING_BALL;

                        launchTimer.reset();
                    }
                    break;
            }

        } else {
            targetSpeed = maintainOuttakeSpeed;

            if (launchState != LaunchState.IDLE) {

                transferDrum.runTransferDrum(0);
                intake.setIntakePower(0);

                launchState = LaunchState.IDLE;
            }

        }
    }

    //Methods for autonomous
    public void startAutoLaunch(double distanceToTag, boolean tagVisible) {

        if (launchState != LaunchState.IDLE) return;

        if (tagVisible) {
            targetSpeed = flywheels.getVelocityFromDistance(distanceToTag);
        } else {
            targetSpeed = maintainOuttakeSpeed;
        }

        flywheelStartTime = System.currentTimeMillis();
        flywheelStartPower = flywheels.getFlywheelVelocity();

        powerUpTimer.reset();
        launchTimer.reset();

        ballsFed = 0;
        launchState = LaunchState.SPINNING_UP;
    }

    public void updateAuto(double distanceToTag, boolean tagVisible) {

        switch (launchState) {

            case IDLE:
                break;

            case SPINNING_UP:

                rampUp();

                if (flywheels.getFlywheelVelocity() >= targetSpeed * 0.9
                        || powerUpTimer.seconds() >= rampUpSpeed + 1.5) {

                    launchTimer.reset();
                    launchState = LaunchState.WAITING_AFTER_SPINUP;
                }
                break;

            case WAITING_AFTER_SPINUP:

                if (launchTimer.milliseconds() > 500) {

                    outtakeSpeedBeforeDrop = flywheels.getFlywheelVelocity();
                    maintainOuttakeSpeed = flywheels.getFlywheelVelocity();

                    launchTimer.reset();
                    launchState = LaunchState.FEEDING_BALL;
                }
                break;

            case FEEDING_BALL:

                if (ballsFed > 0) {
                    intake.setIntakePower(1);
                }

                if (ballsFed >= 2) {
                    transferKick.setTransferKickUp();
                    intake.setIntakePower(0);
                }

                if (flywheels.getFlywheelVelocity()
                        < outtakeSpeedBeforeDrop - 100) {

                    transferDrum.runTransferDrum(0);
                    intake.setIntakePower(0);

                    ballsFed++;
                    launchTimer.reset();

                    if (ballsFed < 3) {
                        launchState = LaunchState.WAITING_BETWEEN_BALLS;
                    } else {
                        launchState = LaunchState.IDLE;
                    }

                } else if (launchTimer.milliseconds() > 1800) {

                    transferDrum.runTransferDrum(0);
                    intake.setIntakePower(0);
                    launchState = LaunchState.IDLE;
                }
                break;

            case WAITING_BETWEEN_BALLS:

                transferKick.setTransferKickDown();

                if (launchTimer.milliseconds() > 1000) {

                    outtakeSpeedBeforeDrop = flywheels.getFlywheelVelocity();
                    maintainOuttakeSpeed = flywheels.getFlywheelVelocity();

                    launchState = LaunchState.FEEDING_BALL;
                    launchTimer.reset();
                }
                break;
        }
    }
}
