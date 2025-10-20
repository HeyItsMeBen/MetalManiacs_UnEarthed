package org.firstinspires.ftc.teamcode.Systems;

import com.arcrobotics.ftclib.controller.PIDController;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Arm {
    private PIDController controller;
    public static double p = 0.001, i = 0, d = 0;
    public static double f = 0;
    private final double ticks_in_degree = 1120 / 360;
    private DcMotorEx arm_motor;

    public Arm(HardwareMap hMap) {
        controller = new PIDController(p, i, d);

        arm_motor = hMap.get(DcMotorEx.class, "arm");   //real name?
        arm_motor.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
        arm_motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        arm_motor.setDirection(DcMotor.Direction.REVERSE);
        arm_motor.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
    }

    public void raiseArmManual(double power) {
        arm_motor.setPower(power);
    }

    public void setArmTarget(double givenTarget) {
        double target=givenTarget*-325;
        controller.setPID(p, i, d);
        int armPos = arm_motor.getCurrentPosition();
        double pid = controller.calculate(armPos, givenTarget);
        double ff = Math.cos(Math.toRadians(givenTarget / ticks_in_degree)) * f;

        double power = (pid + ff) *4;

        arm_motor.setPower(power);
    }
    public void stopMotor(){
        arm_motor.setPower(0);
    }
}
