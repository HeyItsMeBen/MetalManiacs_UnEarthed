package org.firstinspires.ftc.teamcode.AutoCode.Pathings.TrajectoryPaths.BlueFar;

import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.AngularVelConstraint;
import com.acmerobotics.roadrunner.MinVelConstraint;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.TranslationalVelConstraint;
import com.acmerobotics.roadrunner.Vector2d;

import org.firstinspires.ftc.teamcode.AutoCode.Roadrunner.MecanumDrive;

public class BlueFarTrajectories {

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
                .strafeTo(new Vector2d(-12, -45), defaultSpeedConstraint)
                .build();
    }

    public static Action moveToScanPosition(MecanumDrive drive, Pose2d currentPose) {

        return drive.actionBuilder(currentPose)
                .splineTo(new Vector2d(52, -55), Math.toRadians(180), defaultSpeedConstraint)
                .build();
    }

    public static Action firingPosition(MecanumDrive drive, Pose2d currentPose) {

        return drive.actionBuilder(currentPose)

                .setReversed(true)
                .splineToConstantHeading(new Vector2d(-12, -45), Math.toRadians(0), defaultSpeedConstraint)

                .build();
    }

    public static Action collectArtifactsLeft(MecanumDrive drive, Pose2d currentPose) {

        return drive.actionBuilder(currentPose)

                .setReversed(false)
                .splineToConstantHeading(new Vector2d(-60, -50), Math.toRadians(180), patternCollectionConstraint)
                .build();
    }

    public static Action collectArtifactsMiddle(MecanumDrive drive, Pose2d currentPose) {

        return drive.actionBuilder(currentPose)

                .setReversed(false)
                .splineToConstantHeading(new Vector2d(-60, -55), Math.toRadians(180), patternCollectionConstraint)
                .build();
    }

    public static Action collectArtifactsRight(MecanumDrive drive, Pose2d currentPose) {

        return drive.actionBuilder(currentPose)

                .setReversed(false)
                .splineToConstantHeading(new Vector2d(-60, -60), Math.toRadians(180), patternCollectionConstraint)
                .build();
    }

    public static Action park(MecanumDrive drive, Pose2d currentPose) {

        return drive.actionBuilder(currentPose)

                .setReversed(false)
                .splineToConstantHeading(new Vector2d(-35,-55), Math.toRadians(180), defaultSpeedConstraint)

                .build();
    }

}
