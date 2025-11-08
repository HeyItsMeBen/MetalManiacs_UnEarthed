package org.firstinspires.ftc.teamcode.AutoCode.Pathings.Red;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Roadrunner.MecanumDrive;
import org.firstinspires.ftc.teamcode.Systems.Arm;
import org.firstinspires.ftc.teamcode.Systems.Hinge;
import org.firstinspires.ftc.teamcode.Systems.Intake;
import org.firstinspires.ftc.teamcode.Systems.Outtake;

@Autonomous(name = "Auto Pathing Periphery Red", group = "Concept")
//@Disabled
public class AutoPathingPeripheryRed extends LinearOpMode {

    Intake intake;
    Outtake outtake;
    Hinge hinge;

    double armTarget=0;

    @Override
    public void runOpMode() {

        Pose2d beginPose = new Pose2d(50, 50, Math.toRadians(225));
        MecanumDrive drive = new MecanumDrive(hardwareMap, beginPose);

        intake = new Intake(hardwareMap);
        outtake = new Outtake(hardwareMap);
        hinge = new Hinge(hardwareMap);
        //to do: add another hinge servo transfer servo

        waitForStart();

        Actions.runBlocking(
                drive.actionBuilder(beginPose)
                        .stopAndAdd(new AutoPathingPeripheryRed.setHingePosition())

                        .strafeTo(new Vector2d(26, 26))

                        .stopAndAdd(new AutoPathingPeripheryRed.scoreBallSequence(hardwareMap))

                        .splineTo(new Vector2d(25, -35), Math.toRadians(270))

                        .build());
    }

    public class setHingePosition implements Action {
        public setHingePosition() {}
        public boolean run(@NonNull TelemetryPacket telemetryPacket) {
            hinge.liftHinge(hinge.holdPosition);
            return false;
        }
    }

    public class scoreBallSequence implements Action {
        public scoreBallSequence(HardwareMap hMap) {}
        public boolean run(@NonNull TelemetryPacket telemetryPacket) {

            hinge.liftHinge(hinge.holdPosition);

            sleep(500);

//            outtake.setFlywheelVelocity(3000);
            outtake.setFlywheelVelocity(2350);

            hinge.liftHinge(hinge.firePosition);

            outtake.setFlywheelVelocity(0);

            hinge.liftHinge(hinge.holdPosition);

            return false;
        }
    }

}  // end class