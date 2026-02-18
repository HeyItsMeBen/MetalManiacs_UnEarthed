package org.firstinspires.ftc.teamcode.AutoCode.Pathings.TrajectoryPaths.RedClose;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.InstantAction;
import com.acmerobotics.roadrunner.ParallelAction;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.SequentialAction;
import com.acmerobotics.roadrunner.TrajectoryActionBuilder;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.AutoCode.Pathings.PathingActions;
import org.firstinspires.ftc.teamcode.AutoCode.Pathings.PathingTrajectoriesRed;
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

@Disabled
@Config
@Autonomous(name = "Red Close", group = "Autonomous")
public class RedClose extends LinearOpMode {

    Intake intake;
    Flywheels flywheels;
    Transfer kickWheels;
    Transfer kickServo;
    Turret turret;
    AutoAim autoAim;
    OuttakeHood hood;
    Lights lights;

    IntakeController intakeController;
    FlywheelController flywheelController;
    LightsController lightsController;
    AutoAimTurretController aprilTagTurretAim;

    @Override
    public void runOpMode() {

        Pose2d startPose = new Pose2d(18, -60, Math.toRadians(0));
        MecanumDrive drive = new MecanumDrive(hardwareMap, startPose);

        intake = new Intake(hardwareMap);
        flywheels = new Flywheels(hardwareMap);
        kickWheels = new Transfer(hardwareMap);
        kickServo = new Transfer(hardwareMap);
        hood = new OuttakeHood(hardwareMap);
        turret = new Turret(hardwareMap);
        autoAim = new AutoAim(Math.toRadians(15));
        lights = new Lights(hardwareMap);

        intakeController = new IntakeController(intake, kickWheels, kickServo);
        flywheelController = new FlywheelController(flywheels, kickWheels, kickServo, intake, hood);
        lightsController = new LightsController(lights);

        aprilTagTurretAim = new AutoAimTurretController(hardwareMap);

        waitForStart();
        if (isStopRequested()) return;

        lightsController.update(false, false, "Red");

        TrajectoryActionBuilder initialMovement = drive.actionBuilder(startPose)

                .setReversed(true)
                .splineToLinearHeading(new Pose2d(18, 10, Math.toRadians(0)), Math.toRadians(90));

        Actions.runBlocking(
                new SequentialAction(
                        new ParallelAction(
                                new InstantAction(() -> intakeController.setIntakePower(0.8)),
                                new InstantAction(() -> flywheelController.powerUpToSpeed()),
                                initialMovement.build()
                        ),

                        new PathingActions.AutoAimAction(aprilTagTurretAim, lightsController, intakeController, "Red"),
                        new PathingActions.FlywheelSequenceAction(flywheelController, () -> aprilTagTurretAim.getDistanceToGoalInches(), () -> aprilTagTurretAim.isTargetFound()),

                        PathingTrajectoriesRed.PatternCollection(drive, drive.localizer.getPose(), "PPG"),

                        PathingTrajectoriesRed.firingPositionZoneOne(drive, drive.localizer.getPose()),
                        new PathingActions.AutoAimAction(aprilTagTurretAim, lightsController, intakeController, "Red"),
                        new PathingActions.FlywheelSequenceAction(flywheelController, () -> aprilTagTurretAim.getDistanceToGoalInches(), () -> aprilTagTurretAim.isTargetFound()),

                        PathingTrajectoriesRed.LongRangePark(drive, drive.localizer.getPose())
                )
        );
    }

}

