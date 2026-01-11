package org.firstinspires.ftc.teamcode.AutoCode.Pathings;

import com.acmerobotics.roadrunner.*;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.AutoCode.Roadrunner.MecanumDrive;

import com.acmerobotics.roadrunner.Pose2d;

import org.firstinspires.ftc.teamcode.Hardware.Intake;
import org.firstinspires.ftc.teamcode.Hardware.Flywheels;
import org.firstinspires.ftc.teamcode.Hardware.Transfer;
import org.firstinspires.ftc.teamcode.Hardware.Turret;

import java.lang.Math;

public class PathingTrajectoriesBlue {

    static double defaultVelocity = 50.0;

    static double defaultAngVelocity = Math.PI;

    public static Action initialFiringFromWallZoneOne(MecanumDrive drive, Pose2d currentPose, Intake intake, Flywheels flywheel, Transfer wheels, Turret turret, Telemetry telemetry) {

        MinVelConstraint maxSpeedConstraint = new MinVelConstraint(
                java.util.Arrays.asList(
                        new TranslationalVelConstraint(50),
                        new AngularVelConstraint(defaultAngVelocity)
                )
        );

        return drive.actionBuilder(currentPose)

                .setReversed(true)
                .splineToLinearHeading(new Pose2d(-18, 18, Math.toRadians(180)), Math.toRadians(110), maxSpeedConstraint)


                .stopAndAdd(new PathingActions.InitializeTurretPositionZoneOneRed(turret))
                .stopAndAdd(new PathingActions.firingSequence(intake, flywheel, wheels, 1, telemetry))

                .build();

    }

    public static Action initialFiringFromGoalZoneOne(MecanumDrive drive, Pose2d currentPose, Intake intake, Flywheels flywheel, Transfer wheels, Turret turret, Telemetry telemetry) {

        MinVelConstraint maxSpeedConstraint = new MinVelConstraint(
                java.util.Arrays.asList(
                        new TranslationalVelConstraint(20),
                        new AngularVelConstraint(defaultAngVelocity)
                )
        );

        return drive.actionBuilder(currentPose)

                .setReversed(true)
                .splineToConstantHeading(new Vector2d(-30, 50), Math.toRadians(-45), maxSpeedConstraint)

                .splineToSplineHeading(new Pose2d(-18, 18, Math.toRadians(180)), Math.toRadians(-90), maxSpeedConstraint)

                .stopAndAdd(new PathingActions.InitializeTurretPositionZoneOneRed(turret))
                .stopAndAdd(new PathingActions.firingSequence(intake, flywheel, wheels, 1, telemetry))

                .build();

    }

    public static Action initialFiringFromWallZoneTwo(MecanumDrive drive, Pose2d currentPose, Intake intake, Flywheels flywheel, Transfer wheels, Turret turret, Telemetry telemetry) {

        MinVelConstraint maxSpeedConstraint = new MinVelConstraint(
                java.util.Arrays.asList(
                        new TranslationalVelConstraint(50),
                        new AngularVelConstraint(defaultAngVelocity)
                )
        );

        return drive.actionBuilder(currentPose)

                .setReversed(true)
                .strafeTo(new Vector2d(-15, -47), maxSpeedConstraint)

                .stopAndAdd(new PathingActions.InitializeTurretPositionZoneTwoRed(turret))
                .stopAndAdd(new PathingActions.firingSequence(intake, flywheel, wheels, 2, telemetry))

                .build();

    }

    public static Action firingPositionZoneOne(MecanumDrive drive, Pose2d currentPose, Intake intake, Flywheels flywheel, Transfer wheels, Telemetry telemetry) {

        MinVelConstraint maxSpeedConstraint = new MinVelConstraint(
                java.util.Arrays.asList(
                        new TranslationalVelConstraint(50),
                        new AngularVelConstraint(defaultAngVelocity)
                )
        );

        return drive.actionBuilder(currentPose)

                .setReversed(true)
                .splineToLinearHeading(new Pose2d(-18, 18, Math.toRadians(180)), Math.toRadians(90), maxSpeedConstraint)

                .stopAndAdd(new PathingActions.firingSequence(intake, flywheel, wheels, 1, telemetry))

                .build();
    }

