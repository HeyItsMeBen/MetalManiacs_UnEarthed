package org.firstinspires.ftc.teamcode.Hardware;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class Transfer {

    private Servo transferKick = null;
    private DcMotor transferDrum = null;

    public float kickServoUp = 0.48f; // New values 2/25
    public float kickServoDown = 0.85f; // New values 2/25

    public Transfer(HardwareMap hMap) {
        transferKick = hMap.get(Servo.class, "transferKick");
        transferDrum = hMap.get(DcMotor.class, "transferDrum");
        transferDrum.setDirection(DcMotor.Direction.FORWARD);
    }

    public void runTransferDrum(double power){
        transferDrum.setPower(power);
    }
    public void stopTransferDrum() {
        transferDrum.setPower(0);
    }

    public void setTransferKickUp() {
        transferKick.setPosition(kickServoUp);
    }

    public void setTransferKickDown() {
        transferKick.setPosition(kickServoDown);
    }

    public double getCurrentTransferKickPosition () {
        return transferKick.getPosition();
    }

    public void changeTransferKickPositionManual (double increment) {
        double position = transferKick.getPosition();
        position += increment;
        transferKick.setPosition(position);
    }

}