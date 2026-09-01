package org.firstinspires.ftc.teamcode.DriveCode.ActionBasedCode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class Hardware {
    public DcMotor rightWheel,leftWheel,armMotor;
    public Servo rightClaw, leftClaw;
    public Hardware(HardwareMap hardwareMap){

        rightWheel = hardwareMap.get(DcMotor.class, "rightDrive");
        leftWheel = hardwareMap.get(DcMotor.class, "leftDrive");
        armMotor = hardwareMap.get(DcMotor.class, "armMotor");

        rightClaw = hardwareMap.get(Servo.class, "rightClaw");
        leftClaw = hardwareMap.get(Servo.class, "leftClaw");

        leftWheel.setDirection(DcMotor.Direction.REVERSE);
    }

    public void drive(double joystick){
        leftWheel.setPower(joystick);
        rightWheel.setPower(joystick);
    }

    public void rotate(double joystick){
        rightWheel.setPower(-joystick);
        leftWheel.setPower(joystick);
    }

    public void clawUp(){
        armMotor.setPower(.5);
    }

    public void clawDown(){
        armMotor.setPower(-.5);
    }
    public void clawStop(){
        armMotor.setPower(0);
    }
}
