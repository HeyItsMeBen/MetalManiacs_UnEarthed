package org.firstinspires.ftc.teamcode.Controllers;

import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Hardware.Intake;
import org.firstinspires.ftc.teamcode.Hardware.Transfer;

public class IntakeController {

    private Intake intake;
    private Transfer kickWheel;
    private Transfer kickServo;

    private float intakePower = 0;

    private ElapsedTime intakeTimer = new ElapsedTime();

    public IntakeController(Intake intake, Transfer kickWheel, Transfer kickServo) {
        this.intake = intake;
        this.kickWheel = kickWheel;
        this.kickServo = kickServo;
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
            kickWheel.runKickWheels(0);

        } else {
            intakePower = 1;
            intakeTimer.reset();
        }
    }

    public void toggleReverse() {

        if (Math.abs(intakePower) == 1) {
            intakePower = 0;
            kickWheel.runKickWheels(0);

        } else {
            intakePower = -1;
            kickWheel.runKickWheels(-1);
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
        kickWheel.runKickWheels(0);
    }

    public void setIntakePower(double power) {
        intake.setIntakePower(power);
    }
    public void setKickWheelPower(double power) {
        intake.setIntakePower(power);
    }

}
