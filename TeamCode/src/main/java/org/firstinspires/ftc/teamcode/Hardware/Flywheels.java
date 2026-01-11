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
    //rpm = m * distance + b
    double m = 90;
    double b = 1000;

    //Outtake subsystem
    public Flywheels(HardwareMap hMap) {

        flywheel = hMap.get(DcMotorEx.class, "flywheel");
        flywheel.setDirection(DcMotorEx.Direction.REVERSE);
        flywheel.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
        flywheel.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);

        PIDFCoefficients pidfCoefficients = new PIDFCoefficients(p, 0, 0, f);
        flywheel.setPIDFCoefficients(DcMotorEx.RunMode.RUN_USING_ENCODER, pidfCoefficients);

    }

    public void setFlywheelSpeed(double rpm){
        PIDFCoefficients pidfCoefficients = new PIDFCoefficients(p, 0, 0, f);
        flywheel.setPIDFCoefficients(DcMotorEx.RunMode.RUN_USING_ENCODER, pidfCoefficients);
        flywheel.setVelocity(rpm);
    }

    public double launchFromDistance(double distance){ //distance in feet from goal
        double optimalRpm = b+ (m * distance);
        setFlywheelSpeed(optimalRpm);
        return optimalRpm; //return the optimal rpm for telemetry debugging...
    }

    public void stopFlywheel(){
        flywheel.setPower(0);
    }

    //Update
    public void setFlywheelVelocity(float givenRPM) {
        flywheel.setVelocity(tickPerRevolution*(givenRPM/60));
    }

    public double getFlywheelVelocity(){
        return flywheel.getVelocity();
    }

    public double getRPMFromDistance(double distance){
        return b+ (m * distance);  //return the optimal rpm for telemetry debugging...

    }


    @Deprecated
    public void setFlywheelPower(double power) {
        if (power > 1) {
            flywheel.setPower(1.0);
        } else {
            flywheel.setPower(power);
        }
    }

    @Deprecated
    public void setRawFlywheelVelocity(float givenRPM) {
        flywheel.setVelocity(givenRPM);
    }


    @Deprecated
    public double getCurrentWheelVelocity(String motor){
//        if (motor.contains("left") || motor.contains("Left")) {
//            return leftFlyWheel.getVelocity()*60/tickPerRevolution;
//        } else if (motor.contains("right") || motor.contains("Right")) {
//            return rightFlyWheel.getVelocity()*60/tickPerRevolution;
//        }

        return flywheel.getVelocity()*60/tickPerRevolution;
//        return 0;

    }

    @Deprecated
    public double getCurrentWheelRawVelocity(String motor){
//        if (motor.contains("left") || motor.contains("Left")) {
//            return leftFlyWheel.getVelocity();
//        } else if (motor.contains("right") || motor.contains("Right")) {
//            return rightFlyWheel.getVelocity();
//        }
//        return 0;
        return flywheel.getVelocity();
    }

    @Deprecated
    public void runOptimalFlywheelVelocity() {
        flywheel.setVelocity(tickPerRevolution*(optimalVelocity/60));

//        leftFlyWheel.setVelocity(tickPerRevolution*(optimalVelocity/60));
//        rightFlyWheel.setVelocity(tickPerRevolution*(optimalVelocity/60));
    }

    @Deprecated
    public double returnOptimalFlywheelVelocity() {
        return optimalVelocity;
    }

//    public void setFlywheelRawVelocity(float givenRPM) {
//        leftFlyWheel.setVelocity(givenRPM);
//        rightFlyWheel.setVelocity(givenRPM);
//    }
}