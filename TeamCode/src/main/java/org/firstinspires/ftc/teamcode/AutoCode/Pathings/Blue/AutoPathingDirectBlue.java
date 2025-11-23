package org.firstinspires.ftc.teamcode.AutoCode.Pathings.Blue;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.TrajectoryBuilder;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.acmerobotics.roadrunner.Action;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.AutoCode.Pathings.PathingActions;
import org.firstinspires.ftc.teamcode.Roadrunner.MecanumDrive;

import org.firstinspires.ftc.teamcode.Systems.Intake;
import org.firstinspires.ftc.teamcode.Systems.Flywheels;
import org.firstinspires.ftc.teamcode.Systems.Transfer;

@Autonomous(name = "(To Blue Goal, Short Run (5 second delay), Start at Wall) Competition Pathing: Auto Direct", group = "Auto Pathing")
//@Disabled
public class AutoPathingDirectBlue extends LinearOpMode {

    Intake intake;
    Flywheels flywheels;
    Transfer intakeHinge;
    Transfer outtakeHinge;

    double firing_position_x = -18;
    double firing_position_y = 18;

    @Override
    public void runOpMode() {

        Pose2d beginPose = new Pose2d(-15, -60, 3*Math.PI / 2);
        MecanumDrive drive = new MecanumDrive(hardwareMap, beginPose);

        intake = new Intake(hardwareMap);
        flywheels = new Flywheels(hardwareMap);
        intakeHinge = new Transfer(hardwareMap);
        outtakeHinge = new Transfer(hardwareMap);

        waitForStart();

        Actions.runBlocking(
                drive.actionBuilder(beginPose)
                        .waitSeconds(5)

                        .setReversed(true)

                        .splineTo(new Vector2d(-20, -30), Math.toRadians(90))
                        .splineTo(new Vector2d(firing_position_x, firing_position_y), Math.toRadians(130))

                        .stopAndAdd(new PathingActions.maintainIntake(intake))

                        .stopAndAdd(new PathingActions.runFlywheels(flywheels))

                        .stopAndAdd(new PathingActions.scoreBallSequence(intakeHinge, outtakeHinge, flywheels))
                        .stopAndAdd(new PathingActions.scoreBallSequence(intakeHinge, outtakeHinge, flywheels))
                        .stopAndAdd(new PathingActions.scoreBallSequence(intakeHinge, outtakeHinge, flywheels))
                        .stopAndAdd(new PathingActions.scoreBallSequence(intakeHinge, outtakeHinge, flywheels))

                        .stopAndAdd(new PathingActions.stopFlywheels(flywheels))

                        .setReversed(false)

                        .strafeToLinearHeading(new Vector2d(-25, 30),Math.toRadians(180))

                        .build());
    }

}  // end class