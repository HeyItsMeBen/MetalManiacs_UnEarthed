package org.firstinspires.ftc.teamcode.Testing;

import com.acmerobotics.dashboard.config.Config;
import com.arcrobotics.ftclib.controller.PIDController;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

@Config
public class Intake {

    public DcMotor arm = null;
    private Servo armServo = null;
    private Servo armPivotServo = null;

    private PIDController armController;
    public static double p = 0.01, i = 0.05, d = 0.001;
    public static double f = 0;

    public static int armtarget = 0;

    public double pivotTarget = 0.5;




    public Intake(HardwareMap hwMap){
        arm = hwMap.get(DcMotor.class, "arm");
        armServo = hwMap.get(Servo.class, "intakeClawServo");
        armPivotServo = hwMap.get(Servo.class, "intakePivotServo");

        arm.setDirection(DcMotorSimple.Direction.REVERSE);
        arm.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        arm.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        arm.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        armController = new PIDController(p, i, d);
    }


    public void armRetract(int armtarget) {
        armController.setPID(p, i, d);
        double ticks_in_degree = 1120 / 360;
        int armPos = arm.getCurrentPosition();
        double armPID = armController.calculate(armPos, armtarget);
        double armFF = Math.cos(Math.toRadians(armtarget / ticks_in_degree)) * f;

        double armpower = armPID + armFF;

        arm.setPower(armpower * 0.5);//0.75-->0.5
    }

    public void armServoOpen(double pos){
        armServo.setPosition(pos);
    }

    public void armServoClose(){
        armServo.setPosition(0.05);
    }

    public void setArmPivotServoOut(){
        pivotTarget += 0.15;
        armPivotServo.setPosition(pivotTarget);
    }

    public void setArmPivotServoBack(){
        armPivotServo.setPosition(0.5);
    }
}
