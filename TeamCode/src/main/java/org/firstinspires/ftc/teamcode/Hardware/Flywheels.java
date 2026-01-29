package org.firstinspires.ftc.teamcode.Hardware;

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
    double m = 6.68363;
    double b = 922.48777+100;

    //Outtake subsystem
    public Flywheels(HardwareMap hMap) {

        flywheel = hMap.get(DcMotorEx.class, "flywheel");
        flywheel.setDirection(DcMotorEx.Direction.REVERSE);
        flywheel.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
        flywheel.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);

        PIDFCoefficients pidfCoefficients = new PIDFCoefficients(p, 0, 0, f);
        flywheel.setPIDFCoefficients(DcMotorEx.RunMode.RUN_USING_ENCODER, pidfCoefficients);

    }
    public void setFlywheelSpeedRaw(double ticksPerSecond){
        PIDFCoefficients pidfCoefficients = new PIDFCoefficients(p, 0, 0, f);
        flywheel.setPIDFCoefficients(DcMotorEx.RunMode.RUN_USING_ENCODER, pidfCoefficients);
        flywheel.setVelocity(ticksPerSecond);
    }
    public void setFlywheelRPM(double givenRPM){
        PIDFCoefficients pidfCoefficients = new PIDFCoefficients(p, 0, 0, f);
        flywheel.setPIDFCoefficients(DcMotorEx.RunMode.RUN_USING_ENCODER, pidfCoefficients);
        flywheel.setVelocity(tickPerRevolution*(givenRPM/60));
    }
    public double getFlywheelSpeedRaw(){
        return flywheel.getVelocity();
    }
    public double getFlywheelRPM(){
        return flywheel.getVelocity()*60/tickPerRevolution;
    }


    public double launchFromDistance(double distance, double extraSpeed){ //distance in feet from goal
        double optimalSpeed = b+ (m * distance);
        setFlywheelSpeedRaw(optimalSpeed +extraSpeed);
        return optimalSpeed; //return the optimal ticksPerSecond for telemetry debugging...
        //return optimalSpeed*60/tickPerRevolution; //return the optimal rpm for telemetry debugging...
    }
    public double launchFromDistance(double distance){ //distance in feet from goal
        double optimalSpeed = b+ (m * distance);
        setFlywheelSpeedRaw(optimalSpeed);
        return optimalSpeed; //return the optimal ticksPerSecond for telemetry debugging...
        //return optimalSpeed*60/tickPerRevolution; //return the optimal rpm for telemetry debugging...
    }

    public void stopFlywheel(){
        flywheel.setPower(0);
    }

    public double getRPMFromDistance(double distance){
        return b+ (m * distance);  //return the optimal rpm for telemetry debugging...

    }

    public void setFlywheelPower(double power) {
        if (power > 1) {
            flywheel.setPower(1.0);
        } else {
            flywheel.setPower(power);
        }
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
}