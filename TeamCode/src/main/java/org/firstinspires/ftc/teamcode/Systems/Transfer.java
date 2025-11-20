package org.firstinspires.ftc.teamcode.Systems;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

@Config

public class Transfer {

    private Servo intakeHinge = null;
    private Servo outtakeHinge = null;

    public float intakeHingeLiftPosition = 0.72f; //Pushes ball into flywheel holder

    public float intakeHingeStandbyPosition = 0.34f; //Tucks hinge into wheels

    public float outtakeHingeFirePosition = 0.67f; //Pushes ball into flywheels

    public float outtakeHingeRelaxPosition = 0.15f; //Brings hinge down below ramp

    public Transfer(HardwareMap hMap) {
        intakeHinge = hMap.get(Servo.class, "intakeHinge");
        outtakeHinge = hMap.get(Servo.class, "outtakeHinge");
    }

    public void intakeHingeLift() {
        intakeHinge.setPosition(intakeHingeLiftPosition);
    }

    public void intakeHingeStandby() {
        intakeHinge.setPosition(intakeHingeStandbyPosition);
    }

    public void outtakeHingeFire() {
        outtakeHinge.setPosition(outtakeHingeFirePosition);
    }

    public void outtakeHingeRelax() {
        outtakeHinge.setPosition(outtakeHingeRelaxPosition);
    }

    public double getCurrentHingePosition (Servo hinge) {
        return hinge.getPosition();
    }
}