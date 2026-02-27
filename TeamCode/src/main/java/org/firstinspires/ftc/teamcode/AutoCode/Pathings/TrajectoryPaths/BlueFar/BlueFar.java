package org.firstinspires.ftc.teamcode.AutoCode.Pathings.TrajectoryPaths.BlueFar;

// Paths

import static org.firstinspires.ftc.teamcode.AutoCode.Pathings.TrajectoryPaths.BlueFar.BlueFarTrajectories.collectArtifactsLeft;
import static org.firstinspires.ftc.teamcode.AutoCode.Pathings.TrajectoryPaths.BlueFar.BlueFarTrajectories.collectArtifactsMiddle;
import static org.firstinspires.ftc.teamcode.AutoCode.Pathings.TrajectoryPaths.BlueFar.BlueFarTrajectories.collectArtifactsRight;
import static org.firstinspires.ftc.teamcode.AutoCode.Pathings.TrajectoryPaths.BlueFar.BlueFarTrajectories.firingPosition;
import static org.firstinspires.ftc.teamcode.AutoCode.Pathings.TrajectoryPaths.BlueFar.BlueFarTrajectories.initialMoveToPosition;
import static org.firstinspires.ftc.teamcode.AutoCode.Pathings.TrajectoryPaths.BlueFar.BlueFarTrajectories.park;

import org.firstinspires.ftc.teamcode.AutoCode.Pathings.TrajectoryPaths.PathingActions.AimTurretAction;
import org.firstinspires.ftc.teamcode.AutoCode.Pathings.TrajectoryPaths.PathingActions.FlywheelSequenceAction;
import org.firstinspires.ftc.teamcode.AutoCode.Pathings.TrajectoryPaths.PathingActions.LimelightScanAction;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.Action;
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
import org.firstinspires.ftc.teamcode.DriveCode.PassOnFromAutoValues;
import org.firstinspires.ftc.teamcode.Hardware.AutoAim;
import org.firstinspires.ftc.teamcode.Hardware.Flywheels;
import org.firstinspires.ftc.teamcode.Hardware.Intake;
import org.firstinspires.ftc.teamcode.Hardware.Lights;
import org.firstinspires.ftc.teamcode.Hardware.OuttakeHood;
import org.firstinspires.ftc.teamcode.Hardware.Transfer;
import org.firstinspires.ftc.teamcode.Hardware.Turret;

@Config
@Autonomous(name = "Blue Far", group = "Autonomous - Red")
public class BlueFar extends LinearOpMode {

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

        Pose2d startPose = new Pose2d(12, -60, Math.toRadians(0));
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

        //aprilTagTurretAim = new AutoAimTurretController(hardwareMap);

        int visionOutputPosition = 0;

        waitForStart();
        if (isStopRequested()) return;

        lightsController.update(false, false, "Red", ballSequence);

        Actions.runBlocking(
                new SequentialAction(
                        new ParallelAction(
                                initialMoveToPosition(drive, startPose),
                                new InstantAction(() -> intakeController.toggleIntake()),
                                new InstantAction(() -> flywheelController.rampUp()),
                                new AimTurretAction(aprilTagTurretAim, lightsController, intakeController, "Red")
                        ),

                        new FlywheelSequenceAction(flywheelController, () -> aprilTagTurretAim.getDistanceToGoalInches(), () -> aprilTagTurretAim.isTargetFound())
                        )
        );

        // Run limelight Recognition Code

        // Limelight returns some position value:
        visionOutputPosition = 1;

        Action trajectoryActionChosen;
        if (visionOutputPosition == 1) {
            trajectoryActionChosen = collectArtifactsLeft(drive, drive.localizer.getPose());
        } else if (visionOutputPosition == 2) {
            trajectoryActionChosen = collectArtifactsMiddle(drive, drive.localizer.getPose());
        } else if (visionOutputPosition == 3) {
            trajectoryActionChosen = collectArtifactsRight(drive, drive.localizer.getPose());
        } else {
            trajectoryActionChosen = collectArtifactsMiddle(drive, drive.localizer.getPose());
        }

        Actions.runBlocking(
                new SequentialAction(
                        new ParallelAction(
                                new InstantAction(() -> intakeController.update()),
                                trajectoryActionChosen
                        )
                )
        );

        Actions.runBlocking(
                new SequentialAction(
                        new ParallelAction(
                                new AimTurretAction(aprilTagTurretAim, lightsController, intakeController, "Red"),
                                firingPosition(drive, drive.localizer.getPose())
                        ),
                        new FlywheelSequenceAction(flywheelController, () -> aprilTagTurretAim.getDistanceToGoalInches(), () -> aprilTagTurretAim.isTargetFound())
                )
        );

        Actions.runBlocking(
                new SequentialAction(
                        new ParallelAction(
                                new InstantAction(() -> intakeController.toggleIntake()),
                                park(drive, drive.localizer.getPose())
                        ),
                    new InstantAction(() -> intakeController.update())
                )
        );

        PassOnFromAutoValues.currentPose = drive.localizer.getPose();
        PassOnFromAutoValues.teamColor = PassOnFromAutoValues.TeamColor.BLUE;

    }

}

