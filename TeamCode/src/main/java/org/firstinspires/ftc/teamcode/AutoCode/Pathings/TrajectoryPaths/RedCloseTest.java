package org.firstinspires.ftc.teamcode.AutoCode.Pathings.TrajectoryPaths;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.AutoCode.Roadrunner.MecanumDrive;
import org.firstinspires.ftc.teamcode.Controllers.LightsController;
import org.firstinspires.ftc.teamcode.Hardware.Lights;

@Config
@Autonomous(name = "Red Close Test 5", group = "Autonomous - Red")
public class RedCloseTest extends LinearOpMode {

//    Intake intake;
//    Flywheels flywheels;
//    Transfer transferDrum;
//    Transfer transferKick;
//    Turret turret;
//    AutoAim autoAim;
//    OuttakeHood hood;
    Lights lights;
//
//    IntakeController intakeController;
//    FlywheelController flywheelController;
    LightsController lightsController;
    public String ballSequence = "XXX";

    @Override
    public void runOpMode() {

        Pose2d startPose = new Pose2d(52, 52, Math.toRadians(40));
        MecanumDrive drive = new MecanumDrive(hardwareMap, startPose);
//
//        intake = new Intake(hardwareMap);
//        flywheels = new Flywheels(hardwareMap);
//        turret = new Turret(hardwareMap);
//        autoAim = new AutoAim(Math.toRadians(15));
//        transferDrum = new Transfer(hardwareMap);
//        transferKick = new Transfer(hardwareMap);
//        hood = new OuttakeHood(hardwareMap);
        lights = new Lights(hardwareMap);
//
//        intakeController = new IntakeController(intake, transferDrum, transferKick);
//        flywheelController = new FlywheelController(flywheels, transferDrum, transferKick, intake, hood);
        lightsController = new LightsController(lights);

        //aprilTagTurretAim = new AutoAimTurretController(hardwareMap);

        waitForStart();
        if (isStopRequested()) return;

        //lightsController.update(false, false, "Red", ballSequence);

        RedCloseTrajectoriesTest paths = new RedCloseTrajectoriesTest(drive, startPose);

        Actions.runBlocking(
                paths.initialMoveToPosition()
                        .collectPatternPGP()
                        .firingPosition()
                        .openChannel()
                        .park()
                        .build()
        );

    }

}

