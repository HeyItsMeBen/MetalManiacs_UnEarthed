package org.firstinspires.ftc.teamcode.AutoCode.Pathings.TrajectoryPaths.Blue;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.SequentialAction;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.AutoCode.Pathings.AprilTagTurretAim;
import org.firstinspires.ftc.teamcode.AutoCode.Pathings.PathingTrajectoriesBlue;
import org.firstinspires.ftc.teamcode.AutoCode.Roadrunner.MecanumDrive;
import org.firstinspires.ftc.teamcode.Hardware.AutoAim;
import org.firstinspires.ftc.teamcode.Hardware.Flywheels;
import org.firstinspires.ftc.teamcode.Hardware.Intake;
import org.firstinspires.ftc.teamcode.Hardware.Transfer;
import org.firstinspires.ftc.teamcode.Hardware.Turret;

@Config
@Autonomous(name = "[Competition] [Blue] Start at Blue Wall, Shoot from Zone One", group = "Autonomous")
public class BlueWallZoneOne extends LinearOpMode {

    Intake intake;
    Flywheels flywheels;
    Transfer transfer;
    Turret turret;
    AutoAim autoAim;

    AprilTagTurretAim aprilTagTurretAim;
    ElapsedTime turretTimer;

    @Override
    public void runOpMode() {

        Pose2d startPose = new Pose2d(-15, -60, Math.toRadians(180)); // x, y, heading in radians
        MecanumDrive drive = new MecanumDrive(hardwareMap, startPose);

        intake = new Intake(hardwareMap);
        flywheels = new Flywheels(hardwareMap);
        transfer = new Transfer(hardwareMap);
        turret = new Turret(hardwareMap);
        autoAim = new AutoAim(Math.toRadians(15));

        aprilTagTurretAim = new AprilTagTurretAim(this, turret, autoAim, true, 24, 20);

        aprilTagTurretAim.init();

        waitForStart();
        if (isStopRequested()) return;

        aprilTagTurretAim.waitForStreaming();
        aprilTagTurretAim.setManualExposure(6, 250);

        Actions.runBlocking(
                new SequentialAction(
                        PathingTrajectoriesBlue.initialMovementFromWallZoneOne(drive, startPose, intake, flywheels, transfer, telemetry)
                )
        );

        turretTimer = new ElapsedTime();
        while (opModeIsActive() && turretTimer.milliseconds() < 1000) {
            aprilTagTurretAim.update();
        }
        aprilTagTurretAim.stopTurret();

        Actions.runBlocking(
                PathingTrajectoriesBlue.firstLaunch(drive, drive.localizer.getPose(), intake, flywheels, transfer, telemetry)
        );

        Actions.runBlocking(
                new SequentialAction(
                        PathingTrajectoriesBlue.PatternCollection(drive, drive.localizer.getPose(), "PPG", intake)
                )
        );

        Actions.runBlocking(
                new SequentialAction(
                        PathingTrajectoriesBlue.firingPositionZoneOne(drive, drive.localizer.getPose(), intake, flywheels, transfer, telemetry)
                )
        );

        Actions.runBlocking(
                new SequentialAction(
                        PathingTrajectoriesBlue.PatternCollection(drive, drive.localizer.getPose(), "PGP", intake)
                )
        );

        Actions.runBlocking(
                new SequentialAction(
                        PathingTrajectoriesBlue.firingPositionZoneOne(drive, drive.localizer.getPose(), intake, flywheels, transfer, telemetry)
                )
        );

        Actions.runBlocking(
                new SequentialAction(
                        PathingTrajectoriesBlue.PatternCollection(drive, drive.localizer.getPose(), "GPP", intake)
                )
        );

        Actions.runBlocking(
                new SequentialAction(
                        PathingTrajectoriesBlue.firingPositionZoneOne(drive, drive.localizer.getPose(), intake, flywheels, transfer, telemetry)
                )
        );

        Actions.runBlocking(
                new SequentialAction(
                        PathingTrajectoriesBlue.park(drive, drive.localizer.getPose(), flywheels, intake)
                )
        );
    }
}
