package org.firstinspires.ftc.teamcode.DriveCode.DriveCodeClasses;

import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Hardware.Intake;
import org.firstinspires.ftc.teamcode.Hardware.Transfer;

public class IntakeController {

    private Intake intake;
    private Transfer transfer;

    private float intakePower = 0;

    private ElapsedTime intakeTimer = new ElapsedTime();

    public IntakeController(Intake intake, Transfer transfer) {
        this.intake = intake;
        this.transfer = transfer;
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
            transfer.runKickWheels(0);

        } else {
            intakePower = 1;
            intakeTimer.reset();
        }
    }

    public void toggleReverse() {

        if (Math.abs(intakePower) == 1) {
            intakePower = 0;
            transfer.runKickWheels(0);

        } else {
            intakePower = -1;
            transfer.runKickWheels(-1);
        }
    }

    public void update() {

        // Jam detection / auto slow-down
        if (intakePower == 1 &&
            intakeTimer.milliseconds() > 1000 &&
            intake.getVelocityRPM() < 500) {

            intakePower = 0.25f;
        }

        intake.setIntakePower(intakePower);
    }

    public void stopAll() {
        intakePower = 0;
        intake.setIntakePower(0);
        transfer.runKickWheels(0);
    }
}
