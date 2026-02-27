package org.firstinspires.ftc.teamcode.AutoCode.Pathings.TrajectoryPaths.LeaveVarients;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.SequentialAction;
import com.acmerobotics.roadrunner.TrajectoryActionBuilder;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.AutoCode.Roadrunner.MecanumDrive;
import org.firstinspires.ftc.teamcode.Controllers.LightsController;
import org.firstinspires.ftc.teamcode.DriveCode.PassOnFromAutoValues;
import org.firstinspires.ftc.teamcode.Hardware.Lights;

@Config
@Autonomous(name = "Leave / Park Blue", group = "z-Autonomous - Any")
public class LeaveBlue extends LinearOpMode {

    Lights lights;

    LightsController lightsController;

    public String ballSequence = "XXX";


    @Override
    public void runOpMode() {

        Pose2d startPose = new Pose2d(12, -60, Math.toRadians(180));
        MecanumDrive drive = new MecanumDrive(hardwareMap, startPose);

        lights = new Lights(hardwareMap);
        lightsController = new LightsController(lights);

        waitForStart();
        if (isStopRequested()) return;

        lightsController.update(false, false, "Red",ballSequence);

        TrajectoryActionBuilder park = drive.actionBuilder(startPose)

                .strafeTo(new Vector2d((drive.localizer.getPose().position.x - 30), drive.localizer.getPose().position.y));

        Actions.runBlocking(
                new SequentialAction(

                        park.build()

                )
        );

        PassOnFromAutoValues.currentPose = drive.localizer.getPose();
        PassOnFromAutoValues.teamColor = PassOnFromAutoValues.TeamColor.BLUE;

    }

}

