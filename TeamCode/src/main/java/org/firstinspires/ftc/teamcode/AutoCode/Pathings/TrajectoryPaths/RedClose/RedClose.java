package org.firstinspires.ftc.teamcode.AutoCode.Pathings.TrajectoryPaths.RedClose;

// Paths

import static org.firstinspires.ftc.teamcode.AutoCode.Pathings.TrajectoryPaths.RedClose.RedCloseTrajectories.collectPatternPPG;
import static org.firstinspires.ftc.teamcode.AutoCode.Pathings.TrajectoryPaths.RedClose.RedCloseTrajectories.collectPatternPGP;
import static org.firstinspires.ftc.teamcode.AutoCode.Pathings.TrajectoryPaths.RedClose.RedCloseTrajectories.collectPatternGPP;
import static org.firstinspires.ftc.teamcode.AutoCode.Pathings.TrajectoryPaths.RedClose.RedCloseTrajectories.firingPosition;
import static org.firstinspires.ftc.teamcode.AutoCode.Pathings.TrajectoryPaths.RedClose.RedCloseTrajectories.initialMoveToPosition;
import static org.firstinspires.ftc.teamcode.AutoCode.Pathings.TrajectoryPaths.RedClose.RedCloseTrajectories.openChannel;
import static org.firstinspires.ftc.teamcode.AutoCode.Pathings.TrajectoryPaths.RedClose.RedCloseTrajectories.park;

import org.firstinspires.ftc.teamcode.AutoCode.Pathings.TrajectoryPaths.PathingActions.AimTurretAction;
import org.firstinspires.ftc.teamcode.AutoCode.Pathings.TrajectoryPaths.PathingActions.FlywheelSequenceAction;
import org.firstinspires.ftc.teamcode.AutoCode.Pathings.TrajectoryPaths.PathingActions.LimelightScanAction;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.InstantAction;
import com.acmerobotics.roadrunner.ParallelAction;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.SequentialAction;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.AutoCode.Pathings.TrajectoryPaths.PathingActions;
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
@Autonomous(name = "Red Close", group = "Autonomous - Red")
public class RedClose extends LinearOpMode {

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

    // Background intake updater
    private Thread intakeUpdaterThread;
    private volatile boolean intakeUpdaterRunning = false;

    public String ballSequence = "XXX";


    @Override
    public void runOpMode() {

        //Pose2d startPose = new Pose2d(52, 52, Math.toRadians(0));
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
        flywheelController = new FlywheelController(flywheels, transferDrum, transferKick, intake, hood, intakeController);
        lightsController = new LightsController(lights);

        autoAimController = new AutoAimTurretController(hardwareMap, startPose,"Red"); // May crop out, takes too long to initialize

        waitForStart();
        if (isStopRequested()) return;

        // Start background updater thread for intake
        intakeUpdaterRunning = true;
        intakeUpdaterThread = new Thread(() -> {
            try {
                while (opModeIsActive() && intakeUpdaterRunning && !isStopRequested()) {
                    intakeController.update(gamepad1.touchpad, gamepad1.ps);
                    Thread.sleep(20);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "Intake-Updater-Thread");
        intakeUpdaterThread.setDaemon(true);
        intakeUpdaterThread.start();

        lightsController.update(false, false, "Red", ballSequence);

        Actions.runBlocking(
                new SequentialAction(
                        new InstantAction(() -> intakeController.isJammed = true),
                        new InstantAction(() -> intakeController.toggleIntake()),
                        new ParallelAction(
                                new InstantAction(() -> flywheelController.rampUp()),
                                initialMoveToPosition(drive, startPose)
                        )
                )
        );

        double autoAimStartTime=System.currentTimeMillis();
        while (opModeIsActive() && System.currentTimeMillis()<autoAimStartTime+1000){
            autoAimController.updateWithTimeout(false, false);
        }
        autoAimController.setTurretPower(0);
        lightsController.update(autoAimController.isTargetFound(), intakeController.isIntakeRunning(), "Red", ballSequence);

        Actions.runBlocking(
                new SequentialAction(
                        new PathingActions.FlywheelSequenceActionDirect(flywheels, intake, transferDrum, transferKick, () -> autoAimController.getDistanceToGoalInches(), () -> autoAimController.isTargetFound()),
//                        new InstantAction(() -> intakeController.update(gamepad1.touchpad, gamepad1.ps)),
                        collectPatternPGP(drive, drive.localizer.getPose())
                )
        );

        Actions.runBlocking(
                new SequentialAction(
                        firingPosition(drive, drive.localizer.getPose()),
                        new PathingActions.FlywheelSequenceActionDirect(flywheels, intake, transferDrum, transferKick, () -> autoAimController.getDistanceToGoalInches(), () -> autoAimController.isTargetFound())
                )
        );

        Actions.runBlocking(
                new SequentialAction(
//                        new InstantAction(() -> intakeController.toggleIntake()),
                        collectPatternPPG(drive, drive.localizer.getPose())
                )
        );

        Actions.runBlocking(
                new SequentialAction(
                        firingPosition(drive, drive.localizer.getPose()),
                        new PathingActions.FlywheelAutoAction(flywheelController, () -> autoAimController.getDistanceToGoalInches(), () -> autoAimController.isTargetFound())
                )
        );

        Actions.runBlocking(
                new SequentialAction(
                        openChannel(drive, drive.localizer.getPose()),
                        new PathingActions.WaitAction(5000)
                )
        );

        Actions.runBlocking(
                new SequentialAction(
                        firingPosition(drive, drive.localizer.getPose()),
                        new PathingActions.FlywheelSequenceActionDirect(flywheels, intake, transferDrum, transferKick, () -> autoAimController.getDistanceToGoalInches(), () -> autoAimController.isTargetFound())
                )
        );

       Actions.runBlocking(
               new SequentialAction(
//                       new InstantAction(() -> intakeController.toggleIntake()),
                       collectPatternGPP(drive, drive.localizer.getPose())
                )
       );

        Actions.runBlocking(
                new SequentialAction(
                        new ParallelAction(
//                               new InstantAction(() -> intakeController.toggleIntake()),
                                firingPosition(drive, drive.localizer.getPose())
                        ),
                        new FlywheelSequenceAction(flywheelController, () -> autoAimController.getDistanceToGoalInches(), () -> autoAimController.isTargetFound())
               )
        );

//        double autoAimFinishTime=System.currentTimeMillis();
//        while (opModeIsActive() && System.currentTimeMillis()<autoAimFinishTime+1000){
//           autoAimController.turnToCenter();
//        }
        autoAimController.turnToCenter();

        long centerStart = System.currentTimeMillis();

        while (
                opModeIsActive()
                        && !turret.isAtTargetPosition(750)
                        && System.currentTimeMillis() - centerStart < 3000
        ) {
            idle();
        }
        turret.stop();

        Actions.runBlocking(
                new SequentialAction(
                        new ParallelAction(
//                                new InstantAction(() -> intakeController.update(gamepad1.touchpad, gamepad1.ps)),
                                park(drive, drive.localizer.getPose())
                        )
                )
        );

        // Stop the intake updater thread cleanly before exiting
        intakeUpdaterRunning = false;
        if (intakeUpdaterThread != null && intakeUpdaterThread.isAlive()) {
            try {
                intakeUpdaterThread.interrupt();
                intakeUpdaterThread.join(250);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        PassOnFromAutoValues.currentPose = drive.localizer.getPose();
        PassOnFromAutoValues.teamColor = PassOnFromAutoValues.TeamColor.RED;

    }

}
