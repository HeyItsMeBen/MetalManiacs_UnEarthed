package org.firstinspires.ftc.teamcode.AutoCode.Pathings.TrajectoryPaths.Red;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.SequentialAction;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.AutoCode.Pathings.PathingActions;
import org.firstinspires.ftc.teamcode.AutoCode.Pathings.PathingTrajectoriesRed;
import org.firstinspires.ftc.teamcode.AutoCode.Roadrunner.MecanumDrive;
import org.firstinspires.ftc.teamcode.Hardware.AutoAim;
import org.firstinspires.ftc.teamcode.Hardware.Flywheels;
import org.firstinspires.ftc.teamcode.Hardware.Intake;
import org.firstinspires.ftc.teamcode.Hardware.Lights;
import org.firstinspires.ftc.teamcode.Hardware.Transfer;
import org.firstinspires.ftc.teamcode.Hardware.Turret;
import org.firstinspires.ftc.teamcode.AutoCode.Pathings.AprilTagTurretAim;

@Config
@Autonomous(name = "[Competition] [Red] Start at Red Wall, Shoot from Zone One", group = "Autonomous")
public class RedWallZoneOne extends LinearOpMode {

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

        Pose2d startPose = new Pose2d(18, -60, Math.toRadians(0));
        MecanumDrive drive = new MecanumDrive(hardwareMap, startPose);

        intake = new Intake(hardwareMap);
        flywheels = new Flywheels(hardwareMap);
        transfer = new Transfer(hardwareMap);
        turret = new Turret(hardwareMap);
        autoAim = new AutoAim(Math.toRadians(15));
        lights = new Lights(hardwareMap);

        aprilTagTurretAim = new AprilTagTurretAim(this, turret, autoAim, true, 24, 20, lights);

        aprilTagTurretAim.init();

        waitForStart();
        if (isStopRequested()) return;

        aprilTagTurretAim.waitForStreaming();
        aprilTagTurretAim.setManualExposure(6, 250);
        lights.Light_Team_Color("Red");

        Actions.runBlocking(
                new SequentialAction(
                        PathingTrajectoriesRed.initialMovementFromWallZoneOne(
                                drive, startPose, intake, flywheels
                        )
                )
        );

        turretTimer = new ElapsedTime();
        while (opModeIsActive() && turretTimer.milliseconds() < 1000) {
            distanceFromGoal = aprilTagTurretAim.update();
            if (distanceFromGoal == 0) {
                distanceFromGoal = 1750;
            }
        }
        aprilTagTurretAim.stopTurret();

        Actions.runBlocking(
                new SequentialAction(

                        PathingTrajectoriesRed.fire(intake, flywheels, transfer, distanceFromGoal),

                        PathingTrajectoriesRed.PatternCollection(drive, drive.localizer.getPose(), "PPG", intake),

                        PathingTrajectoriesRed.firingPositionZoneOne(drive, drive.localizer.getPose(), intake, flywheels),

                        PathingTrajectoriesRed.fire(intake, flywheels, transfer, distanceFromGoal),

                        PathingTrajectoriesRed.PatternCollection(drive, drive.localizer.getPose(), "PGP", intake),

                        PathingTrajectoriesRed.firingPositionZoneOne(drive, drive.localizer.getPose(), intake, flywheels),

                        PathingTrajectoriesRed.fire(intake, flywheels, transfer, distanceFromGoal),

                        PathingTrajectoriesRed.PatternCollection(drive, drive.localizer.getPose(), "GPP", intake),

                        PathingTrajectoriesRed.firingPositionZoneOne(drive, drive.localizer.getPose(), intake, flywheels),

                        PathingTrajectoriesRed.fire(intake, flywheels, transfer, distanceFromGoal),

                        PathingTrajectoriesRed.LongRangePark(drive, drive.localizer.getPose(), flywheels, intake)

                )

        );
    }

}

