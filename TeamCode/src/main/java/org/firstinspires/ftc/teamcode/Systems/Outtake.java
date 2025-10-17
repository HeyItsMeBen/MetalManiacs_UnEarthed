package org.firstinspires.ftc.teamcode.Systems;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

@Config

public class Outtake {

    public DcMotor rightFlyWheel = null;
    public DcMotor leftFlyWheel = null;
    public Servo hinge = null;
    //Outtake subsystem
    public Outtake(HardwareMap hMap) {
        rightFlyWheel = hMap.get(DcMotor.class, "rightFlyWheel");
        leftFlyWheel = hMap.get(DcMotor.class, "leftFlyWheel");

        //add hinge mapping when done
//        hinge = hardwareMap.get(Servo.class, "hinge");

        rightFlyWheel.setDirection(DcMotor.Direction.REVERSE);
        leftFlyWheel.setDirection(DcMotor.Direction.FORWARD);
    }

    public void fire(float power) {

        //eventually we need to detect if the flywheel is fully accelerated then fire
        rightFlyWheel.setPower(power);
        leftFlyWheel.setPower(power);

    }
}