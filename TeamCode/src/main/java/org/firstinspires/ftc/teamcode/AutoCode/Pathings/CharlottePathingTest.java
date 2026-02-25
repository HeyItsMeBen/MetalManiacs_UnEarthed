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
@Disabled
@Autonomous(name = "Debug pathing", group = "Autonomous - Any")
public class CharlottePathingTest extends LinearOpMode {

    Lights lights;

    LightsController lightsController;
    public String ballSequence = "XXX";

    @Override
    public void runOpMode() {

        Pose2d startPose = new Pose2d(0, 0, Math.toRadians(0));
        MecanumDrive drive = new MecanumDrive(hardwareMap, startPose);

        lights = new Lights(hardwareMap);
        lightsController = new LightsController(lights);

        waitForStart();
        if (isStopRequested()) return;

        lightsController.update(false, false, "Red",ballSequence);

        TrajectoryActionBuilder park = drive.actionBuilder(startPose)


                .setReversed(true)
                .strafeTo(new Vector2d((drive.localizer.getPose().position.x + 25), drive.localizer.getPose().position.y))
                .strafeTo(new Vector2d((drive.localizer.getPose().position.x + 25 ), drive.localizer.getPose().position.y + 25))
                .strafeTo(new Vector2d((drive.localizer.getPose().position.x ), drive.localizer.getPose().position.y + 25))
                .strafeTo(new Vector2d((drive.localizer.getPose().position.x ), drive.localizer.getPose().position.y));

        Actions.runBlocking(
                new SequentialAction(

                        park.build()

                )
        );
    }

}

