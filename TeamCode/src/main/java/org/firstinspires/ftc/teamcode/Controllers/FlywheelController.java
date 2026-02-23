package org.firstinspires.ftc.teamcode.Controllers;

import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Hardware.Flywheels;
import org.firstinspires.ftc.teamcode.Hardware.OuttakeHood;
import org.firstinspires.ftc.teamcode.Hardware.Transfer;
import org.firstinspires.ftc.teamcode.Hardware.Intake;

public class FlywheelController {

    private Flywheels flywheels;
    private Transfer transferDrum;
    private Transfer transferKick;
    private Intake intake;
    private OuttakeHood hood;

    private ElapsedTime launchTimer = new ElapsedTime();

    private ElapsedTime powerUpTimer = new ElapsedTime();

    private double maintainOuttakeSpeed = 1386;
    private double extraOuttakeSpeed = 0;

    private int ballsFed = 0;

    private double targetSpeed = 0;
    private double outtakeSpeedBeforeDrop = 0;

    private float rampUpSpeed = 2;
    double currentTime = 0;
    double flywheelStartTime=0;
    double flywheelStartPower=0;

    public enum LaunchState {
        IDLE,
        SPINNING_UP,
        WAITING_AFTER_SPINUP,
        FEEDING_BALL,
        WAITING_BETWEEN_BALLS
    }

    private LaunchState launchState = LaunchState.IDLE;

    public FlywheelController(Flywheels flywheels,
                              Transfer transferDrum, Transfer transferKick,
                              Intake intake, OuttakeHood hood) {

        this.flywheels = flywheels;
        this.transferDrum = transferDrum;
        this.transferKick = transferKick;
        this.intake = intake;
        this.hood = hood;
    }

    public LaunchState getState() {
        return launchState;
    }

    public double getTargetSpeed() {
        return targetSpeed;
    }

    public double getMaintainSpeed() {
        return maintainOuttakeSpeed;
    }

    public void setExtraSpeed(double extra) {
        extraOuttakeSpeed = extra;
    }

    public void rampUp() {

        double targetSeconds = rampUpSpeed *1000; // convert rampUpSpeed to milliseconds
        currentTime=System.currentTimeMillis()-flywheelStartTime;
        if (currentTime<targetSeconds){
            flywheels.setFlywheelVelocity(flywheelStartPower*((targetSeconds-currentTime)/targetSeconds)+targetSpeed*(currentTime/targetSeconds));
        } else {
            flywheels.setFlywheelVelocity(targetSpeed);
        }

    }

    public void update(boolean triggerPressed,
                       double distanceToTag,
                       boolean tagVisible) {

        if (triggerPressed) {

            switch (launchState) {

                case IDLE:

                    if (tagVisible) {
                        targetSpeed = flywheels.getVelocityFromDistance(distanceToTag);
                        flywheelStartTime=System.currentTimeMillis();
                        flywheelStartPower= flywheels.getFlywheelVelocity();
                    } else {
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

                    transferDrum.runTransferDrum(1);

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

                    if (launchTimer.milliseconds() > 1000) {

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

            if (launchState != LaunchState.IDLE) {

                transferDrum.runTransferDrum(0);
                intake.setIntakePower(0);

                launchState = LaunchState.IDLE;
            }

        }
    }
}
