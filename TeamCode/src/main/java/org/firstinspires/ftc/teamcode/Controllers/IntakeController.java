package org.firstinspires.ftc.teamcode.Controllers;

import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Hardware.Intake;
import org.firstinspires.ftc.teamcode.Hardware.Transfer;

public class IntakeController {

    private Intake intake;
    private Transfer transferDrum;
    private Transfer transferKick;

    private float intakePower = 0;

    private ElapsedTime intakeTimer = new ElapsedTime();
    double intakeStartTime=0;
    double intakeStartPower=0;

    public IntakeController(Intake intake, Transfer transferDrum, Transfer transferKick) {
        this.intake = intake;
        this.transferDrum = transferDrum;
        this.transferKick = transferKick;
    }

    public boolean isIntakeRunning() {
        return Math.abs(intakePower) > 0.1;
    }

    public float getIntakePower() {
        return intakePower;
    }

    public void toggleIntake() {

        if (Math.abs(intakePower) == 1) {
            intakePower = 0;
            transferDrum.runTransferDrum(0);
        } else {
            intakePower = 1;
            intakeTimer.reset();
            intakeStartTime=System.currentTimeMillis();
            intakeStartPower=intake.getIntakePower();
        }
    }

    public void toggleReverse() {

        if (Math.abs(intakePower) == 1) {
            intakePower = 0;
            transferDrum.runTransferDrum(0);

        } else {
            intakePower = -1;
            //transferDrum.runTransferDrum(-1);
            intakeStartTime=System.currentTimeMillis();
            intakeStartPower=intake.getIntakePower();
        }
    }

    public void update() {

        // Jam detection / auto slow-down
        if (intakePower == 1 &&
            intakeTimer.milliseconds() > 1000 &&
            intake.getIntakeMotorRPM() < 500) {

            intakePower = 0.25f;
            intakeStartTime=System.currentTimeMillis();
            intakeStartPower=intake.getIntakePower();
        }

        //intake.setIntakePower(intakePower*(System.currentTimeMillis()/0.5));    //should take 0.5 seconds to speed up.
        double targetSeconds=0.5-0.5*(intakeStartPower/intakePower);
        double currentTime=System.currentTimeMillis()-intakeStartTime;
        if (currentTime<targetSeconds){
            intake.setIntakePower(intakeStartPower*((targetSeconds-currentTime)/targetSeconds)+intakePower*(currentTime/targetSeconds));
        } else {
            intake.setIntakePower(intakePower);
        }
    }

    public void stopAll() {
        intakePower = 0;
        intake.setIntakePower(0);
        transferDrum.runTransferDrum(0);
    }

}
