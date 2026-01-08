package org.firstinspires.ftc.teamcode.Hardware;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class Transfer {

    private Servo trapdoor = null;
    private DcMotor belt = null;

    public float openTrapdoorPosition = 0.5f; // Get New Values

    public float closeTrapdoorPosition = 0.15f; // Get New Values

    public Transfer(HardwareMap hMap) {
        trapdoor = hMap.get(Servo.class, "trapdoor");
        belt = hMap.get(DcMotor.class, "transfer");
    }

    public void trapdoorOpen() {
        trapdoor.setPosition(openTrapdoorPosition);
    }

    public void trapdoorClose() {
        trapdoor.setPosition(closeTrapdoorPosition);
    }

    public double getCurrentHingePosition () {
        return trapdoor.getPosition();
    }

    public void changeTrapdoorPosition (double increment) {
        double position = trapdoor.getPosition();
        position += increment;
        trapdoor.setPosition(position);
    }

    public void setTransferPower(double power){
        belt.setPower(power);
    }
    public void runTransfer() {
        belt.setPower(1.0);
    }

    public void maintainConveyorMotor() {
        belt.setPower(0.5);
    }

    public void stopBelt() {
        belt.setPower(0);
    }

    public void reverseBelt() {
        belt.setPower(-1.0);
    }

    public void changeHingePosition(double v) {
        trapdoor.setPosition(trapdoor.getPosition()+v);
    }
}