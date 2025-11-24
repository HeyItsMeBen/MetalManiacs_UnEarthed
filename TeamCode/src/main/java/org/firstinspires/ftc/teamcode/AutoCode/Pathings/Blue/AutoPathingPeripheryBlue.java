package org.firstinspires.ftc.teamcode.AutoCode.Pathings.Blue;

import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.AutoCode.Pathings.PathingActions;
import org.firstinspires.ftc.teamcode.AutoCode.Roadrunner.MecanumDrive;
import org.firstinspires.ftc.teamcode.Hardware.Intake;
import org.firstinspires.ftc.teamcode.Hardware.Flywheels;
import org.firstinspires.ftc.teamcode.Hardware.Transfer;

@Autonomous(name = "(To Blue Goal, Short Run (5 second delay), Start at Goal) Competition Pathing: Auto Periphery", group = "Auto Pathing")
//@Disabled
public class AutoPathingPeripheryBlue extends LinearOpMode {

    Intake intake;
    Flywheels flywheels;
    Transfer intakeHinge;
    Transfer outtakeHinge;

    double firing_position_x = -18;
    double firing_position_y = 18;

    @Override
    public void runOpMode() {

        Pose2d beginPose = new Pose2d(-52, 52, Math.toRadians(310));
        MecanumDrive drive = new MecanumDrive(hardwareMap, beginPose);

        intake = new Intake(hardwareMap);
        flywheels = new Flywheels(hardwareMap);
        intakeHinge = new Transfer(hardwareMap);
        outtakeHinge = new Transfer(hardwareMap);
        //to do: add another hinge servo transfer servo

        waitForStart();

        Actions.runBlocking(
                drive.actionBuilder(beginPose)

                        .waitSeconds(5)

                        .strafeToLinearHeading(new Vector2d(firing_position_x, firing_position_y), Math.toRadians(315))

                        .stopAndAdd(new PathingActions.maintainIntake(intake))

                        .stopAndAdd(new PathingActions.runFlywheels(flywheels))

                        .stopAndAdd(new PathingActions.scoreBallSequence(intakeHinge, outtakeHinge, flywheels))
                        .stopAndAdd(new PathingActions.scoreBallSequence(intakeHinge, outtakeHinge, flywheels))
                        .stopAndAdd(new PathingActions.scoreBallSequence(intakeHinge, outtakeHinge, flywheels))
                        .stopAndAdd(new PathingActions.scoreBallSequence(intakeHinge, outtakeHinge, flywheels))

                        .stopAndAdd(new PathingActions.runIntake(intake, intakeHinge))

                        .stopAndAdd(new PathingActions.stopFlywheels(flywheels))

                        .strafeToLinearHeading(new Vector2d(-25, 30),Math.toRadians(180))

                        .stopAndAdd(new PathingActions.stopIntake(intake))

                        .build());
    }

}  // end class