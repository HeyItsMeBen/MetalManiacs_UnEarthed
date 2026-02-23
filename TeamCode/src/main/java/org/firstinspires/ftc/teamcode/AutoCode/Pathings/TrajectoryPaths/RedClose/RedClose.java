package org.firstinspires.ftc.teamcode.AutoCode.Pathings.TrajectoryPaths.RedClose;
// Paths

// Actions
// Actions

import static org.firstinspires.ftc.teamcode.AutoCode.Pathings.TrajectoryPaths.BlueClose.BlueCloseTrajectories.collectPattern;
import static org.firstinspires.ftc.teamcode.AutoCode.Pathings.TrajectoryPaths.BlueClose.BlueCloseTrajectories.firingPosition;
import static org.firstinspires.ftc.teamcode.AutoCode.Pathings.TrajectoryPaths.BlueClose.BlueCloseTrajectories.initialMoveToPosition;
import static org.firstinspires.ftc.teamcode.AutoCode.Pathings.TrajectoryPaths.BlueClose.BlueCloseTrajectories.openChannel;
import static org.firstinspires.ftc.teamcode.AutoCode.Pathings.TrajectoryPaths.BlueClose.BlueCloseTrajectories.park;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.InstantAction;
import com.acmerobotics.roadrunner.ParallelAction;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.SequentialAction;
import com.acmerobotics.roadrunner.TrajectoryActionBuilder;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.AutoCode.Pathings.PathingActions;
import org.firstinspires.ftc.teamcode.AutoCode.Roadrunner.MecanumDrive;
import org.firstinspires.ftc.teamcode.Controllers.AutoAimTurretController;
import org.firstinspires.ftc.teamcode.Controllers.FlywheelController;
import org.firstinspires.ftc.teamcode.Controllers.IntakeController;
import org.firstinspires.ftc.teamcode.Controllers.LightsController;
import org.firstinspires.ftc.teamcode.Hardware.AutoAim;
import org.firstinspires.ftc.teamcode.Hardware.Flywheels;
import org.firstinspires.ftc.teamcode.Hardware.Intake;
import org.firstinspires.ftc.teamcode.Hardware.Lights;
import org.firstinspires.ftc.teamcode.Hardware.OuttakeHood;
import org.firstinspires.ftc.teamcode.Hardware.Transfer;
import org.firstinspires.ftc.teamcode.Hardware.Turret;

@Config
@Autonomous(name = "Red Close", group = "Autonomous - Red")
public class RedClose extends LinearOpMode {

    Intake intake;
    Flywheels flywheels;
    Transfer transferDrum;
    Transfer transferKick;
    Turret turret;
    AutoAim autoAim;
    OuttakeHood hood;
    Lights lights;

    IntakeController intakeController;
    FlywheelController flywheelController;
    LightsController lightsController;
    AutoAimTurretController aprilTagTurretAim;
    public String ballSequence = "XXX";

    @Override
    public void runOpMode() throws InterruptedException {

        Pose2d startPose = new Pose2d(52, 52, Math.toRadians(40));
        MecanumDrive drive = new MecanumDrive(hardwareMap, startPose);

        intake = new Intake(hardwareMap);
        flywheels = new Flywheels(hardwareMap);
        turret = new Turret(hardwareMap);
        autoAim = new AutoAim(Math.toRadians(15));
        transferDrum = new Transfer(hardwareMap);
        transferKick = new Transfer(hardwareMap);
        hood = new OuttakeHood(hardwareMap);
        lights = new Lights(hardwareMap);

        intakeController = new IntakeController(intake, transferDrum, transferKick);
        flywheelController = new FlywheelController(flywheels, transferDrum, transferKick, intake, hood);
        lightsController = new LightsController(lights);

        aprilTagTurretAim = new AutoAimTurretController(hardwareMap);

        waitForStart();
        if (isStopRequested()) return;

        lightsController.update(false, false, "Red", ballSequence);

        RedCloseTrajectories paths = new RedCloseTrajectories(drive, startPose);

        TrajectoryActionBuilder initialMoveToPosition = paths.initialMoveToPosition();
        TrajectoryActionBuilder collectPatternPGP = paths.collectPatternPGP(initialMoveToPosition);
        TrajectoryActionBuilder fireFirstSet = paths.firingPosition(collectPatternPGP);
        TrajectoryActionBuilder openChannel = paths.openChannel(fireFirstSet);
        TrajectoryActionBuilder fireSecondSet = paths.firingPosition(openChannel);
        TrajectoryActionBuilder collectPatternPPG = paths.firingPosition(fireSecondSet);
        TrajectoryActionBuilder fireThirdSet = paths.firingPosition(collectPatternPPG);
        TrajectoryActionBuilder collectPatternGPP = paths.firingPosition(fireThirdSet);
        TrajectoryActionBuilder fireFourthSet = paths.firingPosition(collectPatternGPP);
        TrajectoryActionBuilder park = paths.park(fireFourthSet);

        Actions.runBlocking(
                new SequentialAction(

                        new ParallelAction(
                                //new InstantAction(() -> intakeController.toggleIntake()),
                                //new InstantAction(() -> intakeController.update()),
                                //new InstantAction(() -> flywheelController.rampUp()),
                                initialMoveToPosition.build()
                        ),

                        //new PathingActions.AutoAimAction(aprilTagTurretAim, lightsController, intakeController, "Red"),
                        //new PathingActions.FlywheelSequenceAction(flywheelController, () -> aprilTagTurretAim.getDistanceToGoalInches(), () -> aprilTagTurretAim.isTargetFound()),

                        collectPatternPGP.build(),

                        new ParallelAction(
                                //new PathingActions.AutoAimAction(aprilTagTurretAim, lightsController, intakeController, "Red"),
                                fireFirstSet.build()
                        ),
                        //new PathingActions.FlywheelSequenceAction(flywheelController, () -> aprilTagTurretAim.getDistanceToGoalInches(), () -> aprilTagTurretAim.isTargetFound()),

                        openChannel.build(),

                        new ParallelAction(
                                //new PathingActions.AutoAimAction(aprilTagTurretAim, lightsController, intakeController, "Red"),
                                fireSecondSet.build()
                        ),
                        //new PathingActions.FlywheelSequenceAction(flywheelController, () -> aprilTagTurretAim.getDistanceToGoalInches(), () -> aprilTagTurretAim.isTargetFound()),

                        collectPatternPPG.build(),

                        new ParallelAction(
                                //new PathingActions.AutoAimAction(aprilTagTurretAim, lightsController, intakeController, "Red"),
                                fireThirdSet.build()
                        ),
                        //new PathingActions.FlywheelSequenceAction(flywheelController, () -> aprilTagTurretAim.getDistanceToGoalInches(), () -> aprilTagTurretAim.isTargetFound()),

                        collectPatternGPP.build(),

                        new ParallelAction(
                                //new PathingActions.AutoAimAction(aprilTagTurretAim, lightsController, intakeController, "Red"),
                                fireFourthSet.build()
                        ),
                        //new PathingActions.FlywheelSequenceAction(flywheelController, () -> aprilTagTurretAim.getDistanceToGoalInches(), () -> aprilTagTurretAim.isTargetFound()),

                        new ParallelAction(
                                //new InstantAction(() -> intakeController.toggleIntake()),
                                park.build()
                        )
                )
        );

    }

}

