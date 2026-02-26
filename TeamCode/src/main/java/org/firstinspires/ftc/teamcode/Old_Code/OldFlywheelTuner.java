package org.firstinspires.ftc.teamcode.Old_Code;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.util.ElapsedTime;

/*
Configurations:
0: frontLeft
1: backLeft
2: frontRight
3: backRight
 */
@Config
@Autonomous(name = "OldFlywheelTuner", group = "Linear OpMode")
public class OldFlywheelTuner extends LinearOpMode {
    DcMotorEx flywheel; //creates "flywheel" variable, and sets as a "DcMotorEx"-type variable.

    public static double rpm=40;  //rotations per minute. Change the value to whatever u want
    public static int onSeconds=2;
    public static int offSeconds=5;

    //setting PID variables for later calculations
    double integralSum=0;
    public static double Kp=0;
    public static double Ki=0;
    public static double Kd=0;
    public static double Kf=0.05;
    public double lastError=0;


    final double tickPerRevolution=28*5.2;
    double ticksPerSecond=tickPerRevolution*(rpm/60);   //turns rotation per minute (rpm), into ticks per second. This allows it to by used by the "PIDControl" method, which only intakes ticks per second
    ElapsedTime timer = new ElapsedTime();  //keeps track of time. Used for PID calculations

    @Override
    public void runOpMode() throws InterruptedException{
        flywheel = hardwareMap.get(DcMotorEx.class, "intake"); //connects the flywheel variable with the actual motor in the control hub
        flywheel.setDirection(DcMotor.Direction.FORWARD);

        flywheel.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());

        waitForStart(); //waits until you start the program from the driver station
        while (opModeIsActive()){   //infinite loop
            //double power = PIDControl(ticksPerSecond, flywheelRight.getVelocity());    //calculates the needed power with PID
            ticksPerSecond=tickPerRevolution*(rpm/60);
            flywheel.setVelocity(ticksPerSecond);
            //sleep(onSeconds*1000);
            telemetry.addData("Target RPM", rpm);
            telemetry.addData("Current RPM", flywheel.getVelocity()*60/tickPerRevolution);
            telemetry.update();
//            flywheelLeft.setVelocity(0);
//            flywheelRight.setVelocity(0);
//            sleep(offSeconds*1000);
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