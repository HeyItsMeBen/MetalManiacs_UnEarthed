package org.firstinspires.ftc.teamcode.AutoCode.Pathings;

import com.acmerobotics.roadrunner.*;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.AutoCode.Roadrunner.MecanumDrive;

import com.acmerobotics.roadrunner.Pose2d;

import org.firstinspires.ftc.teamcode.Hardware.Intake;
import org.firstinspires.ftc.teamcode.Hardware.Flywheels;
import org.firstinspires.ftc.teamcode.Hardware.Transfer;

import java.lang.Math;

public class PathingTrajectoriesRed {

    static double defaultVelocity = 60.0;

    static double defaultAngVelocity = Math.PI;

    public static Action initialFiringFromWallZoneOne(MecanumDrive drive, Pose2d currentPose, Intake intake, Flywheels flywheel, Transfer wheels, Telemetry telemetry) {

        MinVelConstraint maxSpeedConstraint = new MinVelConstraint(
                java.util.Arrays.asList(
                        new TranslationalVelConstraint(defaultVelocity),
                        new AngularVelConstraint(defaultAngVelocity)
                )
        );

        return drive.actionBuilder(currentPose)

                .stopAndAdd(new PathingActions.powerUpFlywheels(flywheel, 1))

                .splineToLinearHeading(new Pose2d(18, 25, Math.toRadians(0)), Math.toRadians(45), maxSpeedConstraint)

                .stopAndAdd(new PathingActions.firingSequence(intake, flywheel, wheels, 1, telemetry))

                .build();

    }

    public static Action initialFiringFromGoalZoneOne(MecanumDrive drive, Pose2d currentPose, Intake intake, Flywheels flywheel, Transfer wheels, Telemetry telemetry) {

        MinVelConstraint maxSpeedConstraint = new MinVelConstraint(
                java.util.Arrays.asList(
                        new TranslationalVelConstraint(defaultVelocity),
                        new AngularVelConstraint(defaultAngVelocity)
                )
        );

        return drive.actionBuilder(currentPose)

                .stopAndAdd(new PathingActions.powerUpFlywheels(flywheel, 1))

                .setReversed(true)
                .splineToConstantHeading(new Vector2d(30, 50), Math.toRadians(225), maxSpeedConstraint)

                .splineToSplineHeading(new Pose2d(18, 25, Math.toRadians(0)), Math.toRadians(270), maxSpeedConstraint)

                .stopAndAdd(new PathingActions.firingSequence(intake, flywheel, wheels, 1, telemetry))

                .build();

    }

    public static Action initialFiringFromWallZoneTwo(MecanumDrive drive, Pose2d currentPose, Intake intake, Flywheels flywheel, Transfer wheels, Telemetry telemetry) {

        MinVelConstraint maxSpeedConstraint = new MinVelConstraint(
                java.util.Arrays.asList(
                        new TranslationalVelConstraint(25),
                        new AngularVelConstraint(defaultAngVelocity)
                )
        );

        return drive.actionBuilder(currentPose)

                .stopAndAdd(new PathingActions.powerUpFlywheels(flywheel, 2))

                .splineToLinearHeading(new Pose2d(15, -47, Math.toRadians(30)), Math.toRadians(90))

                .stopAndAdd(new PathingActions.firingSequence(intake, flywheel, wheels, 2, telemetry))

                .build();

    }

    public static Action firingPositionZoneOne(MecanumDrive drive, Pose2d currentPose, Intake intake, Flywheels flywheel, Transfer wheels, Telemetry telemetry) {

        MinVelConstraint maxSpeedConstraint = new MinVelConstraint(
                java.util.Arrays.asList(
                        new TranslationalVelConstraint(defaultVelocity),
                        new AngularVelConstraint(defaultAngVelocity)
                )
        );

        return drive.actionBuilder(currentPose)

                .stopAndAdd(new PathingActions.stopIntake(intake))
                .stopAndAdd(new PathingActions.powerUpFlywheels(flywheel, 1))

                .setReversed(true)
                .splineToLinearHeading(new Pose2d(18, 25, Math.toRadians(0)), Math.toRadians(90), maxSpeedConstraint)

                .stopAndAdd(new PathingActions.firingSequence(intake, flywheel, wheels, 1, telemetry))

                .build();
    }

    public static Action firingPositionZoneTwo(MecanumDrive drive, Pose2d currentPose, Intake intake, Flywheels flywheel, Transfer wheels, Telemetry telemetry) {

        MinVelConstraint maxSpeedConstraint = new MinVelConstraint(
                java.util.Arrays.asList(
                        new TranslationalVelConstraint(25),
                        new AngularVelConstraint(defaultAngVelocity)
                )
        );

        return drive.actionBuilder(currentPose)

                .stopAndAdd(new PathingActions.stopIntake(intake))

                .strafeToLinearHeading(new Vector2d(15, -47), Math.toRadians(30), maxSpeedConstraint)

                .stopAndAdd(new PathingActions.firingSequence(intake, flywheel, wheels, 2, telemetry))
                .waitSeconds(2)

                .build();
    }

