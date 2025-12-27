package org.firstinspires.ftc.teamcode.Hardware;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class TransferBelt {

    private Servo trapdoorServo = null;
    private DcMotor conveyorbeltMotor = null;

    public float openTrapdoorPosition = 0.67f; // Get New Values

    public float closeTrapdoorPosition = 0.32f; // Get New Values

    public TransferBelt(HardwareMap hMap) {
        trapdoorServo = hMap.get(Servo.class, "trapdoor");
        conveyorbeltMotor = hMap.get(DcMotor.class, "conveyorBelt");
    }

    public void trapdoorServoOpen() {
        trapdoorServo.setPosition(openTrapdoorPosition);
    }

    public void trapdoorServoClose() {
        trapdoorServo.setPosition(closeTrapdoorPosition);
    }

    public double getCurrentHingePosition () {
        return trapdoorServo.getPosition();
    }

    public void changeTrapdoorPosition (double increment) {
        double position = trapdoorServo.getPosition();
        position += increment;
        trapdoorServo.setPosition(position);
    }

    public void runConveyorMotor() {
        conveyorbeltMotor.setPower(1.0);
    }

    public void maintainConveyorMotor() {
        conveyorbeltMotor.setPower(0.5);
    }

    public void stopConveyorMotor() {
        conveyorbeltMotor.setPower(0);
    }

    public void reverseConveyorMotor() {
        conveyorbeltMotor.setPower(-1.0);
    }

}