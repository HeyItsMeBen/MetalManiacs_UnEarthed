package org.firstinspires.ftc.teamcode.AutoCode.Pathings.TrajectoryPaths.Red;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.SequentialAction;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.AutoCode.Pathings.PathingTrajectoriesRed;
import org.firstinspires.ftc.teamcode.AutoCode.Roadrunner.MecanumDrive;
import org.firstinspires.ftc.teamcode.Hardware.Flywheels;
import org.firstinspires.ftc.teamcode.Hardware.Intake;
import org.firstinspires.ftc.teamcode.Hardware.Transfer;
import org.firstinspires.ftc.teamcode.Hardware.Turret;

@Config
@Autonomous(name = "[Competition] [Red] Start at Red Wall, Shoot from Zone One", group = "Autonomous")
public class RedWallZoneOne extends LinearOpMode {

    Intake intake;
    Flywheels flywheels;
    Transfer transfer;
    Turret turret;

    @Override
    public void runOpMode() {

        Pose2d startPose = new Pose2d(15, -60, Math.toRadians(270)); // x, y, heading in radians
        MecanumDrive drive = new MecanumDrive(hardwareMap, startPose);

        intake = new Intake(hardwareMap);
        flywheels = new Flywheels(hardwareMap);
        transfer = new Transfer(hardwareMap);
        turret = new Turret(hardwareMap);

        waitForStart();

        Actions.runBlocking(
                new SequentialAction(
                        PathingTrajectoriesRed.initialFiringFromWallZoneOne(drive, startPose, intake, flywheels, transfer, turret, telemetry)
                )
        );

        Actions.runBlocking(
                new SequentialAction(
                        PathingTrajectoriesRed.PatternCollection(drive, drive.localizer.getPose(), "PPG", intake)
                )
        );

        Actions.runBlocking(
                new SequentialAction(
                        PathingTrajectoriesRed.openChannel(drive, drive.localizer.getPose(), intake)
                )
        );

        Actions.runBlocking(
                new SequentialAction(
                        PathingTrajectoriesRed.firingPositionZoneOne(drive, drive.localizer.getPose(), intake, flywheels, transfer, telemetry)
                )
        );

        Actions.runBlocking(
                new SequentialAction(
                        PathingTrajectoriesRed.PatternCollection(drive, drive.localizer.getPose(), "PGP", intake)
                )
        );

        Actions.runBlocking(
                new SequentialAction(
                        PathingTrajectoriesRed.firingPositionZoneOne(drive, drive.localizer.getPose(), intake, flywheels, transfer, telemetry)
                )
        );

        Actions.runBlocking(
                new SequentialAction(
                        PathingTrajectoriesRed.PatternCollection(drive, drive.localizer.getPose(), "GPP", intake)
                )
        );

        Actions.runBlocking(
                new SequentialAction(
                        PathingTrajectoriesRed.firingPositionZoneOne(drive, drive.localizer.getPose(), intake, flywheels, transfer, telemetry)
                )
        );

        Actions.runBlocking(
                new SequentialAction(
                        PathingTrajectoriesRed.park(drive, drive.localizer.getPose(), flywheels, intake, turret)
                )
        );
    }
}
