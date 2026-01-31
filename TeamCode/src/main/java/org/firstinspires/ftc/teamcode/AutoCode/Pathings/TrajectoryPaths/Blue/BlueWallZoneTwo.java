//package org.firstinspires.ftc.teamcode.AutoCode.Pathings.TrajectoryPaths.Blue;
//import com.acmerobotics.dashboard.config.Config;
//import com.acmerobotics.roadrunner.Pose2d;
//import com.acmerobotics.roadrunner.SequentialAction;
//import com.acmerobotics.roadrunner.ftc.Actions;
//import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
//import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
//
//import org.firstinspires.ftc.teamcode.AutoCode.Pathings.PathingTrajectoriesBlue;
//import org.firstinspires.ftc.teamcode.AutoCode.Pathings.PathingTrajectoriesRed;
//import org.firstinspires.ftc.teamcode.AutoCode.Roadrunner.MecanumDrive;
//import org.firstinspires.ftc.teamcode.Hardware.Flywheels;
//import org.firstinspires.ftc.teamcode.Hardware.Intake;
//import org.firstinspires.ftc.teamcode.Hardware.Transfer;
//import org.firstinspires.ftc.teamcode.Hardware.Turret;
//
//@Config
//@Autonomous(name = "[Competition] [Blue] Start at Blue Wall, Shoot from Zone Two", group = "Autonomous")
//public class BlueWallZoneTwo extends LinearOpMode {
//
//    Intake intake;
//    Flywheels flywheels;
//    Transfer transfer;
//
//    @Override
//    public void runOpMode() {
//
//        Pose2d startPose = new Pose2d(-15, -60, Math.toRadians(180));
//        MecanumDrive drive = new MecanumDrive(hardwareMap, startPose);
//
//        intake = new Intake(hardwareMap);
//        flywheels = new Flywheels(hardwareMap);
//        transfer = new Transfer(hardwareMap);
//
//        waitForStart();
//
//        Actions.runBlocking(
//                new SequentialAction(
//                        PathingTrajectoriesBlue.initialFiringFromWallZoneTwo(drive, startPose, intake, flywheels, transfer, telemetry)
//                )
//        );
//
//        Actions.runBlocking(
//                new SequentialAction(
//                        PathingTrajectoriesBlue.collectArtifactsZoneTwo(drive, startPose, intake)
//                )
//        );
//
//        Actions.runBlocking(
//                new SequentialAction(
//                        PathingTrajectoriesBlue.firingPositionZoneTwo(drive, startPose, intake, flywheels, transfer, telemetry)
//                )
//        );
//
////        Actions.runBlocking(
////                new SequentialAction(
////                        PathingTrajectoriesBlue.grabThree(drive, drive.localizer.getPose(), intake)
////                )
////        );
////
////        Actions.runBlocking(
////                new SequentialAction(
////                        PathingTrajectoriesBlue.firingPositionZoneTwo(drive, startPose, intake, flywheels, transfer, telemetry)
////                )
////        );
//
//        Actions.runBlocking(
//                new SequentialAction(
//                        PathingTrajectoriesBlue.LongRangePark(drive, drive.localizer.getPose(), flywheels, intake)
//                )
//        );
//    }
//}