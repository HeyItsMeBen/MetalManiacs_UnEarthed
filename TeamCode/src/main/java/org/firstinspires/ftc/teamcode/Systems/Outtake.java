package org.firstinspires.ftc.teamcode.Systems;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

public class Outtake {

    private DcMotorEx leftFlyWheel = null;
    private DcMotorEx rightFlyWheel = null;
    final double tickPerRevolution=28;


    public boolean done=true;
    public boolean isOpModeActive=true;

    //Outtake subsystem
    public Outtake(HardwareMap hMap) {

        leftFlyWheel = hMap.get(DcMotorEx.class, "leftFlyWheel");
        rightFlyWheel = hMap.get(DcMotorEx.class, "rightFlyWheel");

        leftFlyWheel.setDirection(DcMotorEx.Direction.FORWARD);
        rightFlyWheel.setDirection(DcMotorEx.Direction.REVERSE);

        leftFlyWheel.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightFlyWheel.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        leftFlyWheel.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightFlyWheel.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

    }

    public void setFlywheelVelocity(float givenRPM) {
        leftFlyWheel.setVelocity(tickPerRevolution*(givenRPM/60));
        rightFlyWheel.setVelocity(tickPerRevolution*(givenRPM/60));
    }
    public double getCurrentWheelVelocity(String motor){
        if (motor.contains("left") || motor.contains("Left")) {
            return leftFlyWheel.getVelocity()*60/tickPerRevolution;
        } else if (motor.contains("right") || motor.contains("Right")) {
            return rightFlyWheel.getVelocity()*60/tickPerRevolution;
        }
        return 0;

    }
}