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
@Autonomous(name = "Auto Pathing Direct", group = "Concept")
//@Disabled
public class AutoPathingDirect extends LinearOpMode {

    Intake Intake;
    Arm Aim;
    Outtake Flywheel;

    Outtake hinge;

    @Override
    public void runOpMode() {

        Pose2d beginPose = new Pose2d(12, -60, Math.PI / 2);
        MecanumDrive drive = new MecanumDrive(hardwareMap, beginPose);

        Intake = new Intake(hardwareMap);
        Aim = new Arm(hardwareMap);
        Flywheel = new Outtake(hardwareMap);
        hinge = new Outtake(hardwareMap);

        waitForStart();

        Actions.runBlocking(
                drive.actionBuilder(beginPose)

                        .strafeTo(new Vector2d(12, -40))
                        .splineToLinearHeading(new Pose2d(37, 37, Math.toRadians(225)), Math.toRadians(45))

                        .stopAndAdd(new raiseArm(hardwareMap))

                        .stopAndAdd(new liftHinge(hardwareMap))

                        .stopAndAdd(new lowerHinge(hardwareMap))

                        .stopAndAdd(new launchBall(hardwareMap))

                        .build());
    }

    public class runIntake implements Action {
        public runIntake(HardwareMap hMap) {}
        public boolean run(@NonNull TelemetryPacket telemtryPacket)  {
            Intake.setMotorPower(0.5);
            sleep(3);
            Intake.setMotorPower(0);
            return false;
        }
    }

    public class raiseArm implements Action {
        public raiseArm(HardwareMap hMap) {}
        public boolean run(@NonNull TelemetryPacket telemtryPacket)  {
            Aim.setArmTarget(1);
            Aim.stopMotor();
            return false;
        }
    }

    public class launchBall implements Action {
        public launchBall(HardwareMap hMap) {}
        public boolean run(@NonNull TelemetryPacket telemetryPacket) {
            Flywheel.fire(1);
            Flywheel.fire(0);
            return false;
        }
    }

    public class liftHinge implements Action {
        public liftHinge(HardwareMap hMap) {}
        public boolean run(@NonNull TelemetryPacket telemetryPacket) {
            hinge.liftHinge(10);
            return false;
        }
    }
    public class lowerHinge implements Action {
        public lowerHinge(HardwareMap hMap) {}
        public boolean run(@NonNull TelemetryPacket telemetryPacket) {
            hinge.liftHinge(0);
            return false;
        }
    }

}  // end class