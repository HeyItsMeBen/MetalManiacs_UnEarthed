package org.firstinspires.ftc.teamcode.Hardware;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;

public class Flywheels {

    private DcMotorEx flywheel = null;
    //    private DcMotorEx leftFlyWheel = null;
//    private DcMotorEx rightFlyWheel = null;
    final double tickPerRevolution = 28;

    double optimalVelocity = 2350;

    public boolean done = true;
    public boolean isOpModeActive = true;

    public double f = 14.12;
    public double p = 100;

    //Outtake subsystem
    public Flywheels(HardwareMap hMap) {

        flywheel = hMap.get(DcMotorEx.class, "flywheel");
        flywheel.setDirection(DcMotorEx.Direction.REVERSE);
        flywheel.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
        flywheel.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);

        PIDFCoefficients pidfCoefficients = new PIDFCoefficients(p, 0, 0, f);
        flywheel.setPIDFCoefficients(DcMotorEx.RunMode.RUN_USING_ENCODER, pidfCoefficients);

//        leftFlyWheel = hMap.get(DcMotorEx.class, "leftFlyWheel");
//        rightFlyWheel = hMap.get(DcMotorEx.class, "rightFlyWheel");
//
//        leftFlyWheel.setDirection(DcMotorEx.Direction.FORWARD);
//        rightFlyWheel.setDirection(DcMotorEx.Direction.REVERSE);
//
//        leftFlyWheel.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
//        rightFlyWheel.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
//
//        leftFlyWheel.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
//        rightFlyWheel.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

    }

    public void setFlywheelSpeed(double rpm) {
        PIDFCoefficients pidfCoefficients = new PIDFCoefficients(p, 0, 0, f);
        flywheel.setPIDFCoefficients(DcMotorEx.RunMode.RUN_USING_ENCODER, pidfCoefficients);
        flywheel.setVelocity(rpm);
    }

    public double launchFromDistance(double distance) { //distance in feet
        double optimalRpm = 1000 + (83.3 * distance);
        setFlywheelSpeed(optimalRpm);
        return optimalRpm; //return the optimal rpm for telemetry debugging...
    }

    //    public void setFlywheelPower(double power) {
//        if (power > 1) {
//            flywheel.setPower(1.0);
//        } else {
//            flywheel.setPower(power);
//        }
//    }
    public void stopFlywheel() {
        flywheel.setPower(0);
    }

    public void setRawFlywheelVelocity(float givenRPM) {
        flywheel.setVelocity(givenRPM);
    }

    public void setFlywheelVelocity(float givenRPM) {
        flywheel.setVelocity(tickPerRevolution * (givenRPM / 60));
//        leftFlyWheel.setVelocity(tickPerRevolution*(givenRPM/60));
//        rightFlyWheel.setVelocity(tickPerRevolution*(givenRPM/60));
    }

    public double getCurrentWheelVelocity(String motor) {
//        if (motor.contains("left") || motor.contains("Left")) {
//            return leftFlyWheel.getVelocity()*60/tickPerRevolution;
//        } else if (motor.contains("right") || motor.contains("Right")) {
//            return rightFlyWheel.getVelocity()*60/tickPerRevolution;
//        }

        return flywheel.getVelocity() * 60 / tickPerRevolution;
//        return 0;

    }

    public double getCurrentWheelRawVelocity(String motor) {
//        if (motor.contains("left") || motor.contains("Left")) {
//            return leftFlyWheel.getVelocity();
//        } else if (motor.contains("right") || motor.contains("Right")) {
//            return rightFlyWheel.getVelocity();
//        }
//        return 0;
        return flywheel.getVelocity();
    }

    public void runOptimalFlywheelVelocity() {
        flywheel.setVelocity(tickPerRevolution * (optimalVelocity / 60));

//        leftFlyWheel.setVelocity(tickPerRevolution*(optimalVelocity/60));
//        rightFlyWheel.setVelocity(tickPerRevolution*(optimalVelocity/60));
    }

    public double returnOptimalFlywheelVelocity() {
        return optimalVelocity;
    }

//    public void setFlywheelRawVelocity(float givenRPM) {
//        leftFlyWheel.setVelocity(givenRPM);
//        rightFlyWheel.setVelocity(givenRPM);
//    }
}