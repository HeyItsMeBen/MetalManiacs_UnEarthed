package org.firstinspires.ftc.teamcode.Hardware;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class Transfer {

    private Servo trapdoor = null;
    private DcMotor transfer = null;
//0.41(open), 0.2 (close)
    public float openTrapdoorPosition = 0.41f; //0.24

    public float closeTrapdoorPosition = 0.2f; // Get New Values

    public Transfer(HardwareMap hMap) {
        trapdoor = hMap.get(Servo.class, "trapdoor");
        transfer = hMap.get(DcMotor.class, "transfer");
        transfer.setDirection(DcMotorEx.Direction.REVERSE);
    }

    public void setTransferPower(double power){
        transfer.setPower(power);
    }
    public void runTransfer() {
        transfer.setPower(1.0);
    }

    public void stopTransfer() {
        transfer.setPower(0);
    }



    @Deprecated
    public void trapdoorOpen() {
        trapdoor.setPosition(openTrapdoorPosition);
    }

    @Deprecated
    public void trapdoorClose() {
        trapdoor.setPosition(closeTrapdoorPosition);
    }

    @Deprecated
    public double getCurrentHingePosition () {
        return trapdoor.getPosition();
    }

    @Deprecated
    public void changeTrapdoorPosition (double increment) {
        double position = trapdoor.getPosition();
        position += increment;
        trapdoor.setPosition(position);
    }

    @Deprecated
    public void maintainConveyorMotor() {
        transfer.setPower(0.5);
    }

    @Deprecated
    public void stopBelt() {
        transfer.setPower(0);
    }

    @Deprecated
    public void runBelt() {
        transfer.setPower(1);
    }

    @Deprecated
    public void reverseBelt() {
        transfer.setPower(-1.0);
    }

    @Deprecated
    public void changeHingePosition(double v) {
        trapdoor.setPosition(trapdoor.getPosition()+v);
    }
}