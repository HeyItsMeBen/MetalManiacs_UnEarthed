package org.firstinspires.ftc.teamcode.Old_Code;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class Transfer {

    private Servo intakeHinge = null;
    private Servo outtakeHinge = null;

    public float intakeHingeLiftPosition = 0.67f; //Pushes ball into flywheel holder

    public float intakeHingeStandbyPosition = 0.32f; //Tucks hinge into wheels

    public float outtakeHingeFirePosition = 1.03f; //Pushes ball into flywheels

    public float outtakeHingeRelaxPosition = 0.51f; //Brings hinge down below ramp

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

    public double getCurrentHingePosition (String servoName) {
        if (servoName.contains("intake") || servoName.contains("Intake")) {
            return intakeHinge.getPosition();
        } else if (servoName.contains("outtake") || servoName.contains("Outtake")) {
            return outtakeHinge.getPosition();
        }
        return 100000;
    }

    public void changeHingePosition (String servoName, double increment) {
        if (servoName.contains("intake") || servoName.contains("Intake")) {
            double position = intakeHinge.getPosition();
            position += increment;
            intakeHinge.setPosition(position);
        } else if (servoName.contains("outtake") || servoName.contains("Outtake")) {
            double position = outtakeHinge.getPosition();
            position += increment;
            outtakeHinge.setPosition(position + increment);
        }
    }
}