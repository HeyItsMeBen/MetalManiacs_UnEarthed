package org.firstinspires.ftc.teamcode.DriveCode.ExperimentalDriveCode;

import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Hardware.Flywheels;
import org.firstinspires.ftc.teamcode.Hardware.Transfer;
import org.firstinspires.ftc.teamcode.Hardware.Intake;

public class FlywheelController {

    private Flywheels flywheels;
    private Transfer transfer;
    private Intake intake;

    private ElapsedTime launchTimer = new ElapsedTime();

    private double maintainOuttakeSpeed = 1386;
    private double extraOuttakeSpeed = 0;

    private int ballsFed = 0;

    private double targetSpeed = 0;
    private double outtakeSpeedBeforeDrop = 0;

    public enum LaunchState {
        IDLE,
        SPINNING_UP,
        WAITING_AFTER_SPINUP,
        FEEDING_BALL,
        WAITING_BETWEEN_BALLS
    }

    private LaunchState launchState = LaunchState.IDLE;

    public FlywheelController(Flywheels flywheels,
                              Transfer transfer,
                              Intake intake) {

        this.flywheels = flywheels;
        this.transfer = transfer;
        this.intake = intake;
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

    public void update(boolean triggerPressed,
                       double distanceToTag,
                       boolean tagVisible) {

        if (triggerPressed) {

            switch (launchState) {

                case IDLE:

                    if (tagVisible) {
                        targetSpeed =
                                flywheels.launchFromDistance(
                                        distanceToTag,
                                        extraOuttakeSpeed
                                );
                    } else {
                        targetSpeed = maintainOuttakeSpeed;
                        flywheels.setFlywheelSpeedRaw(
                                maintainOuttakeSpeed
                        );
                    }

                    launchTimer.reset();
                    launchState = LaunchState.SPINNING_UP;
                    ballsFed = 0;
                    break;

                case SPINNING_UP:

                    if (flywheels.getFlywheelSpeedRaw()
                            >= targetSpeed * 0.9) {

                        launchTimer.reset();
                        launchState =
                                LaunchState.WAITING_AFTER_SPINUP;

                    } else if (launchTimer.milliseconds() > 1200) {

                        launchTimer.reset();
                        launchState =
                                LaunchState.WAITING_AFTER_SPINUP;
                    }
                    break;

                case WAITING_AFTER_SPINUP:

                    if (launchTimer.milliseconds() > 500) {

                        outtakeSpeedBeforeDrop =
                                flywheels.getFlywheelRPM();

                        maintainOuttakeSpeed =
                                flywheels.getFlywheelSpeedRaw();

                        launchTimer.reset();
                        launchState =
                                LaunchState.FEEDING_BALL;
                    }
                    break;

                case FEEDING_BALL:

                    if (ballsFed > 0) {
                        intake.setIntakePower(1);
                    }

                    transfer.setTransferPower(1);

                    if (flywheels.getFlywheelRPM()
                            < outtakeSpeedBeforeDrop - 100) {

                        transfer.setTransferPower(0);
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

                        transfer.setTransferPower(0);
                        intake.setIntakePower(0);
                        launchState = LaunchState.IDLE;
                    }
                    break;

                case WAITING_BETWEEN_BALLS:

                    if (launchTimer.milliseconds() > 1000) {

                        outtakeSpeedBeforeDrop =
                                flywheels.getFlywheelRPM();

                        maintainOuttakeSpeed =
                                flywheels.getFlywheelSpeedRaw();

                        launchState =
                                LaunchState.FEEDING_BALL;

                        launchTimer.reset();
                    }
                    break;
            }

        } else {

            if (launchState != LaunchState.IDLE) {

                transfer.setTransferPower(0);
                intake.setIntakePower(0);

                launchState = LaunchState.IDLE;
            }

            flywheels.setFlywheelSpeedRaw(maintainOuttakeSpeed);
        }
    }
}
