package org.firstinspires.ftc.teamcode.Systems;

import com.arcrobotics.ftclib.controller.PIDController;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

public class Arm {
    private PIDController controller;
    //public static double p = 0.001, i = 0, d = 0;
    public static double p = 0.0015, i = 0.1, d = 0.000001;
    //public double chosenArmTarget=0;

    public static double f = 0;
    private final double ticks_in_degree = 1120 / 360;
    private DcMotorEx arm_motor;

    public Arm(HardwareMap hMap) {
        controller = new PIDController(p, i, d);

        arm_motor = hMap.get(DcMotorEx.class, "arm");   //real name?
        /*arm_motor.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
        arm_motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        arm_motor.setDirection(DcMotor.Direction.FORWARD);
        arm_motor.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);*/
    }

    public void raiseArmManual(double power) {
        arm_motor.setPower(power);
    }

//    public void moveArmTo(double givenTarget, double expectedWaitTime) {  //Mostly works. But it can cause loops to run after opMode has stopped. So, it's best not to use
//        ElapsedTime timer2;
//        timer2 = new ElapsedTime();
//        while (timer2.milliseconds()/1000<expectedWaitTime){
//            arm_motor.setPower(setArmTarget(givenTarget));
//        }
//    }
    public double setArmTarget(double givenTarget) {
        double target=givenTarget*-325;
        controller.setPID(p, i, d);
        int armPos = arm_motor.getCurrentPosition();
        double pid = controller.calculate(armPos, givenTarget);
        double ff = Math.cos(Math.toRadians(givenTarget / ticks_in_degree)) * f;

        double power = pid + ff;

        return -power;
    }
    public void stopMotor(){
        arm_motor.setPower(0);
    }
    public void resetArmEncoders(){
        arm_motor.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
        arm_motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        arm_motor.setDirection(DcMotor.Direction.FORWARD);
        arm_motor.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
    }
    public double getArmPosition(){
        return arm_motor.getCurrentPosition();
    }
}
