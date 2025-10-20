package org.firstinspires.ftc.teamcode.Systems;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class Outtake {

    public DcMotorEx rightFlyWheel = null;
    public DcMotorEx leftFlyWheel = null;
    final double tickPerRevolution=28;

    //Outtake subsystem
    public Outtake(HardwareMap hMap) {
        rightFlyWheel = hMap.get(DcMotorEx.class, "rightFlyWheel");
        leftFlyWheel = hMap.get(DcMotorEx.class, "leftFlyWheel");

        rightFlyWheel.setDirection(DcMotorEx.Direction.REVERSE);
        leftFlyWheel.setDirection(DcMotorEx.Direction.FORWARD);
    }

    public void fire(float givenRPM) {  //givenRPM should be around 2900 or 3000.
        rightFlyWheel.setVelocity(tickPerRevolution*(givenRPM/60));
        leftFlyWheel.setVelocity(tickPerRevolution*(givenRPM/60));
    }
}