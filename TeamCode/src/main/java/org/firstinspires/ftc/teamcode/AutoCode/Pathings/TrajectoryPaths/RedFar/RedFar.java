package org.firstinspires.ftc.teamcode.AutoCode.Pathings.TrajectoryPaths.RedFar;

// Paths
import static org.firstinspires.ftc.teamcode.AutoCode.Pathings.TrajectoryPaths.RedFar.RedFarTrajectories.collectArtifactsLeft;
import static org.firstinspires.ftc.teamcode.AutoCode.Pathings.TrajectoryPaths.RedFar.RedFarTrajectories.collectArtifactsMiddle;
import static org.firstinspires.ftc.teamcode.AutoCode.Pathings.TrajectoryPaths.RedFar.RedFarTrajectories.collectArtifactsRight;
import static org.firstinspires.ftc.teamcode.AutoCode.Pathings.TrajectoryPaths.RedFar.RedFarTrajectories.moveToScanPosition;
import static org.firstinspires.ftc.teamcode.AutoCode.Pathings.TrajectoryPaths.RedFar.RedFarTrajectories.firingPosition;
import static org.firstinspires.ftc.teamcode.AutoCode.Pathings.TrajectoryPaths.RedFar.RedFarTrajectories.initialMoveToPosition;
import static org.firstinspires.ftc.teamcode.AutoCode.Pathings.TrajectoryPaths.RedFar.RedFarTrajectories.park;

import org.firstinspires.ftc.teamcode.AutoCode.Pathings.TrajectoryPaths.PathingActions;
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
import com.qualcomm.robotcore.util.ElapsedTime;

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
@Autonomous(name = "Red Far", group = "Autonomous - Red")
public class RedFar extends LinearOpMode {

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
    AutoAimTurretController autoAimController;

    public String ballSequence = "XXX";


    @Override
    public void runOpMode() throws InterruptedException {

        Pose2d startPose = new Pose2d(12, -60, Math.toRadians(0));
        MecanumDrive drive = new MecanumDrive(hardwareMap, startPose);

        Pose2d shootingPose =  new Pose2d(12, -45, Math.toRadians(0));

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

        autoAimController = new AutoAimTurretController(hardwareMap, startPose,"Red"); // May crop out, takes too long to initialize

        waitForStart();
        if (isStopRequested()) return;

        lightsController.update(false, false, "Red", ballSequence);

        Actions.runBlocking(
                new SequentialAction(
                        new ParallelAction(
                                initialMoveToPosition(drive, startPose),
                                new InstantAction(() -> intakeController.toggleIntake()),
                                new InstantAction(() -> flywheelController.rampUp())
                        )
                )
        );

        double autoAimStartTime=System.currentTimeMillis();
        while (System.currentTimeMillis()<autoAimStartTime+1000){
            autoAimController.updateWithTimeout(false, false);
        }
        autoAimController.setTurretPower(0);
        lightsController.update(autoAimController.isTargetFound(), intakeController.isIntakeRunning(), "Red", ballSequence);

        // Repeat as many times as necessary

        Actions.runBlocking(
                new SequentialAction(
                        new PathingActions.FlywheelSequenceActionDirect(flywheels, intake, transferDrum, transferKick, () -> autoAimController.getDistanceToGoalInches(), () -> autoAimController.isTargetFound()),
                        new InstantAction(() -> autoAimController.closeWebcam()),
                        new ParallelAction(
                                new InstantAction(() -> intakeController.update()),
                                moveToScanPosition(drive, drive.localizer.getPose())
                        )
                )
        );

        Actions.runBlocking(
                new SequentialAction(
                        collectArtifactsRight(drive, drive.localizer.getPose())
                        //new LimelightScanAction(drive, true)
                )
        );

        Actions.runBlocking(
                new SequentialAction(
                        firingPosition(drive, drive.localizer.getPose()),
                        new PathingActions.FlywheelSequenceActionDirect(flywheels, intake, transferDrum, transferKick, () -> autoAimController.getDistanceToGoalInches(), () -> autoAimController.isTargetFound())
                )
        );

        // Repeat as many times as necessary

        Actions.runBlocking(
                new SequentialAction(
                        new ParallelAction(
                                new InstantAction(() -> autoAimController.closeWebcam()),
                                moveToScanPosition(drive, drive.localizer.getPose())
                        )
                )
        );

        Actions.runBlocking(
                new SequentialAction(
                        collectArtifactsLeft(drive, drive.localizer.getPose())
                )
        );

        Actions.runBlocking(
                new SequentialAction(
                        firingPosition(drive, drive.localizer.getPose()),
                        new PathingActions.FlywheelSequenceActionDirect(flywheels, intake, transferDrum, transferKick, () -> autoAimController.getDistanceToGoalInches(), () -> autoAimController.isTargetFound())
                )
        );

        // Repeat as many times as necessary

        //Issues
//        double autoAimFinishTime=System.currentTimeMillis();
//        while (System.currentTimeMillis()<autoAimFinishTime+250){
//            autoAimController.turnToCenter();
//        }
        //Issues

        Actions.runBlocking(
                new SequentialAction(
                        new ParallelAction(
                                new InstantAction(() -> intakeController.toggleIntake()),
                                park(drive, drive.localizer.getPose())
                        )
                )
        );

        PassOnFromAutoValues.currentPose = drive.localizer.getPose();
        PassOnFromAutoValues.teamColor = PassOnFromAutoValues.TeamColor.RED;

    }
}
