package org.firstinspires.ftc.teamcode.AutoCode.Pathings;

import com.acmerobotics.roadrunner.*;

import org.firstinspires.ftc.teamcode.AutoCode.Roadrunner.MecanumDrive;

import com.acmerobotics.roadrunner.Pose2d;

import org.firstinspires.ftc.teamcode.Hardware.Intake;
import org.firstinspires.ftc.teamcode.Hardware.Flywheels;
import org.firstinspires.ftc.teamcode.Hardware.Transfer;

import java.lang.Math;

public class PathingTrajectoriesRed {

    static double defaultVelocity = 10.0;

    static double defaultAngVelocity = Math.PI;

    static MinVelConstraint defaultSpeedConstraint = new MinVelConstraint(
            java.util.Arrays.asList(
                    new TranslationalVelConstraint(defaultVelocity),
                    new AngularVelConstraint(defaultAngVelocity)
            )
    );

    public static Action firingPositionZoneOne(MecanumDrive drive, Pose2d currentPose) {

        return drive.actionBuilder(currentPose)

                .setReversed(true)
                .splineToLinearHeading(new Pose2d(18, 35, Math.toRadians(0)), Math.toRadians(90), defaultSpeedConstraint)

                .build();
    }

    public static Action firingPositionZoneTwo(MecanumDrive drive, Pose2d currentPose) {

        MinVelConstraint maxSpeedConstraint = new MinVelConstraint(
                java.util.Arrays.asList(
                        new TranslationalVelConstraint(25),
                        new AngularVelConstraint(defaultAngVelocity)
                )
        );

        return drive.actionBuilder(currentPose)

                .strafeToLinearHeading(new Vector2d(15, -55), Math.toRadians(90), maxSpeedConstraint)

                .build();
    }

    public static Action PatternCollection(MecanumDrive drive, Pose2d currentPose, String Pattern) {

        switch (Pattern) {

            case "PPG":

                return drive.actionBuilder(currentPose)

                        .setReversed(false)
                        .splineToConstantHeading(new Vector2d(18, 20), Math.toRadians(270))
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
                        .splineToConstantHeading(new Vector2d(18, -5), Math.toRadians(270), defaultSpeedConstraint)
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
                        .splineToConstantHeading(new Vector2d(18, -25), Math.toRadians(270), defaultSpeedConstraint)
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
                        .splineTo(new Vector2d(20, -35), Math.toRadians(270), defaultSpeedConstraint)

                        .build();
        }
    }

    public static Action collectArtifactsZoneTwo(MecanumDrive drive, Pose2d currentPose) {


        return drive.actionBuilder(currentPose)

                .splineTo(new Vector2d(63, -58), Math.toRadians(0), defaultSpeedConstraint)

                .build();
    }

    public static Action openChannel(MecanumDrive drive, Pose2d currentPose) {

        return drive.actionBuilder(currentPose)

                .setReversed(false)
                .strafeTo(new Vector2d(45,8), defaultSpeedConstraint)
                .splineToConstantHeading(new Vector2d(67,4), Math.toRadians(0), defaultSpeedConstraint)

                .waitSeconds(1)

                .build();
    }

    public static Action park(MecanumDrive drive, Pose2d currentPose) {

        return drive.actionBuilder(currentPose)

                .setReversed(false)
                .strafeTo(new Vector2d(35, -60), defaultSpeedConstraint)

                .build();
    }

    public static Action LongRangePark(MecanumDrive drive, Pose2d currentPose) {

        return drive.actionBuilder(currentPose)

                .setReversed(false)
                .strafeTo(new Vector2d(35, -60), defaultSpeedConstraint)

                .build();
    }

}
