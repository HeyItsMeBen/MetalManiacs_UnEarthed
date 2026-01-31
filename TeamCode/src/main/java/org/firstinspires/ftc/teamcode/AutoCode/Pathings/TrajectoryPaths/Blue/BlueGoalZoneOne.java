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
import org.firstinspires.ftc.teamcode.Hardware.Lights;
import org.firstinspires.ftc.teamcode.Hardware.Transfer;
import org.firstinspires.ftc.teamcode.Hardware.Turret;

@Config
@Autonomous(name = "[Competition] [Blue] Start at Blue Goal, Shoot from Zone One", group = "Autonomous")
public class BlueGoalZoneOne extends LinearOpMode {

    Intake intake;
    Flywheels flywheels;
    Transfer transfer;
    Turret turret;
    AutoAim autoAim;
    Lights lights;

    AprilTagTurretAim aprilTagTurretAim;
    ElapsedTime turretTimer;

    public double distanceFromGoal = 1650;

    @Override
    public void runOpMode() {

        Pose2d startPose = new Pose2d(-52, 52, Math.toRadians(225));
        MecanumDrive drive = new MecanumDrive(hardwareMap, startPose);

        intake = new Intake(hardwareMap);
        flywheels = new Flywheels(hardwareMap);
        transfer = new Transfer(hardwareMap);
        turret = new Turret(hardwareMap);
        autoAim = new AutoAim(Math.toRadians(15));
        lights = new Lights(hardwareMap);

        aprilTagTurretAim = new AprilTagTurretAim(this, turret, autoAim, true, 24, 20,lights);

        aprilTagTurretAim.init();

        waitForStart();
        if (isStopRequested()) return;

        aprilTagTurretAim.waitForStreaming();
        aprilTagTurretAim.setManualExposure(6, 250);
        lights.Light_Team_Color("Blue");

        Actions.runBlocking(
                new SequentialAction(
                        PathingTrajectoriesBlue.fire(drive, drive.localizer.getPose(), intake, flywheels, transfer, distanceFromGoal)
                )
        );

        Actions.runBlocking(
                new SequentialAction(
                        PathingTrajectoriesBlue.PatternCollection(drive, drive.localizer.getPose(), "PPG", intake)
                )
        );

//        Actions.runBlocking(
//                new SequentialAction(
//                        PathingTrajectoriesBlue.openChannel(drive, drive.localizer.getPose(), intake)
//                )
//        );

        Actions.runBlocking(
                new SequentialAction(
                        PathingTrajectoriesBlue.fire(drive, drive.localizer.getPose(), intake, flywheels, transfer, distanceFromGoal)
                )
        );

        Actions.runBlocking(
                new SequentialAction(
                        PathingTrajectoriesBlue.PatternCollection(drive, drive.localizer.getPose(), "PGP", intake)
                )
        );

        Actions.runBlocking(
                new SequentialAction(
                        PathingTrajectoriesBlue.fire(drive, drive.localizer.getPose(), intake, flywheels, transfer, distanceFromGoal)
                )
        );

        Actions.runBlocking(
                new SequentialAction(
                        PathingTrajectoriesBlue.PatternCollection(drive, drive.localizer.getPose(), "GPP", intake)
                )
        );

        Actions.runBlocking(
                new SequentialAction(
                        PathingTrajectoriesBlue.fire(drive, drive.localizer.getPose(), intake, flywheels, transfer, distanceFromGoal)
                )
        );

        Actions.runBlocking(
                new SequentialAction(
                        PathingTrajectoriesBlue.park(drive, drive.localizer.getPose(), flywheels, intake)
                )
        );
    }
}