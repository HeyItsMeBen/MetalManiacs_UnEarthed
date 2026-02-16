//package org.firstinspires.ftc.teamcode.AutoCode.Pathings.TrajectoryPaths.Red;
//import com.acmerobotics.dashboard.config.Config;
//import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
//import com.acmerobotics.roadrunner.Action;
//import com.acmerobotics.roadrunner.InstantAction;
//import com.acmerobotics.roadrunner.Pose2d;
//import com.acmerobotics.roadrunner.SequentialAction;
//import com.acmerobotics.roadrunner.TrajectoryActionBuilder;
//import com.acmerobotics.roadrunner.ftc.Actions;
//import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
//import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
//import com.qualcomm.robotcore.util.ElapsedTime;
//
//import org.firstinspires.ftc.robotcore.external.hardware.camera.BuiltinCameraDirection;
//import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
//import org.firstinspires.ftc.robotcore.external.hardware.camera.controls.ExposureControl;
//import org.firstinspires.ftc.robotcore.external.hardware.camera.controls.GainControl;
//import org.firstinspires.ftc.teamcode.AutoCode.Pathings.AprilTagTurretAim;
//import org.firstinspires.ftc.teamcode.AutoCode.Pathings.PathingActions;
//import org.firstinspires.ftc.teamcode.AutoCode.Pathings.PathingTrajectoriesRed;
//import org.firstinspires.ftc.teamcode.AutoCode.Roadrunner.MecanumDrive;
//import org.firstinspires.ftc.teamcode.DriveCode.DriveCodeClasses.FlywheelController;
//import org.firstinspires.ftc.teamcode.Hardware.AutoAim;
//import org.firstinspires.ftc.teamcode.Hardware.Flywheels;
//import org.firstinspires.ftc.teamcode.Hardware.Intake;
//import org.firstinspires.ftc.teamcode.Hardware.Lights;
//import org.firstinspires.ftc.teamcode.Hardware.OuttakeHood;
//import org.firstinspires.ftc.teamcode.Hardware.Transfer;
//import org.firstinspires.ftc.teamcode.Hardware.Turret;
//import org.firstinspires.ftc.vision.VisionPortal;
//import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
//import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;
//
//import java.util.List;
//import java.util.concurrent.TimeUnit;
//
//@Config
//@Autonomous(name = "[Competition] [Red] Start at Red Goal, Shoot from Zone One", group = "Autonomous")
//public class RedGoalZoneOne extends LinearOpMode {
//    // Initialize the Apriltag Detection process
//    Intake intake;
//    Flywheels flywheels;
//    Transfer transfer;
//    Transfer kickServo;
//    Turret turret;
//    AutoAim autoAim;
//    Lights lights;
//
//    AprilTagTurretAim aprilTagTurretAim;
//    ElapsedTime turretTimer;
//    MecanumDrive drive;
//    OuttakeHood hood;
//
//    FlywheelController flywheelController;
//    public double distanceFromGoal = 1750;
//
//    @Override
//    public void runOpMode() {
//        // Initialize the Apriltag Detection process
//
//        Pose2d startPose = new Pose2d(52, 52, Math.toRadians(315));
//        drive = new MecanumDrive(hardwareMap, startPose);
//
//        intake = new Intake(hardwareMap);
//        flywheels = new Flywheels(hardwareMap);
//        transfer = new Transfer(hardwareMap);
//        kickServo = new Transfer(hardwareMap);
//        turret = new Turret(hardwareMap);
//        autoAim = new AutoAim(Math.toRadians(15));
//        lights = new Lights(hardwareMap);
//        hood = new OuttakeHood(hardwareMap);
//
//        aprilTagTurretAim = new AprilTagTurretAim(this, turret, autoAim, true, 24, 20,lights);
//
//        aprilTagTurretAim.init();
//
//        flywheelController = new FlywheelController(flywheels, transfer, kickServo, intake, hood);
//
//        waitForStart();
//
//        if (isStopRequested()) return;
//
//        aprilTagTurretAim.waitForStreaming();
//        aprilTagTurretAim.setManualExposure(6, 250);
//        lights.Light_Team_Color("Red");
//
//        Actions.runBlocking(
//                new SequentialAction(
//                        PathingTrajectoriesRed.initialMovementFromGoalZoneOne(drive, drive.localizer.getPose(), intake, flywheels)
//                )
//        );
//
//        turretTimer = new ElapsedTime();
//        while (opModeIsActive() && turretTimer.milliseconds() < 1000) {
//            distanceFromGoal = aprilTagTurretAim.update();
//            if (distanceFromGoal == 0) {
//                distanceFromGoal = 1750;
//            }
//        }
//        aprilTagTurretAim.stopTurret();
//
//        Actions.runBlocking(
//                new SequentialAction(
//
//                        new PathingActions.firingSequence(flywheelController, distanceFromGoal),
//
//                        PathingTrajectoriesRed.PatternCollection(drive, drive.localizer.getPose(), "PPG", intake),
//
//                        PathingTrajectoriesRed.firingPositionZoneOne(drive, drive.localizer.getPose(), intake, flywheels),
//
//                        PathingTrajectoriesRed.fire(intake, flywheels, transfer, distanceFromGoal),
//
//                        PathingTrajectoriesRed.PatternCollection(drive, drive.localizer.getPose(), "PGP", intake),
//
//                        PathingTrajectoriesRed.firingPositionZoneOne(drive, drive.localizer.getPose(), intake, flywheels),
//
//                        PathingTrajectoriesRed.fire(intake, flywheels, transfer, distanceFromGoal),
//
//                        PathingTrajectoriesRed.PatternCollection(drive, drive.localizer.getPose(), "GPP", intake),
//
//                        PathingTrajectoriesRed.firingPositionZoneOne(drive, drive.localizer.getPose(), intake, flywheels),
//
//                        PathingTrajectoriesRed.fire(intake, flywheels, transfer, distanceFromGoal),
//
//                        PathingTrajectoriesRed.LongRangePark(drive, drive.localizer.getPose(), flywheels, intake)
//
//                )
//
//        );
//    }
//}