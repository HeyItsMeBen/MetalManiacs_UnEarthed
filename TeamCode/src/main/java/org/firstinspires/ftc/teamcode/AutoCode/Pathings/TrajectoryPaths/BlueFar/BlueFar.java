package org.firstinspires.ftc.teamcode.AutoCode.Pathings.TrajectoryPaths.BlueFar;

// Paths

import static org.firstinspires.ftc.teamcode.AutoCode.Pathings.PathingActions.AutoAimAction;
import static org.firstinspires.ftc.teamcode.AutoCode.Pathings.PathingActions.FlywheelSequenceAction;
import static org.firstinspires.ftc.teamcode.AutoCode.Pathings.TrajectoryPaths.BlueFar.BlueFarTrajectories.collectArtifacts;
import static org.firstinspires.ftc.teamcode.AutoCode.Pathings.TrajectoryPaths.BlueFar.BlueFarTrajectories.firingPosition;
import static org.firstinspires.ftc.teamcode.AutoCode.Pathings.TrajectoryPaths.BlueFar.BlueFarTrajectories.initialMoveToPosition;
import static org.firstinspires.ftc.teamcode.AutoCode.Pathings.TrajectoryPaths.BlueFar.BlueFarTrajectories.park;

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

        aprilTagTurretAim = new AutoAimTurretController(hardwareMap);

        int visionOutputPosition = 0;

        waitForStart();
        if (isStopRequested()) return;

        lightsController.update(false, false, "Red", ballSequence);

        Actions.runBlocking(
                new SequentialAction(
                        new ParallelAction(
                                initialMoveToPosition(drive, drive.localizer.getPose()),
                                new InstantAction(() -> intakeController.toggleIntake()),
                                new InstantAction(() -> intakeController.update()),
                                new InstantAction(() -> flywheelController.rampUp()),
                                new AutoAimAction(aprilTagTurretAim, lightsController, intakeController, "Red")
                        ),

                        new FlywheelSequenceAction(flywheelController, () -> aprilTagTurretAim.getDistanceToGoalInches(), () -> aprilTagTurretAim.isTargetFound())
                        )
        );

        // Run limelight Recognition Code

        // Limelight returns some position value:
        visionOutputPosition = 1;

        Action trajectoryActionChosen;
        if (visionOutputPosition == 1) {
            trajectoryActionChosen = collectArtifacts(drive, drive.localizer.getPose(), "Left");
        } else if (visionOutputPosition == 2) {
            trajectoryActionChosen = collectArtifacts(drive, drive.localizer.getPose(), "Middle");
        } else if (visionOutputPosition == 3) {
            trajectoryActionChosen = collectArtifacts(drive, drive.localizer.getPose(), "Right");
        } else {
            trajectoryActionChosen = collectArtifacts(drive, drive.localizer.getPose(), "Middle");
        }

        Actions.runBlocking(
                new SequentialAction(
                        trajectoryActionChosen,

                        new ParallelAction(
                                new AutoAimAction(aprilTagTurretAim, lightsController, intakeController, "Red"),
                                firingPosition(drive, drive.localizer.getPose())
                        ),
                        new FlywheelSequenceAction(flywheelController, () -> aprilTagTurretAim.getDistanceToGoalInches(), () -> aprilTagTurretAim.isTargetFound()),

                        new ParallelAction(
                                new InstantAction(() -> intakeController.toggleIntake()),
                                park(drive, drive.localizer.getPose())
                        )
                )
        );
    }

}

