package org.firstinspires.ftc.teamcode.AutoCode.Pathings.TrajectoryPaths.BlueClose;

// Paths

import static org.firstinspires.ftc.teamcode.AutoCode.Pathings.PathingActions.AutoAimAction;
import static org.firstinspires.ftc.teamcode.AutoCode.Pathings.PathingActions.FlywheelSequenceAction;
import static org.firstinspires.ftc.teamcode.AutoCode.Pathings.TrajectoryPaths.BlueClose.BlueCloseTrajectories.collectPattern;
import static org.firstinspires.ftc.teamcode.AutoCode.Pathings.TrajectoryPaths.BlueClose.BlueCloseTrajectories.firingPosition;
import static org.firstinspires.ftc.teamcode.AutoCode.Pathings.TrajectoryPaths.BlueClose.BlueCloseTrajectories.initialMoveToPosition;
import static org.firstinspires.ftc.teamcode.AutoCode.Pathings.TrajectoryPaths.BlueClose.BlueCloseTrajectories.openChannel;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.InstantAction;
import com.acmerobotics.roadrunner.ParallelAction;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.SequentialAction;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
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
@Autonomous(name = "Blue Close", group = "Autonomous - Blue")
public class BlueClose extends LinearOpMode {

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

        intakeController = new IntakeController(intake, transferDrum, transferKick);
        flywheelController = new FlywheelController(flywheels, transferDrum, transferKick, intake, hood);
        lightsController = new LightsController(lights);

        aprilTagTurretAim = new AutoAimTurretController(hardwareMap);

        waitForStart();
        if (isStopRequested()) return;

        lightsController.update(false, false, "Red");

        Actions.runBlocking(
                new SequentialAction(

                        new ParallelAction(
                                new InstantAction(() -> intakeController.toggleIntake()),
                                new InstantAction(() -> intakeController.update()),
                                new InstantAction(() -> flywheelController.rampUp()),
                                initialMoveToPosition(drive, drive.localizer.getPose())
                        ),

                        new AutoAimAction(aprilTagTurretAim, lightsController, intakeController, "Red"),
                        new FlywheelSequenceAction(flywheelController, () -> aprilTagTurretAim.getDistanceToGoalInches(), () -> aprilTagTurretAim.isTargetFound()),

                        collectPattern(drive, drive.localizer.getPose(), "PGP"),

                        new ParallelAction(
                                new AutoAimAction(aprilTagTurretAim, lightsController, intakeController, "Red"),
                                firingPosition(drive, drive.localizer.getPose())
                        ),
                        new FlywheelSequenceAction(flywheelController, () -> aprilTagTurretAim.getDistanceToGoalInches(), () -> aprilTagTurretAim.isTargetFound()),

                        openChannel(drive, drive.localizer.getPose()) //,

//                        new ParallelAction(
//                                new AutoAimAction(aprilTagTurretAim, lightsController, intakeController, "Red"),
//                                firingPosition(drive, drive.localizer.getPose())
//                        ),
//                        new FlywheelSequenceAction(flywheelController, () -> aprilTagTurretAim.getDistanceToGoalInches(), () -> aprilTagTurretAim.isTargetFound()),
//
//                        collectPattern(drive, drive.localizer.getPose(), "PPG"),
//
//                        new ParallelAction(
//                                new AutoAimAction(aprilTagTurretAim, lightsController, intakeController, "Red"),
//                                firingPosition(drive, drive.localizer.getPose())
//                        ),
//                        new FlywheelSequenceAction(flywheelController, () -> aprilTagTurretAim.getDistanceToGoalInches(), () -> aprilTagTurretAim.isTargetFound()),
//
//                        collectPattern(drive, drive.localizer.getPose(), "GPP"),
//
//                        new ParallelAction(
//                                new AutoAimAction(aprilTagTurretAim, lightsController, intakeController, "Red"),
//                                firingPosition(drive, drive.localizer.getPose())
//                        ),
//                        new FlywheelSequenceAction(flywheelController, () -> aprilTagTurretAim.getDistanceToGoalInches(), () -> aprilTagTurretAim.isTargetFound()),
//
//                        new ParallelAction(
//                                new InstantAction(() -> intakeController.toggleIntake()),
//                                park(drive, drive.localizer.getPose())
//                        )
                )
        );
    }

}

