package org.firstinspires.ftc.teamcode.Hardware;

import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;

public class Flywheels {

    private DcMotorEx flywheel = null;
    final double tickPerRevolution = 28;

    public double p = 10.0;
    public double i = 1.0;
    public double d = 5.0;

    public double f = 15;
    //rpm = m * distance + b
    double m = 4.9141;
    double b = 800;//719.9908(og value)

    //Outtake subsystem
    public Flywheels(HardwareMap hMap) {

        flywheel = hMap.get(DcMotorEx.class, "flywheel");
        flywheel.setDirection(DcMotorEx.Direction.FORWARD);
        flywheel.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
        flywheel.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);

        PIDFCoefficients pidfCoefficients = new PIDFCoefficients(p, i, d, f);
        flywheel.setPIDFCoefficients(DcMotorEx.RunMode.RUN_USING_ENCODER, pidfCoefficients);

    }
    public void setFlywheelVelocity(double ticksPerSecond){
        flywheel.setVelocity(ticksPerSecond);
    }

    public double getFlywheelVelocity(){
        return flywheel.getVelocity();
    }

    public double launchFromDistance(double distance){ //distance in feet from goal
        double optimalSpeed = b+ (m * distance);

        if (distance > 8.0){
            optimalSpeed += 50;
        }

        setFlywheelVelocity(optimalSpeed);
        return optimalSpeed; //return the optimal ticksPerSecond for telemetry debugging...
        //return optimalSpeed*60/tickPerRevolution; //return the optimal rpm for telemetry debugging...
    }

    public double getVelocityFromDistance(double distance){
        double optimalSpeed = b+ (m * distance);

        if (distance > 8.0){
            optimalSpeed += 50;
        }

        return optimalSpeed;
    }

    public void stopFlywheel(){
        flywheel.setPower(0);
    }

    public void setFlywheelPower(double power){
        power = Math.max(-1, Math.min(1, power));
        flywheel.setPower(power);
    }

    @Deprecated
    public void setFlywheelRPM(double rpm){
        flywheel.setVelocity(rpm*tickPerRevolution/60);
    }

    @Deprecated
    public double getFlywheelRPM(){
        return flywheel.getVelocity()*60/tickPerRevolution;
    }

}