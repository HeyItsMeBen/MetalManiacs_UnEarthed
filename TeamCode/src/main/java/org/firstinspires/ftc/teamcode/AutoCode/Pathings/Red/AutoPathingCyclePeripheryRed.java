package org.firstinspires.ftc.teamcode.AutoCode.Pathings.Red;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.acmerobotics.roadrunner.Action;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.AutoCode.Pathings.PathingActions;
import org.firstinspires.ftc.teamcode.Roadrunner.MecanumDrive;

import org.firstinspires.ftc.teamcode.Systems.Transfer;

import org.firstinspires.ftc.teamcode.Systems.Intake;
import org.firstinspires.ftc.teamcode.Systems.Flywheels;

@Autonomous(name = "(To Red Goal, Long Run, Start at Goal) Competition Pathing: Auto Periphery", group = "Auto Pathing")
//@Disabled
public class AutoPathingCyclePeripheryRed extends LinearOpMode {

    Intake intake;
    Flywheels flywheels;
    Transfer intakeHinge;
    Transfer outtakeHinge;

    double firing_position_x = 18;
    double firing_position_y = 18;

    @Override
    public void runOpMode() {

        Pose2d beginPose = new Pose2d(52, 52, Math.toRadians(220));
        MecanumDrive drive = new MecanumDrive(hardwareMap, beginPose);

        intake = new Intake(hardwareMap);
        flywheels = new Flywheels(hardwareMap);
        intakeHinge = new Transfer(hardwareMap);
        outtakeHinge = new Transfer(hardwareMap);

        waitForStart();

        Actions.runBlocking(
                drive.actionBuilder(beginPose)

                        .strafeToLinearHeading(new Vector2d(firing_position_x, firing_position_y), Math.toRadians(225))

                        .stopAndAdd(new PathingActions.maintainIntake(intake))

                        .stopAndAdd(new PathingActions.runFlywheels(flywheels))

                        .stopAndAdd(new PathingActions.scoreBallSequence(intakeHinge, outtakeHinge, flywheels))
                        .stopAndAdd(new PathingActions.scoreBallSequence(intakeHinge, outtakeHinge, flywheels))
                        .stopAndAdd(new PathingActions.scoreBallSequence(intakeHinge, outtakeHinge, flywheels))
                        .stopAndAdd(new PathingActions.scoreBallSequence(intakeHinge, outtakeHinge, flywheels))

                        .stopAndAdd(new PathingActions.stopFlywheels(flywheels))

                        .setReversed(false)

                        .strafeToLinearHeading(new Vector2d(firing_position_x, 5), Math.toRadians(0))

                        .stopAndAdd(new PathingActions.runIntake(intake, intakeHinge))

                        .strafeTo(new Vector2d(63, 5))

                        .waitSeconds(1)

                        .setTangent(180)
                        .splineToSplineHeading(new Pose2d(firing_position_x, firing_position_y, Math.toRadians(225)), Math.toRadians(135))

                        .stopAndAdd(new PathingActions.maintainIntake(intake))

                        .stopAndAdd(new PathingActions.runFlywheels(flywheels))

                        .stopAndAdd(new PathingActions.scoreBallSequence(intakeHinge, outtakeHinge, flywheels))
                        .stopAndAdd(new PathingActions.scoreBallSequence(intakeHinge, outtakeHinge, flywheels))
                        .stopAndAdd(new PathingActions.scoreBallSequence(intakeHinge, outtakeHinge, flywheels))

                        .stopAndAdd(new PathingActions.runIntake(intake, intakeHinge))

                        .stopAndAdd(new PathingActions.stopFlywheels(flywheels))

                        .stopAndAdd(new PathingActions.stopIntake(intake))

                        .setReversed(false)

                        .strafeToLinearHeading(new Vector2d(25, -30),Math.toRadians(0))

                        .build());
    }

}  // end class