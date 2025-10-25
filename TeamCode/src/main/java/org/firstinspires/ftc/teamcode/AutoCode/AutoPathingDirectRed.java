package org.firstinspires.ftc.teamcode.AutoCode;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.acmerobotics.roadrunner.Action;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Roadrunner.MecanumDrive;

import org.firstinspires.ftc.teamcode.Systems.Arm;
import org.firstinspires.ftc.teamcode.Systems.Intake;
import org.firstinspires.ftc.teamcode.Systems.Outtake;
import org.firstinspires.ftc.teamcode.Systems.Hinge;

@Autonomous(name = "Auto Pathing Direct Red", group = "Concept")
//@Disabled
public class AutoPathingDirectRed extends LinearOpMode {

    Intake intake;
    Arm arm;
    Outtake outtake;
    Hinge hinge;

    double armTarget=0;

    @Override
    public void runOpMode() {

        Pose2d beginPose = new Pose2d(12, -60, Math.PI / 2);
        MecanumDrive drive = new MecanumDrive(hardwareMap, beginPose);

        intake = new Intake(hardwareMap);
        arm = new Arm(hardwareMap);
        outtake = new Outtake(hardwareMap);
        hinge = new Hinge(hardwareMap);
        //to do: add another hinge servo transfer servo

        waitForStart();

        Actions.runBlocking(
                drive.actionBuilder(beginPose)

                        .strafeTo(new Vector2d(20, -40))

                        .splineToLinearHeading(new Pose2d(35, 55, Math.toRadians(225)), Math.toRadians(45))

                        .waitSeconds(1)

                        .stopAndAdd(new AutoPathingDirectRed.raiseArm(500))
                        .stopAndAdd(new AutoPathingDirectRed.scoreBallSequence(hardwareMap))
                        .stopAndAdd(new AutoPathingDirectRed.lowerArmFully())

                        .splineTo(new Vector2d(20, -40), Math.toRadians(270))

                        .build());
    }

    public class setHingePosition implements Action {
        public setHingePosition() {}
        public boolean run(@NonNull TelemetryPacket telemetryPacket) {
            return false;
        }
    }

    public class raiseArm implements Action {
        public raiseArm(double givenTarget) {armTarget=givenTarget;}
        public boolean run(@NonNull TelemetryPacket telemetryPacket) {
            sleepWhileRunningArmPID(2000);
            return false;
        }
    }
    public class scoreBallSequence implements Action {
        public scoreBallSequence(HardwareMap hMap) {}
        public boolean run(@NonNull TelemetryPacket telemetryPacket) {

            hinge.liftHinge(hinge.holdPosition);

            sleep(500);

            outtake.setFlywheelVelocity(3000);

            sleepWhileRunningArmPID(1000);

            hinge.liftHinge(hinge.firePosition);

            sleepWhileRunningArmPID(1000);

            outtake.setFlywheelVelocity(0);

            hinge.liftHinge(hinge.holdPosition);

            return false;
        }
    }
    public class lowerArmFully implements Action {    //lowers the arm to 0, to prepare for teleOp
        public lowerArmFully() {}
        public boolean run(@NonNull TelemetryPacket telemetryPacket) {
            armTarget=300;
            sleepWhileRunningArmPID(1000); //change later
            armTarget=100;
            sleepWhileRunningArmPID(500); //change later
            armTarget=0;
            sleepWhileRunningArmPID(100); //change later
            return false;
        }
    }
    public void sleepWhileRunningArmPID(double milliseconds){  //acts as a sleep function, while also running the arm PID. This keeps the arm at it's target position.
        ElapsedTime sleepTimer;
        sleepTimer = new ElapsedTime();
        while (sleepTimer.milliseconds()<milliseconds && opModeIsActive()){
            arm.raiseArmManual(arm.setArmTarget(armTarget));
        }
    }

}  // end class