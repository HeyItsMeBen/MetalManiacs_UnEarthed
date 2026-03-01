package org.firstinspires.ftc.teamcode.AutoCode.Pathings.TrajectoryPaths.RedClose;

import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.AngularVelConstraint;
import com.acmerobotics.roadrunner.MinVelConstraint;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.TranslationalVelConstraint;
import com.acmerobotics.roadrunner.Vector2d;

import org.firstinspires.ftc.teamcode.AutoCode.Pathings.TrajectoryPaths.PathingActions;
import org.firstinspires.ftc.teamcode.AutoCode.Roadrunner.MecanumDrive;

public class RedCloseTrajectories {

    static double defaultVelocity = 80.0;

    static double defaultAngVelocity = Math.PI;

    static double patternCollectionVelocity = 30.0;

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
                .splineToSplineHeading(new Pose2d(15, 10, Math.toRadians(0)), Math.toRadians(270), defaultSpeedConstraint)

                .build();
    }

    public static Action collectPatternPPG(MecanumDrive drive, Pose2d currentPose) {

        return drive.actionBuilder(currentPose)

                .setReversed(false)
                .splineToSplineHeading(new Pose2d(50,8, Math.toRadians(0)), Math.toRadians(0), patternCollectionConstraint)

                .build();
    }

    public static Action collectPatternPGP(MecanumDrive drive, Pose2d currentPose) {
        return drive.actionBuilder(currentPose)

                .setReversed(false)
                .splineToConstantHeading(new Vector2d(30,-14), Math.toRadians(0), defaultSpeedConstraint)
                .splineToConstantHeading(new Vector2d(50, -14), Math.toRadians(0), patternCollectionConstraint)

                .build();
    }

    public static Action collectPatternGPP(MecanumDrive drive, Pose2d currentPose) {
        return drive.actionBuilder(currentPose)

                .setReversed(false)
                .splineToConstantHeading(new Vector2d(37, -40), Math.toRadians(0), defaultSpeedConstraint)
                .splineToConstantHeading(new Vector2d(50, -40), Math.toRadians(0), patternCollectionConstraint)

                .build();

    }

    public static Action firingPosition(MecanumDrive drive, Pose2d currentPose) {

        return drive.actionBuilder(currentPose)

                .setReversed(true)
                .splineToSplineHeading(new Pose2d(20, 20, Math.toRadians(0)), Math.toRadians(90), defaultSpeedConstraint)
                //.splineToConstantHeading(new Vector2d(15, 10), Math.toRadians(90), defaultSpeedConstraint)

                .build();
    }

    public static Action openChannel(MecanumDrive drive, Pose2d currentPose) {

        return drive.actionBuilder(currentPose)

                .setReversed(false)
                .splineToSplineHeading(new Pose2d(62,5, Math.toRadians(90)), Math.toRadians(0), patternCollectionConstraint)
                .build();
    }

    public static Action park(MecanumDrive drive, Pose2d currentPose) {

        return drive.actionBuilder(currentPose)

                .setReversed(false)
                .splineTo(new Vector2d(45,6), Math.toRadians(0))

                .build();
    }

}
