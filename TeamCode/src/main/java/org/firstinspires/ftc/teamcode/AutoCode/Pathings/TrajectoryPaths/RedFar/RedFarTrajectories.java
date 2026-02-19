package org.firstinspires.ftc.teamcode.AutoCode.Pathings.TrajectoryPaths.RedFar;

import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.AngularVelConstraint;
import com.acmerobotics.roadrunner.MinVelConstraint;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.TranslationalVelConstraint;
import com.acmerobotics.roadrunner.Vector2d;

import org.firstinspires.ftc.teamcode.AutoCode.Roadrunner.MecanumDrive;

public class RedFarTrajectories {

    static double defaultVelocity = 10.0;

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

    public static Action firingPosition(MecanumDrive drive, Pose2d currentPose) {

        return drive.actionBuilder(currentPose)

                .setReversed(true)
                .splineToConstantHeading(new Vector2d(15, 10), Math.toRadians(90), defaultSpeedConstraint)

                .build();
    }

    public static Action collectArtifacts(MecanumDrive drive, Pose2d currentPose) {

        return drive.actionBuilder(currentPose)

                .setReversed(true)
                .splineToConstantHeading(new Vector2d(15, 10), Math.toRadians(90), defaultSpeedConstraint)

                .build();
    }

    public static Action park(MecanumDrive drive, Pose2d currentPose) {

        return drive.actionBuilder(currentPose)

                .setReversed(false)
                .splineTo(new Vector2d(45,6), Math.toRadians(0))

                .build();
    }

}
