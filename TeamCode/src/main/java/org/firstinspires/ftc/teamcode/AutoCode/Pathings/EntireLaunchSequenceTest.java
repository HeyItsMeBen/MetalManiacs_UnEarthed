package org.firstinspires.ftc.teamcode.AutoCode.Pathings;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.SequentialAction;
import com.acmerobotics.roadrunner.TrajectoryActionBuilder;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.AutoCode.Pathings.TrajectoryPaths.PathingActions;
import org.firstinspires.ftc.teamcode.AutoCode.Roadrunner.MecanumDrive;
import org.firstinspires.ftc.teamcode.Controllers.AutoAimTurretController;
import org.firstinspires.ftc.teamcode.Controllers.FlywheelController;
import org.firstinspires.ftc.teamcode.Controllers.IntakeController;
import org.firstinspires.ftc.teamcode.Controllers.LightsController;
import org.firstinspires.ftc.teamcode.Hardware.Flywheels;
import org.firstinspires.ftc.teamcode.Hardware.Intake;
import org.firstinspires.ftc.teamcode.Hardware.Lights;
import org.firstinspires.ftc.teamcode.Hardware.OuttakeHood;
import org.firstinspires.ftc.teamcode.Hardware.Transfer;

@Config
@Autonomous(name = "Launch Sequence Test", group = "z-Autonomous - Any")
public class EntireLaunchSequenceTest extends LinearOpMode {

    Flywheels flywheels;
    Transfer transferDrum;
    Transfer transferServo;
    Intake intake;
    OuttakeHood hood;

    FlywheelController flywheelController;

    public String ballSequence = "XXX";


    @Override
    public void runOpMode() {

        Pose2d startPose = new Pose2d(0, 0, Math.toRadians(90));
        MecanumDrive drive = new MecanumDrive(hardwareMap, startPose);

        flywheels = new Flywheels(hardwareMap);
        intake = new Intake(hardwareMap);
        transferDrum = new Transfer(hardwareMap);
        transferServo = new Transfer(hardwareMap);
        hood = new OuttakeHood(hardwareMap);
        Lights lights = new Lights(hardwareMap);

        IntakeController intakeController = new IntakeController(intake, transferDrum, transferServo);
        flywheelController = new FlywheelController(flywheels, transferDrum, transferServo, intake, hood);
        LightsController lightsController = new LightsController(lights);

        AutoAimTurretController autoAimController = new AutoAimTurretController(hardwareMap, startPose, "Red"); // May crop out, takes too long to initialize

        TrajectoryActionBuilder park = drive.actionBuilder(startPose)

                .strafeTo(new Vector2d((drive.localizer.getPose().position.x + 30), drive.localizer.getPose().position.y));

        waitForStart();
        if (isStopRequested()) return;

        double autoAimStartTime=System.currentTimeMillis();
        while (System.currentTimeMillis()<autoAimStartTime+1000){
            autoAimController.update2(false, false);
        }
        autoAimController.setTurretPower(0);
        lightsController.update(autoAimController.isTargetFound(), intakeController.isIntakeRunning(), "Red", ballSequence);

        Actions.runBlocking(
                new SequentialAction(

                        //new PathingActions.FlywheelSequenceAction(flywheelController, () -> autoAimController.getDistanceToGoalInches(), () -> autoAimController.isTargetFound()),

                        //new PathingActions.FlywheelSequenceActionDirect(flywheels, intake, transferDrum, transferServo, () -> autoAimController.getDistanceToGoalInches(), () -> autoAimController.isTargetFound()),

                        park.build()

                )
        );

        double autoAimFinishTime=System.currentTimeMillis();
        while (System.currentTimeMillis()<autoAimFinishTime+500){
            autoAimController.turnToCenter();
        }

    }

}

