package org.firstinspires.ftc.teamcode.Hardware;

import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;

public class Flywheels {

    private DcMotorEx flywheel = null;
    //    private DcMotorEx leftFlyWheel = null;
//    private DcMotorEx rightFlyWheel = null;
    final double tickPerRevolution = 28;

    public double f = 14.12;
    public double p = 100;
    //rpm = m * distance + b
    double m = 6.68363;
    double b = 922.48777+115;

    //Outtake subsystem
    public Flywheels(HardwareMap hMap) {

        flywheel = hMap.get(DcMotorEx.class, "flywheel");
        flywheel.setDirection(DcMotorEx.Direction.REVERSE);
        flywheel.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
        flywheel.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);

        PIDFCoefficients pidfCoefficients = new PIDFCoefficients(p, 0, 0, f);
        flywheel.setPIDFCoefficients(DcMotorEx.RunMode.RUN_USING_ENCODER, pidfCoefficients);

    }
    public void setFlywheelVelocity(double ticksPerSecond){
        flywheel.setVelocity(ticksPerSecond);
    }

    public double getFlywheelVelocity(){
        return flywheel.getVelocity();
    }

    public void setFlywheelRPM(double rpm){
        flywheel.setVelocity(rpm*tickPerRevolution/60);
    }
    public double getFlywheelRPM(){
        return flywheel.getVelocity()*60/tickPerRevolution;
    }

    public double launchFromDistance(double distance, double extraSpeed){ //distance in feet from goal
        double optimalSpeed = b+ (m * distance);

        if (distance > 8.0){
            optimalSpeed += 50;
        }

        setFlywheelVelocity(optimalSpeed);
        return optimalSpeed; //return the optimal ticksPerSecond for telemetry debugging...
        //return optimalSpeed*60/tickPerRevolution; //return the optimal rpm for telemetry debugging...
    }

    public double getVelocityFromDistance(double distance){
        return b+ (m * distance);  //return the optimal rpm for telemetry debugging...
    }

    public void stopFlywheel(){
        flywheel.setPower(0);
    }

    public void setFlywheelPower(double power){
        power = Math.max(-1, Math.min(1, power));
        flywheel.setPower(power);
    }

}