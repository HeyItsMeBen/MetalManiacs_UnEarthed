package org.firstinspires.ftc.teamcode.Hardware;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class Transfer {

    private Servo kickServo = null;
    private DcMotor kickWheels = null;
//0.41(open), 0.2 (close)
    public float kickServoUp = 0.41f; //0.24

    public float kickServoDown = 0.2f; // Get New Values

    public Transfer(HardwareMap hMap) {
        kickServo = hMap.get(Servo.class, "kickServo");
        kickWheels = hMap.get(DcMotor.class, "kickWheels");
        kickWheels.setDirection(DcMotorEx.Direction.REVERSE);
    }

    public void runKickWheels(double power){
        kickWheels.setPower(power);
    }
    public void stopKickWheels() {
        kickWheels.setPower(0);
    }

    public void setKickServoUp() {
        kickServo.setPosition(kickServoUp);
    }

    public void setKickServoDown() {
        kickServo.setPosition(kickServoDown);
    }

    public double getCurrentKickServoPosition () {
        return kickServo.getPosition();
    }

    public void changeKickServoPositionManual (double increment) {
        double position = kickServo.getPosition();
        position += increment;
        kickServo.setPosition(position);
    }

}