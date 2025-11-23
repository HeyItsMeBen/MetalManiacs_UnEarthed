package org.firstinspires.ftc.teamcode.Systems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Flywheels {

    private DcMotorEx leftFlyWheel = null;
    private DcMotorEx rightFlyWheel = null;
    final double tickPerRevolution = 28;

    double optimalVelocity = 2350;

    public boolean done = true;
    public boolean isOpModeActive = true;

    //Outtake subsystem
    public Flywheels(HardwareMap hMap) {

        leftFlyWheel = hMap.get(DcMotorEx.class, "leftFlyWheel");
        rightFlyWheel = hMap.get(DcMotorEx.class, "rightFlyWheel");

        leftFlyWheel.setDirection(DcMotorEx.Direction.FORWARD);
        rightFlyWheel.setDirection(DcMotorEx.Direction.REVERSE);

        leftFlyWheel.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightFlyWheel.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        leftFlyWheel.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightFlyWheel.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

    }

    public void setFlywheelPower(double power) {
        if (power > 1) {
            leftFlyWheel.setPower(1.0);
            rightFlyWheel.setPower(1.0);
        } else {
            leftFlyWheel.setPower(power);
            rightFlyWheel.setPower(power);
        }
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
    public double getCurrentWheelRawVelocity(String motor){
        if (motor.contains("left") || motor.contains("Left")) {
            return leftFlyWheel.getVelocity();
        } else if (motor.contains("right") || motor.contains("Right")) {
            return rightFlyWheel.getVelocity();
        }
        return 0;
    }

    public void runOptimalFlywheelVelocity() {
        leftFlyWheel.setVelocity(tickPerRevolution*(optimalVelocity/60));
        rightFlyWheel.setVelocity(tickPerRevolution*(optimalVelocity/60));
    }

    public double returnOptimalFlywheelVelocity() {
        return optimalVelocity;
    }

    public void setFlywheelRawVelocity(float givenRPM) {
        leftFlyWheel.setVelocity(givenRPM);
        rightFlyWheel.setVelocity(givenRPM);
    }
}