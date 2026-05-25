package org.firstinspires.ftc.teamcode.Hardware;

import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;

public class Transfer {

    private Servo transferKick;
    private DcMotorEx transferDrum;

    final double tickPerRevolution = 28 * 5.2; // ticks per revolution of the transfer drum motor

    public float kickServoUp = 0.38f; // New values 2/26
    public float kickServoDown = 0.85f; // New values 2/25

    public Transfer(HardwareMap hMap) {
        transferKick = hMap.get(Servo.class, "transferKick");
        transferDrum = hMap.get(DcMotorEx.class, "transferDrum");
        transferDrum.setDirection(DcMotorEx.Direction.FORWARD);
    }

    public void runTransferDrum(double power){
        transferDrum.setPower(power);
    }
    public void stopTransferDrum() {
        transferDrum.setPower(0);
    }

    public double getTransferDrumRPM() {
        return transferDrum.getVelocity() * 60 / tickPerRevolution;
    }

    public double getTransferDrumAmps() {
        return transferDrum.getCurrent(CurrentUnit.AMPS);
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