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
@Autonomous(name = "Red Close Working", group = "Autonomous - Any")
public class RedCloseWorking extends LinearOpMode {

    Intake intake;
    Flywheels flywheels;
    Transfer transferDrum;
    Transfer transferKick;
    Turret turret;
    AutoAim autoAim;
    OuttakeHood hood;
    Lights lights;

    IntakeController intakeController;
    FlywheelController flywheelController;
    LightsController lightsController;
    AutoAimTurretController aprilTagTurretAim;
    public String ballSequence = "XXX";


    @Override
    public void runOpMode() {

        Pose2d startPose = new Pose2d(52, 52, Math.toRadians(40));
        MecanumDrive drive = new MecanumDrive(hardwareMap, startPose);

        intake = new Intake(hardwareMap);
        flywheels = new Flywheels(hardwareMap);
        turret = new Turret(hardwareMap);
        autoAim = new AutoAim(Math.toRadians(15));
        transferDrum = new Transfer(hardwareMap);
        transferKick = new Transfer(hardwareMap);
        hood = new OuttakeHood(hardwareMap);
        lights = new Lights(hardwareMap);
//
        intakeController = new IntakeController(intake, transferDrum, transferKick);
        flywheelController = new FlywheelController(flywheels, transferDrum, transferKick, intake, hood, intakeController);
        lightsController = new LightsController(lights);
//
        aprilTagTurretAim = new AutoAimTurretController(hardwareMap, startPose,"Red");

        waitForStart();
        if (isStopRequested()) return;

        lightsController.update(false, false, "Red",ballSequence);

        TrajectoryActionBuilder initialMoveToPosition = drive.actionBuilder(startPose)
                .setReversed(true)
                .splineToSplineHeading(new Pose2d(15, 10, Math.toRadians(0)), Math.toRadians(270));

        TrajectoryActionBuilder collectPattern = initialMoveToPosition.endTrajectory().fresh()
                .setReversed(false)
                .splineToSplineHeading(new Pose2d(50, 12, Math.toRadians(0)), Math.toRadians(0));

        TrajectoryActionBuilder fireFirstSet = collectPattern.endTrajectory().fresh()
                .setReversed(true)
                .splineToConstantHeading(new Vector2d(15, 10), Math.toRadians(90));

        TrajectoryActionBuilder openChannel = fireFirstSet.endTrajectory().fresh()
                .setReversed(false)
                .splineToSplineHeading(new Pose2d(58, -10, Math.toRadians(20)), Math.toRadians(0));

        Actions.runBlocking(
                new SequentialAction(

                        initialMoveToPosition.build(),
                        collectPattern.build(),
                        fireFirstSet.build(),
                        openChannel.build()

                )

        );
    }

}

