package org.firstinspires.ftc.teamcode.Experimental.FlywheelTuning;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

/*
Configurations:
0: frontLeft
1: backLeft
2: frontRight
3: backRight
 */

@Disabled

@Config
@Autonomous(name = "InertiaCalculator", group = "Linear OpMode")
public class InertiaCalculator extends LinearOpMode {
    DcMotorEx flywheelLeft; //creates "flywheel" variable, and sets as a "DcMotorEx"-type variable.
    DcMotorEx flywheelRight;

    public static float rpm=2500;  //rotations per minute. Change the value to whatever u want
    public static int prepSeconds=2;
    public static int calculateSeconds=2;


    final float tickPerRevolution=28;//1120-->28
    float ticksPerSecond=tickPerRevolution*(rpm/60f);   //turns rotation per minute (rpm), into ticks per second.


    //Inertia calculation
    //Constants. SET THESE TO THE ACTUAL VALUES
    float ballMass=70.5f/1000;   //measured in kg
    float wheelCircumference=0.3989823f; //measured in meters
    //variables setup
    float maxWheelVelocity=0;
    float minimumWheelVelocity=0;
    double inertia=0;

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
        /*hinge.liftHinge(hinge.holdPosition);
        ticksPerSecond=tickPerRevolution*(rpm/60f);
        flywheelLeft.setVelocity(ticksPerSecond);
        flywheelRight.setVelocity(ticksPerSecond);
        sleep(prepSeconds*1000);
        float flywheelRpm=(float)flywheelRight.getVelocity()*60f/tickPerRevolution;
        float lastFlywheelRpm=flywheelRpm;
        maxWheelVelocity=rpmToMeterPerSecond(flywheelRpm);
        telemetry.addData("Target RPM: ", rpm);
        telemetry.addData("Current RPM: ", flywheelRpm);
        telemetry.addLine("Slide ball in now. Calculating minimum...");
        telemetry.update();
//        while (maxWheelVelocity>rpmToMeterPerSecond(flywheelRpm)-200 && opModeIsActive()){
//            flywheelRpm=flywheelRight.getVelocity()*60/tickPerRevolution;
//        }
        ElapsedTime timer = new ElapsedTime();
        while (timer.milliseconds()<calculateSeconds*1000){
            flywheelRpm=(float)flywheelRight.getVelocity()*60/tickPerRevolution;
            if (flywheelRpm<lastFlywheelRpm){
                lastFlywheelRpm=flywheelRpm;
            }
        }
        minimumWheelVelocity=rpmToMeterPerSecond(lastFlywheelRpm);
        inertia=(ballMass*Math.pow(minimumWheelVelocity, 2))/(2*(Math.pow(maxWheelVelocity, 2)-Math.pow(minimumWheelVelocity, 2)));
        telemetry.addData("Velocity max: ", maxWheelVelocity);
        telemetry.addData("Velocity min: ", minimumWheelVelocity);
        telemetry.addData("Inertia: ", inertia);
        telemetry.update();
        flywheelLeft.setVelocity(0);
        flywheelRight.setVelocity(0);
        sleep(5*1000);

         */
        maxWheelVelocity=rpmToMeterPerSecond(2500);
        minimumWheelVelocity=rpmToMeterPerSecond((1000+1071.42857f+1119.04762f+1160.71429f+1107.14286f+1050)/6);
        telemetry.addData("Inertia (2500): ", (ballMass*Math.pow(minimumWheelVelocity, 2))/(2*(Math.pow(maxWheelVelocity, 2)-Math.pow(minimumWheelVelocity, 2))));

        maxWheelVelocity=rpmToMeterPerSecond(1500);
        minimumWheelVelocity=rpmToMeterPerSecond(500);
        telemetry.addData("Inertia (1500): ", (ballMass*Math.pow(minimumWheelVelocity, 2))/(2*(Math.pow(maxWheelVelocity, 2)-Math.pow(minimumWheelVelocity, 2))));
        telemetry.update();
        sleep(5*1000);
    }
    float rpmToMeterPerSecond(float givenRpm){
        float mPerS=givenRpm*(wheelCircumference/60);
        return mPerS;
    }
}