package org.firstinspires.ftc.teamcode.AutoCode.Testing;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.AngularVelConstraint;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.SequentialAction;
import com.acmerobotics.roadrunner.TrajectoryActionBuilder;
import com.acmerobotics.roadrunner.TranslationalVelConstraint;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.AutoCode.Pathings.PathingTrajectories;
import org.firstinspires.ftc.teamcode.AutoCode.Roadrunner.MecanumDrive;

import java.util.Arrays;

@Disabled
@Config
@Autonomous(name = "Trajectory Path Caller", group = "Autonomous")
public class CallTrajectoryPaths extends LinearOpMode {

    @Override
    public void runOpMode() {
        Pose2d startPose = new Pose2d(0, 0, 0); // x, y, heading in radians
        MecanumDrive drive = new MecanumDrive(hardwareMap, startPose);

        Action trajectoryActionChosen = PathingTrajectories.buildTab1(drive, startPose);
        Action trajectoryActionChosen2 = PathingTrajectories.buildTab2(drive,  new Pose2d(0, 0, 0));


        Actions.runBlocking(new SequentialAction(
                trajectoryActionChosen,
                trajectoryActionChosen2
            )
        );

    }
}
