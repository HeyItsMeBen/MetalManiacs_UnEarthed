package org.firstinspires.ftc.teamcode.Systems;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

public class Outtake {

    Arm arm;
    private DcMotorEx rightFlyWheel = null;
    private DcMotorEx leftFlyWheel = null;
    final double tickPerRevolution=28;

    public boolean done=true;

    //Outtake subsystem
    public Outtake(HardwareMap hMap) {
        rightFlyWheel = hMap.get(DcMotorEx.class, "rightFlyWheel");
        leftFlyWheel = hMap.get(DcMotorEx.class, "leftFlyWheel");

        rightFlyWheel.setDirection(DcMotorEx.Direction.REVERSE);
        leftFlyWheel.setDirection(DcMotorEx.Direction.FORWARD);
        arm = new Arm(hMap);
    }

    public void setFlywheelVelocity(float givenRPM, double expectedWaitTime, double armTarget) {
        rightFlyWheel.setVelocity(tickPerRevolution*(givenRPM/60));
        leftFlyWheel.setVelocity(tickPerRevolution*(givenRPM/60));
        ElapsedTime timer;
        timer = new ElapsedTime();
        while (timer.milliseconds()/1000<expectedWaitTime){
            arm.raiseArmManual(arm.setArmTarget(armTarget));    //allows arm to hold it's position
        }
    }
    public void setFlywheelVelocity(float givenRPM, double expectedWaitTime) {  //givenRPM should be around 2900 or 3000.
        rightFlyWheel.setVelocity(tickPerRevolution*(givenRPM/60));
        leftFlyWheel.setVelocity(tickPerRevolution*(givenRPM/60));
        ElapsedTime timer;
        timer = new ElapsedTime();
        while (timer.milliseconds()/1000<expectedWaitTime){}
    }
    public double getCurrentWheelRPM(){
        return rightFlyWheel.getVelocity()*60/tickPerRevolution;
    }
}