package org.firstinspires.ftc.teamcode.AutoCode.Pathings.TrajectoryPaths.RedClose;

import com.acmerobotics.roadrunner.*;

import org.firstinspires.ftc.teamcode.AutoCode.Roadrunner.MecanumDrive;

import com.acmerobotics.roadrunner.Pose2d;

import java.lang.Math;

public class RedCloseTrajectories {

    static double defaultVelocity = 20.0;

    static double defaultAngVelocity = Math.PI;

    static double patternCollectionVelocity = 10.0;

    static double patternCollectionAngVelocity = Math.PI;


    static MinVelConstraint defaultSpeedConstraint = new MinVelConstraint(
            java.util.Arrays.asList(
                    new TranslationalVelConstraint(defaultVelocity),
                    new AngularVelConstraint(defaultAngVelocity)
            )
    );

    static MinVelConstraint patternCollectionConstraint = new MinVelConstraint(
            java.util.Arrays.asList(
                    new TranslationalVelConstraint(patternCollectionVelocity),
                    new AngularVelConstraint(patternCollectionAngVelocity)
            )
    );


    public static Action initialMoveToPosition(MecanumDrive drive, Pose2d currentPose) {

        return drive.actionBuilder(currentPose)

                .setReversed(true)
                .splineToLinearHeading(new Pose2d(18, 35, Math.toRadians(0)), Math.toRadians(90), defaultSpeedConstraint)

                .build();
    }

    public static Action collectPattern(MecanumDrive drive, Pose2d currentPose, String Pattern) {

        switch (Pattern) {

            case "PPG":

                return drive.actionBuilder(currentPose)

                        .setReversed(false)
                        .splineToSplineHeading(new Pose2d(50,12, Math.toRadians(0)), Math.toRadians(0), patternCollectionConstraint)

                        .build();

            case "PGP":

                return drive.actionBuilder(currentPose)

                        .setReversed(true)
                        .splineToSplineHeading(new Pose2d(30,-12, Math.toRadians(0)), Math.toRadians(0), defaultSpeedConstraint)
                        .splineToConstantHeading(new Vector2d(50, -12), Math.toRadians(0), patternCollectionConstraint)

                        .build();

            case "GPP":

                return drive.actionBuilder(currentPose)

                        .setReversed(false)
                        .splineToConstantHeading(new Vector2d(37, -35), Math.toRadians(0), patternCollectionConstraint)
                        .splineToConstantHeading(new Vector2d(50, -35), Math.toRadians(0), defaultSpeedConstraint)

                        .build();

            default:

                return drive.actionBuilder(currentPose)

                        .setReversed(false)
                        .splineTo(new Vector2d(45,6), Math.toRadians(0))

                        .build();
        }
    }

    public static Action firingPosition(MecanumDrive drive, Pose2d currentPose) {

        return drive.actionBuilder(currentPose)

                .setReversed(true)
                .splineToConstantHeading(new Vector2d(15, 10), Math.toRadians(90), defaultSpeedConstraint)

                .build();
    }

    public static Action openChannel(MecanumDrive drive, Pose2d currentPose) {

        return drive.actionBuilder(currentPose)

                .setReversed(false)
                .splineToSplineHeading(new Pose2d(58,-10, Math.toRadians(20)), Math.toRadians(0), defaultSpeedConstraint)

                .build();
    }

    public static Action park(MecanumDrive drive, Pose2d currentPose) {

        return drive.actionBuilder(currentPose)

                .setReversed(false)
                .splineTo(new Vector2d(45,6), Math.toRadians(0))

                .build();
    }

}