    public static Action PatternCollection(MecanumDrive drive, Pose2d currentPose, String Pattern, Intake intake) {

        MinVelConstraint maxSpeedConstraint = new MinVelConstraint(
                java.util.Arrays.asList(
                        new TranslationalVelConstraint(50),
                        new AngularVelConstraint(defaultAngVelocity)
                )
        );

        switch (Pattern) {

            case "PPG":

                return drive.actionBuilder(currentPose)

                        .stopAndAdd(new PathingActions.runIntake(intake))

                        .setReversed(false)
                        .splineTo(new Vector2d(58, 12), Math.toRadians(0),
                                new MinVelConstraint(
                                java.util.Arrays.asList(
                                        new TranslationalVelConstraint(20),
                                        new AngularVelConstraint(defaultAngVelocity)
                                )
                        ))

                        .build();

            case "PGP":

                return drive.actionBuilder(currentPose)

                        .setReversed(false)
                        .splineToConstantHeading(new Vector2d(18, -5), Math.toRadians(270), maxSpeedConstraint)
                        .stopAndAdd(new PathingActions.runIntake(intake))
                        .splineToLinearHeading(new Pose2d(58, -10, Math.toRadians(0)), Math.toRadians(0),
                                new MinVelConstraint(
                                        java.util.Arrays.asList(
                                                new TranslationalVelConstraint(20),
                                                new AngularVelConstraint(defaultAngVelocity)
                                        )
                                )
                        )
                        .build();

            case "GPP":

                return drive.actionBuilder(currentPose)

                        .setReversed(false)
                        .splineToConstantHeading(new Vector2d(18, -25), Math.toRadians(270), maxSpeedConstraint)
                        .stopAndAdd(new PathingActions.runIntake(intake))
                        .splineToLinearHeading(new Pose2d(58, -36, Math.toRadians(0)), Math.toRadians(0), new MinVelConstraint(
                                        java.util.Arrays.asList(
                                                new TranslationalVelConstraint(20),
                                                new AngularVelConstraint(defaultAngVelocity)
                                        )
                                )
                        )
                        .build();

            default:

                return drive.actionBuilder(currentPose)

                        .setReversed(false)
                        .splineTo(new Vector2d(20, -35), Math.toRadians(270), maxSpeedConstraint)

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

                .stopAndAdd(new PathingActions.runIntake(intake))
                .strafeToLinearHeading(new Vector2d(60, -55), Math.toRadians(350), maxSpeedConstraint)

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
                .strafeTo(new Vector2d(45,8), maxSpeedConstraint)
                .splineToConstantHeading(new Vector2d(67,4), Math.toRadians(0), maxSpeedConstraint)

                .waitSeconds(1)

                .build();
    }

    public static Action park(MecanumDrive drive, Pose2d currentPose, Flywheels flywheel, Intake intake) {

        MinVelConstraint maxSpeedConstraint = new MinVelConstraint(
                java.util.Arrays.asList(
                        new TranslationalVelConstraint(defaultVelocity),
                        new AngularVelConstraint(defaultAngVelocity)
                )
        );

        return drive.actionBuilder(currentPose)

                .stopAndAdd(new PathingActions.stopFlywheels(flywheel))
                .stopAndAdd(new PathingActions.stopIntake(intake))

                .setReversed(false)
                .splineTo(new Vector2d(20, -35), Math.toRadians(270), maxSpeedConstraint)

                .build();
    }

    public static Action LongRangePark(MecanumDrive drive, Pose2d currentPose, Flywheels flywheel, Intake intake) {

        MinVelConstraint maxSpeedConstraint = new MinVelConstraint(
                java.util.Arrays.asList(
                        new TranslationalVelConstraint(defaultVelocity),
                        new AngularVelConstraint(defaultAngVelocity)
                )
        );

        return drive.actionBuilder(currentPose)

                .stopAndAdd(new PathingActions.stopFlywheels(flywheel))
                .stopAndAdd(new PathingActions.stopIntake(intake))

                .setReversed(false)
                .strafeToLinearHeading(new Vector2d(20, -35), Math.toRadians(0), maxSpeedConstraint)

                .build();
    }

    public static Action grabThree(MecanumDrive drive, Pose2d currentPose, Intake intake) {

        MinVelConstraint maxSpeedConstraint = new MinVelConstraint(
                java.util.Arrays.asList(
                        new TranslationalVelConstraint(defaultVelocity),
                        new AngularVelConstraint(defaultAngVelocity)
                )
        );

        return drive.actionBuilder(currentPose)

                .stopAndAdd(new PathingActions.runIntake(intake))

                .splineTo(new Vector2d(58, -33), Math.toRadians(0), maxSpeedConstraint)

                .build();
    }

}
