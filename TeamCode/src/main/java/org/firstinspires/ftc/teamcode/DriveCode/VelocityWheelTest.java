package org.firstinspires.ftc.teamcode.DriveCode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.util.ElapsedTime;

public class VelocityWheelTest extends LinearOpMode {
    DcMotorEx flywheel; //creates "flywheel" variable, and sets as a "DcMotorEx"-type variable.

    double rpm=40;  //rotations per minute. Change the value to whatever u want

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

    @Override
    public void runOpMode() throws InterruptedException{
        flywheel = hardwareMap.get(DcMotorEx.class, "outtakeFlywheel"); //connects the flywheel variable with the actual motor in the control hub
        flywheel.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);  //The video told me to type it...

        waitForStart(); //waits until you start the program from the driver station
        while (opModeIsActive()){   //infinite loop
            double power = PIDControl(ticksPerSecond*tickPerRevolution, flywheel.getVelocity());    //calculates the needed power with PID
            flywheel.setPower(power);
        }
    }
    public double PIDControl(double reference, double state){   //This is where the magic happens. It does some weird math
        double error=reference-state;
        integralSum+=error*timer.seconds();
        double derivitive = (error-lastError) / timer.seconds();
        lastError=error;

        timer.reset();

        double output = (error*Kp)+(derivitive*Kd)+(integralSum*Ki)+(reference*Kf);
        return output;
    }
}
