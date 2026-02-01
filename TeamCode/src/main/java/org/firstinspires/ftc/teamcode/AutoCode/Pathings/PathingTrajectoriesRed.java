package org.firstinspires.ftc.teamcode.AutoCode.Pathings;

import com.acmerobotics.roadrunner.*;

import org.firstinspires.ftc.teamcode.AutoCode.Roadrunner.MecanumDrive;

import com.acmerobotics.roadrunner.Pose2d;

import org.firstinspires.ftc.teamcode.Hardware.Intake;
import org.firstinspires.ftc.teamcode.Hardware.Flywheels;
import org.firstinspires.ftc.teamcode.Hardware.Transfer;

import java.lang.Math;

public class PathingTrajectoriesRed {

    static double defaultVelocity = 100.0;

    static double defaultAngVelocity = Math.PI;

    public static Action initialMovementFromWallZoneOne(MecanumDrive drive, Pose2d currentPose, Intake intake, Flywheels flywheel) {

        MinVelConstraint maxSpeedConstraint = new MinVelConstraint(
                java.util.Arrays.asList(
                        new TranslationalVelConstraint(defaultVelocity),
                        new AngularVelConstraint(defaultAngVelocity)
                )
        );

        return drive.actionBuilder(currentPose)

                .stopAndAdd(new PathingActions.powerUpFlywheels(flywheel))

                .stopAndAdd(new PathingActions.maintainIntake(intake))

                .splineToLinearHeading(new Pose2d(18, 35, Math.toRadians(0)), Math.toRadians(45), maxSpeedConstraint)

                .build();

    }

    public static Action initialMovementFromGoalZoneOne(MecanumDrive drive, Pose2d currentPose, Intake intake, Flywheels flywheel) {

        MinVelConstraint maxSpeedConstraint = new MinVelConstraint(
                java.util.Arrays.asList(
                        new TranslationalVelConstraint(defaultVelocity),
                        new AngularVelConstraint(defaultAngVelocity)
                )
        );

        return drive.actionBuilder(currentPose)

                .stopAndAdd(new PathingActions.powerUpFlywheels(flywheel))

                .stopAndAdd(new PathingActions.maintainIntake(intake))

                .setReversed(true)
                .splineToConstantHeading(new Vector2d(30, 50), Math.toRadians(225), maxSpeedConstraint)

                .splineToSplineHeading(new Pose2d(18, 35, Math.toRadians(0)), Math.toRadians(270), maxSpeedConstraint)

                //.stopAndAdd(new PathingActions.firingSequence(intake, flywheel, wheels, distance))

                .build();

    }

    public static Action firingPositionZoneOne(MecanumDrive drive, Pose2d currentPose, Intake intake, Flywheels flywheel) {

        MinVelConstraint maxSpeedConstraint = new MinVelConstraint(
                java.util.Arrays.asList(
                        new TranslationalVelConstraint(defaultVelocity),
                        new AngularVelConstraint(defaultAngVelocity)
                )
        );

        return drive.actionBuilder(currentPose)

                .stopAndAdd(new PathingActions.maintainIntake(intake))
                .stopAndAdd(new PathingActions.powerUpFlywheels(flywheel))

                .setReversed(true)
                .splineToLinearHeading(new Pose2d(18, 35, Math.toRadians(0)), Math.toRadians(90), maxSpeedConstraint)

                .build();
    }

    public static Action firingPositionZoneTwo(MecanumDrive drive, Pose2d currentPose, Intake intake, Flywheels flywheel) {

        MinVelConstraint maxSpeedConstraint = new MinVelConstraint(
                java.util.Arrays.asList(
                        new TranslationalVelConstraint(25),
                        new AngularVelConstraint(defaultAngVelocity)
                )
        );

        return drive.actionBuilder(currentPose)

                .stopAndAdd(new PathingActions.maintainIntake(intake))

                .strafeToLinearHeading(new Vector2d(15, -55), Math.toRadians(90), maxSpeedConstraint)

                .build();
    }

    public static Action fire(MecanumDrive drive, Pose2d currentPose, Intake intake, Flywheels flywheel, Transfer wheels, double distance) {
        return new PathingActions.firingSequence(intake, flywheel, wheels, distance);
    }
    public static Action fireFar(MecanumDrive drive, Pose2d currentPose, Intake intake, Flywheels flywheel, Transfer wheels, double distance) {
        return new PathingActions.firingSequenceFar(intake, flywheel, wheels, distance);
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

                        .setReversed(false)
                        .splineToConstantHeading(new Vector2d(18, 20), Math.toRadians(270))
                        .stopAndAdd(new PathingActions.runIntake(intake))
                        .splineToLinearHeading(new Pose2d(48, 10, Math.toRadians(0)), Math.toRadians(0),
                                new MinVelConstraint(
                                        java.util.Arrays.asList(
                                                new TranslationalVelConstraint(30),
                                                new AngularVelConstraint(defaultAngVelocity)
                                        )
                                ))

                        .build();

            case "PGP":

                return drive.actionBuilder(currentPose)

                        .setReversed(false)
                        .splineToConstantHeading(new Vector2d(18, -5), Math.toRadians(270), maxSpeedConstraint)
                        .stopAndAdd(new PathingActions.runIntake(intake))
                        .splineToLinearHeading(new Pose2d(48, -14, Math.toRadians(0)), Math.toRadians(0),
                                new MinVelConstraint(
                                        java.util.Arrays.asList(
                                                new TranslationalVelConstraint(30),
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
                        .splineToLinearHeading(new Pose2d(48, -38, Math.toRadians(0)), Math.toRadians(0), new MinVelConstraint(
                                        java.util.Arrays.asList(
                                                new TranslationalVelConstraint(30),
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
                .splineTo(new Vector2d(63, -58), Math.toRadians(0), maxSpeedConstraint)

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
                .strafeTo(new Vector2d(35, -60), maxSpeedConstraint)

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
                .strafeTo(new Vector2d(33, -60), maxSpeedConstraint)

                .build();
    }
}
