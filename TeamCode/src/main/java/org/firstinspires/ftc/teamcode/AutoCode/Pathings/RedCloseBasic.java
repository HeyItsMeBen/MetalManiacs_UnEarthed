package org.firstinspires.ftc.teamcode.AutoCode.Pathings;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.SequentialAction;
import com.acmerobotics.roadrunner.TrajectoryActionBuilder;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.AutoCode.Roadrunner.MecanumDrive;
import org.firstinspires.ftc.teamcode.Controllers.AutoAimTurretController;
import org.firstinspires.ftc.teamcode.Controllers.FlywheelController;
import org.firstinspires.ftc.teamcode.Controllers.IntakeController;
import org.firstinspires.ftc.teamcode.Controllers.LightsController;
import org.firstinspires.ftc.teamcode.Hardware.AutoAim;
import org.firstinspires.ftc.teamcode.Hardware.Flywheels;
import org.firstinspires.ftc.teamcode.Hardware.Intake;
import org.firstinspires.ftc.teamcode.Hardware.Lights;
import org.firstinspires.ftc.teamcode.Hardware.OuttakeHood;
import org.firstinspires.ftc.teamcode.Hardware.Transfer;
import org.firstinspires.ftc.teamcode.Hardware.Turret;

@Config
@Autonomous(name = "RedCloseBasic", group = "Autonomous - Any")
public class RedCloseBasic extends LinearOpMode {

    Lights lights;

    LightsController lightsController;
    public String ballSequence = "XXX";


    @Override
    public void runOpMode() {

        Pose2d startPose = new Pose2d(52, 52, Math.toRadians(40));
        MecanumDrive drive = new MecanumDrive(hardwareMap, startPose);

        lights = new Lights(hardwareMap);
        lightsController = new LightsController(lights);

        waitForStart();
        if (isStopRequested()) return;

        lightsController.update(false, false, "Red",ballSequence);

        TrajectoryActionBuilder park = drive.actionBuilder(startPose)


                .setReversed(true)
                .splineToSplineHeading(new Pose2d(15, 10, Math.toRadians(0)), Math.toRadians(270))
                .waitSeconds(0.5f)

                //picks up from middle row of balls
                .setReversed(false)
                .splineToConstantHeading(new Vector2d(30,-12), Math.toRadians(0))
                .splineToConstantHeading(new Vector2d(50, -12), Math.toRadians(0))
                .waitSeconds(0.25f)

                //get in position to shoot
                .setReversed(true)
                .splineToConstantHeading(new Vector2d(15, 10), Math.toRadians(90))
                .waitSeconds(0.5f)

                //get balls from gate
                .setReversed(false)
                .splineToSplineHeading(new Pose2d(58,-10, Math.toRadians(20)), Math.toRadians(0))
                .waitSeconds(0.5f)

                //get in position to shoot
                .setReversed(true)
                .splineToConstantHeading(new Vector2d(15, 10), Math.toRadians(90))
                .waitSeconds(0.5f)

                //picks up balls from the top
                .setReversed(false)
                .splineToSplineHeading(new Pose2d(50,12, Math.toRadians(0)), Math.toRadians(0))
                .waitSeconds(0.25f)

                //get in position to shoot
                .setReversed(true)
                .splineToConstantHeading(new Vector2d(15, 10), Math.toRadians(90))
                .waitSeconds(0.5f)

                //pick up balls from the bottom
                .setReversed(false)
                .splineToConstantHeading(new Vector2d(37, -35), Math.toRadians(0))
                .splineToConstantHeading(new Vector2d(50, -35), Math.toRadians(0))
                .waitSeconds(0.5f)

                //get in position to shoot
                .setReversed(true)
                .splineToConstantHeading(new Vector2d(15, 10), Math.toRadians(90))
                .waitSeconds(0.5f)

                //park
                .setReversed(false)
                .splineTo(new Vector2d(45,6), Math.toRadians(0))
                .waitSeconds(2f);



        Actions.runBlocking(
                new SequentialAction(

                        park.build()

                )
        );
    }

}

