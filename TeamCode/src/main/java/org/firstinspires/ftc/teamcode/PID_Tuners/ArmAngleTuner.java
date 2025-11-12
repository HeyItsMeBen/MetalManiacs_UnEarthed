package org.firstinspires.ftc.teamcode.PID_Tuners;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Systems.Arm;
import org.firstinspires.ftc.teamcode.Systems.Hinge;
import org.firstinspires.ftc.teamcode.Systems.Flywheels;
@Deprecated
@Disabled
@Config
@Autonomous(name = "ArmAngleTuner", group = "Autonomous")
public class ArmAngleTuner extends LinearOpMode {
    public static double armShootPosition=700;
    double velocityPeak=0;
    double armTarget=0;
    Flywheels outtake;
    Hinge hinge;
    Arm arm;
    public void runOpMode() {
        outtake = new Flywheels(hardwareMap);
        hinge = new Hinge(hardwareMap);
        arm = new Arm(hardwareMap);
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
        waitForStart();
        while (opModeIsActive()){
            armTarget=armShootPosition;
            sleepWhileRunningArmPID(2000);
            outtake.setFlywheelVelocity(3000);
            sleepWhileRunningArmPID(1000);
            hinge.liftHinge(hinge.firePosition);
            sleepWhileRunningArmPID(1000);
            velocityPeak=outtake.getCurrentWheelVelocity("right");  //this is just something for telemetry. It's the velocity that the wheels get to before launching and shutting off. If you plan to use it, remember to run it WITHOUT the ball, or else value will be off
            outtake.setFlywheelVelocity(0);
            hinge.liftHinge(hinge.holdPosition);
            telemetry.addData("VELOCITY: ", velocityPeak);
            telemetry.addData("Arm Pos: ", arm.getArmPosition());
            telemetry.addData("Arm Target: ", armShootPosition);
            telemetry.update();
            armTarget=100;
            sleepWhileRunningArmPID(2000);
            sleep(6000);
        }
    }
    public void sleepWhileRunningArmPID(double milliseconds){  //acts as a sleep function, while also running the arm PID. This keeps the arm at it's target position.
        ElapsedTime sleepTimer;
        sleepTimer = new ElapsedTime();
        while (sleepTimer.milliseconds()<milliseconds && opModeIsActive()){
            arm.raiseArmManual(arm.setArmTarget(armTarget));
        }
    }
}
/*
outtake.setFlywheelVelocity(3000, 1);
            hinge.liftHingeAndWait(hinge.firePosition, 1);
            velocityPeak=outtake.getCurrentWheelRPM();
            outtake.setFlywheelVelocity(0, 0);
            hinge.liftHingeAndWait(hinge.holdPosition, 0);
 */