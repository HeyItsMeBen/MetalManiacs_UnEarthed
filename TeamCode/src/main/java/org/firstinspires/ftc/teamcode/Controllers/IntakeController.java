package org.firstinspires.ftc.teamcode.Controllers;

import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Hardware.Intake;
import org.firstinspires.ftc.teamcode.Hardware.Transfer;

public class IntakeController {

    private float targetSpeed = 0.9f; // determines how fast the intake should run
    private float rampUpSpeed = 1; // how fast intake should ramp up to target speed (in seconds)
    private Intake intake;
    private Transfer transferDrum;
    private Transfer transferKick;

    private float intakePower = 0;
    private float transferPower = 0;

    private ElapsedTime intakeTimer = new ElapsedTime();
    double intakeStartTime=0;
    double intakeStartPower=0;
    double currentTime = 0;

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

    public void toggleIntake() {

        if (Math.abs(intakePower) >= targetSpeed) {
            intakePower = 0;
            transferPower = 0;
            intakeStartTime=System.currentTimeMillis();
            intakeStartPower=intake.getIntakePower();
//            transferDrum.runTransferDrum(0);
        } else {
            intakePower = targetSpeed;
            transferPower = targetSpeed;

            intakeTimer.reset();
            intakeStartTime=System.currentTimeMillis();
            intakeStartPower=intake.getIntakePower();
        }
    }

    public void toggleReverse() {

        if (Math.abs(intakePower) >= targetSpeed) {
            intakePower = 0;
            transferPower = 0;
            intakeStartTime=System.currentTimeMillis();
            intakeStartPower=intake.getIntakePower();
//            transferDrum.runTransferDrum(0);

        } else {
            intakePower = -targetSpeed;
            transferPower = -targetSpeed;
            //transferDrum.runTransferDrum(-1);
            intakeStartTime=System.currentTimeMillis();
            intakeStartPower=intake.getIntakePower();
        }
    }

    public double getCurrentTime(){
        return currentTime;
    }

    public void update() {

        // Jam detection / auto slow-down
        if (intakePower >= targetSpeed &&
            intakeTimer.milliseconds() > 5000 &&
            intake.getIntakeMotorRPM() < 750) {

            intakePower = 0.25f;
            intakeStartTime=System.currentTimeMillis();
            intakeStartPower=intake.getIntakePower();
        }

        //intake.setIntakePower(intakePower*(System.currentTimeMillis()/0.5));    //should take 0.5 seconds to speed up.
//        double targetSeconds=rampUpSpeed-rampUpSpeed*(intakeStartPower/intakePower); //should take 0.5 seconds to speed up

        //   V poorly named but too lazy to change (is in milliseconds)
        double targetSeconds = rampUpSpeed *1000; // convert rampUpSpeed to milliseconds
        currentTime=System.currentTimeMillis()-intakeStartTime;
        if (currentTime<targetSeconds){
            intake.setIntakePower(intakeStartPower*((targetSeconds-currentTime)/targetSeconds)+intakePower*(currentTime/targetSeconds));
            transferDrum.runTransferDrum(intakeStartPower*((targetSeconds-currentTime)/targetSeconds)+intakePower*(currentTime/targetSeconds));
        } else {
            intake.setIntakePower(intakePower);
            transferDrum.runTransferDrum(intakePower);
        }
    }

    public void stopAll() {
        intakePower = 0;
        intake.setIntakePower(0);
        transferDrum.runTransferDrum(0);
    }

    public void runPower() {
        intake.setIntakePower(0.5);
        transferDrum.runTransferDrum(0.5);
    }

}
