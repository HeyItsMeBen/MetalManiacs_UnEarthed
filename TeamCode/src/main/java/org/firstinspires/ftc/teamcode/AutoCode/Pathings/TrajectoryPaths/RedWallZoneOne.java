package org.firstinspires.ftc.teamcode.AutoCode.Pathings.TrajectoryPaths;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.SequentialAction;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.AutoCode.Pathings.PathingTrajectories;

import org.firstinspires.ftc.teamcode.AutoCode.Roadrunner.MecanumDrive;
import org.firstinspires.ftc.teamcode.Hardware.Flywheels;
import org.firstinspires.ftc.teamcode.Hardware.Intake;
import org.firstinspires.ftc.teamcode.Hardware.Transfer;

@Config
@Autonomous(name = "[Red] Start at Red Wall, Shoot from Zone One", group = "Autonomous")
public class RedWallZoneOne extends LinearOpMode {

    Intake intake;
    Flywheels flywheels;

    Transfer belt;

    @Override
    public void runOpMode() {
        Pose2d startPose = new Pose2d(15, -60, Math.toRadians(270)); // x, y, heading in radians
        MecanumDrive drive = new MecanumDrive(hardwareMap, startPose);

        intake = new Intake(hardwareMap);
        flywheels = new Flywheels(hardwareMap);
        belt = new Transfer(hardwareMap);

        waitForStart();

        Actions.runBlocking(
                new SequentialAction(

                        PathingTrajectories.initialFiringFromWallZoneOne(drive, startPose, flywheels),

                        PathingTrajectories.PatternCollection(drive, drive.localizer.getPose(), "PPG", intake, belt),

                        PathingTrajectories.openChannel(drive, drive.localizer.getPose(), intake, belt),

                        PathingTrajectories.firingPositionZoneOne(drive, drive.localizer.getPose(), flywheels),

                        PathingTrajectories.PatternCollection(drive, drive.localizer.getPose(), "PGP", intake, belt),

                        PathingTrajectories.firingPositionZoneOne(drive, drive.localizer.getPose(), flywheels),

                        PathingTrajectories.PatternCollection(drive, drive.localizer.getPose(), "GPP", intake, belt),

                        PathingTrajectories.firingPositionZoneOne(drive, drive.localizer.getPose(), flywheels),

                        PathingTrajectories.park(drive, drive.localizer.getPose(), flywheels, intake, belt)


                )

            );
    }
}
