package org.firstinspires.ftc.teamcode.DriveCode;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
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
@Autonomous(name = "VelocityWheelTest", group = "Linear OpMode")
public class VelocityWheelTest extends LinearOpMode {
    DcMotorEx flywheelLeft; //creates "flywheel" variable, and sets as a "DcMotorEx"-type variable.
    DcMotorEx flywheelRight;

    public static double rpm=40;  //rotations per minute. Change the value to whatever u want

    //setting PID variables for later calculations
    double integralSum=0;
    public static double Kp=0;
    public static double Ki=0;
    public static double Kd=0;
    public static double Kf=0.05;
    public double lastError=0;


    final double tickPerRevolution=28;//1120-->28
    double ticksPerSecond=tickPerRevolution*(rpm/60);   //turns rotation per minute (rpm), into ticks per second. This allows it to by used by the "PIDControl" method, which only intakes ticks per second
    ElapsedTime timer = new ElapsedTime();  //keeps track of time. Used for PID calculations

    @Override
    public void runOpMode() throws InterruptedException{
        flywheelLeft = hardwareMap.get(DcMotorEx.class, "leftFlyWheel"); //connects the flywheel variable with the actual motor in the control hub
        flywheelRight = hardwareMap.get(DcMotorEx.class, "rightFlyWheel");
        flywheelLeft.setDirection(DcMotor.Direction.FORWARD);
        flywheelRight.setDirection(DcMotor.Direction.REVERSE);

        flywheelLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        flywheelRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);  //The video told me to type it...
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());

        waitForStart(); //waits until you start the program from the driver station
        while (opModeIsActive()){   //infinite loop
            //double power = PIDControl(ticksPerSecond, flywheelRight.getVelocity());    //calculates the needed power with PID
            ticksPerSecond=tickPerRevolution*(rpm/60);
            flywheelLeft.setVelocity(ticksPerSecond);
            flywheelRight.setVelocity(ticksPerSecond);
            telemetry.addData("Target RPM", rpm);
            telemetry.addData("Current RPM", flywheelRight.getVelocity()*60/tickPerRevolution);
            telemetry.update();

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
