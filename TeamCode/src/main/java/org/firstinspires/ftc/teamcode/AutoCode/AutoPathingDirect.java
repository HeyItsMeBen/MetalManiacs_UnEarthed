package org.firstinspires.ftc.teamcode.AutoCode;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.acmerobotics.roadrunner.Action;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;

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

    @Override
    public void runOpMode() {

        Pose2d beginPose = new Pose2d(12, -60, Math.PI / 2);
        MecanumDrive drive = new MecanumDrive(hardwareMap, beginPose);

        Intake = new Intake(hardwareMap);
        Aim = new Arm(hardwareMap);
        Flywheel = new Outtake(hardwareMap);

        waitForStart();

        Actions.runBlocking(
                drive.actionBuilder(beginPose)
                        //.splineTo(new Vector2d(48, 13), 0)

                        .stopAndAdd(new runIntake(hardwareMap))



                        //.waitSeconds(0.5f)
                        //.setTangent(Math.toRadians(180))
                        //.splineTo(new Vector2d(37, 37), Math.toRadians(45))
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

    public class aimArm implements Action {
        public aimArm(HardwareMap hMap) {}
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

}  // end class