package org.firstinspires.ftc.teamcode.AutoCode.Testing;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.AngularVelConstraint;
import com.acmerobotics.roadrunner.MinVelConstraint;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.SequentialAction;
import com.acmerobotics.roadrunner.TrajectoryActionBuilder;
import com.acmerobotics.roadrunner.TranslationalVelConstraint;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.AutoCode.Roadrunner.MecanumDrive;

import java.util.Arrays;

@Disabled
@Config
@Autonomous(name = "Dynamic Drive", group = "Autonomous")
public class DynamicDrive extends LinearOpMode {

    @Override
    public void runOpMode() {
        Pose2d startPose = new Pose2d(0, 0, 0); // x, y, heading in radians
        MecanumDrive drive = new MecanumDrive(hardwareMap, startPose);

        MinVelConstraint maxSpeedConstraint = new MinVelConstraint(Arrays.asList(
                new TranslationalVelConstraint(50.0),
                new AngularVelConstraint(Math.PI / 2)
        ));

        TrajectoryActionBuilder tab1 = drive.actionBuilder(startPose)
                .strafeToLinearHeading(new Vector2d(11, -26), Math.toRadians(-90), maxSpeedConstraint);

        Action trajectoryActionChosen = tab1.build();

        Actions.runBlocking(
                new SequentialAction(
                        trajectoryActionChosen
                )
        );


    }
}