    public static Action firingPositionZoneTwo(MecanumDrive drive, Pose2d currentPose, Intake intake, Flywheels flywheel, Transfer wheels, Telemetry telemetry) {

        MinVelConstraint maxSpeedConstraint = new MinVelConstraint(
                java.util.Arrays.asList(
                        new TranslationalVelConstraint(20),
                        new AngularVelConstraint(defaultAngVelocity)
                )
        );

        return drive.actionBuilder(currentPose)

                .strafeToLinearHeading(new Vector2d(-15, -47), Math.toRadians(-90), maxSpeedConstraint)

                .stopAndAdd(new PathingActions.firingSequence(intake, flywheel, wheels, 2, telemetry))

                .build();
    }

    public static Action PatternCollection(MecanumDrive drive, Pose2d currentPose, String Pattern, Intake intake) {

        MinVelConstraint maxSpeedConstraint = new MinVelConstraint(
                java.util.Arrays.asList(
                        new TranslationalVelConstraint(30),
                        new AngularVelConstraint(defaultAngVelocity)
                )
        );

        if (Pattern.equals("PPG")) {

            return drive.actionBuilder(currentPose)

                    .stopAndAdd(new PathingActions.runIntake(intake))

                    .setReversed(false)
                    .splineTo(new Vector2d(-55,12), Math.toRadians(180), maxSpeedConstraint)

                    .build();

        }
        else if (Pattern.equals("PGP")) {

            return drive.actionBuilder(currentPose)

                    .stopAndAdd(new PathingActions.runIntake(intake))

                    .setReversed(false)
                    .splineTo(new Vector2d(-55,-12), Math.toRadians(180), maxSpeedConstraint)

                    .build();

        }

        else if (Pattern.equals("GPP")) {

            return drive.actionBuilder(currentPose)

                    .stopAndAdd(new PathingActions.runIntake(intake))

                    .setReversed(false)
                    .splineToConstantHeading(new Vector2d(-18, -25), Math.toRadians(-90), maxSpeedConstraint)
                    .splineToLinearHeading(new Pose2d(-55, -35, Math.toRadians(180)), Math.toRadians(180), maxSpeedConstraint)
                    .build();

        }

        else {
            return drive.actionBuilder(currentPose)

                    .setReversed(false)
                    .splineTo(new Vector2d(-20, -35), Math.toRadians(-90), maxSpeedConstraint)

                    .build();
        }
    }

    public static Action collectArtifactsZoneTwo(MecanumDrive drive, Pose2d currentPose, Intake intake) {

        MinVelConstraint maxSpeedConstraint = new MinVelConstraint(
                java.util.Arrays.asList(
                        new TranslationalVelConstraint(20),
                        new AngularVelConstraint(defaultAngVelocity)
                )
        );

        return drive.actionBuilder(currentPose)

                .strafeTo(new Vector2d(-60, -55), maxSpeedConstraint)

                .stopAndAdd(new PathingActions.runIntake(intake))

                .build();
    }

    public static Action openChannel(MecanumDrive drive, Pose2d currentPose, Intake intake) {

        MinVelConstraint maxSpeedConstraint = new MinVelConstraint(
                java.util.Arrays.asList(
                        new TranslationalVelConstraint(15),
                        new AngularVelConstraint(defaultAngVelocity)
                )
        );

        return drive.actionBuilder(currentPose)

                .stopAndAdd(new PathingActions.stopIntake(intake))

                .setReversed(false)
                .strafeTo(new Vector2d(-45,8), maxSpeedConstraint)
                .splineToConstantHeading(new Vector2d(-63,5), Math.toRadians(180), maxSpeedConstraint)

                .waitSeconds(1)

                .build();
    }

    public static Action park(MecanumDrive drive, Pose2d currentPose, Flywheels flywheel, Intake intake, Turret turret) {

        MinVelConstraint maxSpeedConstraint = new MinVelConstraint(
                java.util.Arrays.asList(
                        new TranslationalVelConstraint(50),
                        new AngularVelConstraint(defaultAngVelocity)
                )
        );

        return drive.actionBuilder(currentPose)

                .stopAndAdd(new PathingActions.stopIntake(intake))
                .stopAndAdd(new PathingActions.endingTurretPosition(turret))

                .setReversed(false)
                .splineTo(new Vector2d(-20, -35), Math.toRadians(-90), maxSpeedConstraint)

                .build();
    }

}
