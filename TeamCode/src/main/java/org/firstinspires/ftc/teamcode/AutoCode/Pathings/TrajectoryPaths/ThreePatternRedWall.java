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
import org.firstinspires.ftc.teamcode.Old_Code.Intake;

@Config
@Autonomous(name = "Three Pattern Red Wall", group = "Autonomous")
public class ThreePatternRedWall extends LinearOpMode {

    Intake intake;
    Flywheels flywheels;

    @Override
    public void runOpMode() {
        Pose2d startPose = new Pose2d(15, -60, Math.toRadians(270)); // x, y, heading in radians
        MecanumDrive drive = new MecanumDrive(hardwareMap, startPose);

        intake = new Intake(hardwareMap);
        flywheels = new Flywheels(hardwareMap);

        String PatternOne = "PPG";
        String PatternTwo = "PGP";
        String PatternThree = "GPP";

        waitForStart();

        Actions.runBlocking(
                new SequentialAction(

                        PathingTrajectories.initialFiringFromWall(drive, startPose, flywheels),

                        PathingTrajectories.PatternCollection(drive, drive.localizer.getPose(), PatternOne, intake),

                        PathingTrajectories.firingPosition(drive, drive.localizer.getPose(), flywheels),

                        PathingTrajectories.PatternCollection(drive, drive.localizer.getPose(), PatternTwo, intake),

                        PathingTrajectories.firingPosition(drive, drive.localizer.getPose(), flywheels),

                        PathingTrajectories.PatternCollection(drive, drive.localizer.getPose(), PatternThree, intake),

                        PathingTrajectories.firingPosition(drive, drive.localizer.getPose(), flywheels),

                        PathingTrajectories.park(drive, drive.localizer.getPose(), flywheels, intake)


                )

            );
    }
}
