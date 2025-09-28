package org.firstinspires.ftc.teamcode.DriveCode;

import static java.lang.Math.atan;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

public class outtakeFlywheel{
    DcMotorEx flywheel; //creates "flywheel" variable, and sets as a "DcMotorEx"-type variable.

    double rpm=40;  //change the value to whatever u want

    //setting PID variables for later calculations
    double integralSum=0;
    double Kp=0;
    double Ki=0;
    double Kd=0;
    double Kf=0;
    public double lastError=0;


    final double tickPerRevolution=1120;
    double ticksPerSecond=tickPerRevolution*(rpm/60);   //turns rotation per minute (rpm), into ticks per second. This allows it to by used by the "PIDControl" method, which only intakes ticks per second
    ElapsedTime timer = new ElapsedTime();  //keeps track of time. Used for PID calculations
    public outtakeFlywheel(HardwareMap hMap){
        flywheel = hMap.get(DcMotorEx.class, "flywheelLeft");    //connects the flywheel variable with the actual motor in the control hub
        flywheel.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);      //The video told me to type it...
    }

    public double [] calculateAngleAndVelocity(double basketX){
        double basketY=40;  //measured in meters. Change this to actual value. Actual value should equal basketHeight-heightOfFlywheelFromGround+ballRadius
        double H = 60; //measured in meters. Max height that launched ball will reach. Change as desired.
        double gravity=9.8; //i think this is the right value
        double theta1=atan((2*H/basketX)*(1+Math.sqrt(1-basketY/H)));
        double theta2=atan((2*H/basketX)*(1-Math.sqrt(1-basketY/H)));
        double theta1Distance=2*H*(1/Math.tan(theta1));
        double theta2Distance=2*H*(1/Math.tan(theta2));
        double theta=0; //radians
        double [] values={0, 0};
        boolean err=false;

        //Stuff for ballVelocity-->wheelVelocity
        double flywheelWeight=3; //kilograms (kg)
        double ballWeight=2;

        if (theta1Distance<basketX){
            theta=theta1;
        }
        else if (theta2Distance<basketX){
            theta=theta2;
        }
        else {
            err=true;
        }

        double ballVelocity=Math.sqrt(2*gravity*H)/Math.abs(Math.sin(theta));
        double neededEnergy = Math.pow(ballVelocity, 2)*(2*flywheelWeight+ballWeight);
        double wheelVelocity=Math.sqrt(neededEnergy/(2*flywheelWeight)); //measured in meters per second (m/s)

        if (!err && theta!=0) {
            values[0]=wheelVelocity;    //the velocity that wheels will need to be spinning at
            values[1]=theta;            //the angle that the ball will need to be launched at
        }
        return values;
    }
    public double getPIDPower(){
        return PIDControl(ticksPerSecond, flywheel.getVelocity());  //calculates and return the needed power with PID
    }
    private double PIDControl(double reference, double state){  //This is where the magic happens. It does some weird math
        double error=reference-state;
        integralSum+=error*timer.seconds();
        double derivitive = (error-lastError) / timer.seconds();
        lastError=error;

        timer.reset();

        double output = (error*Kp)+(derivitive*Kd)+(integralSum*Ki)+(reference*Kf);
        return output;
    }
}
