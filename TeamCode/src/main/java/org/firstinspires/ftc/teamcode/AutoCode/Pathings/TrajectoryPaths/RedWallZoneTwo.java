package org.firstinspires.ftc.teamcode.AutoCode.Pathings.TrajectoryPaths;
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
@Autonomous(name = "[Competition] [Red] Start at Red Wall, Shoot from Zone Two", group = "Autonomous")
public class RedWallZoneTwo extends LinearOpMode {

    Intake intake;
    Flywheels flywheels;
    Transfer belt;
    Transfer trapdoor;
    Turret turret;

    @Override
    public void runOpMode() {

        Pose2d startPose = new Pose2d(15, -60, Math.toRadians(270));
        MecanumDrive drive = new MecanumDrive(hardwareMap, startPose);

        intake = new Intake(hardwareMap);
        flywheels = new Flywheels(hardwareMap);
        belt = new Transfer(hardwareMap);
        trapdoor = new Transfer(hardwareMap);
        turret = new Turret(hardwareMap);

        waitForStart();

        Actions.runBlocking(
                new SequentialAction(
                        PathingTrajectoriesRed.initialFiringFromWallZoneTwo(drive, startPose, intake, flywheels, belt, trapdoor, turret)
                )
        );

        Actions.runBlocking(
                new SequentialAction(
                        PathingTrajectoriesRed.collectArtifactsZoneTwo(drive, startPose, intake, belt)
                )
        );

        Actions.runBlocking(
                new SequentialAction(
                        PathingTrajectoriesRed.firingPositionZoneTwo(drive, startPose, intake, flywheels, belt, trapdoor)
                )
        );

        Actions.runBlocking(
                new SequentialAction(
                        PathingTrajectoriesRed.park(drive, drive.localizer.getPose(), flywheels, intake, belt, turret)
                )
        );
    }
}