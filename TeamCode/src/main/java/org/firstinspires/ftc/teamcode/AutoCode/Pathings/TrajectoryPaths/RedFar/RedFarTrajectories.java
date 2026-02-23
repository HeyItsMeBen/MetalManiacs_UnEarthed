package org.firstinspires.ftc.teamcode.AutoCode.Pathings.TrajectoryPaths.RedFar;

import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.AngularVelConstraint;
import com.acmerobotics.roadrunner.MinVelConstraint;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.TranslationalVelConstraint;
import com.acmerobotics.roadrunner.Vector2d;

import org.firstinspires.ftc.teamcode.AutoCode.Roadrunner.MecanumDrive;

public class RedFarTrajectories {

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

        Pose2d NewPose = drive.localizer.getPose();

        return drive.actionBuilder(NewPose)
                .strafeTo(new Vector2d(12, -45), defaultSpeedConstraint)
                .build();
    }

    public static Action firingPosition(MecanumDrive drive, Pose2d currentPose) {

        Pose2d NewPose = drive.localizer.getPose();

        return drive.actionBuilder(currentPose)

                .setReversed(true)
                .splineToConstantHeading(new Vector2d(12, -45), Math.toRadians(180))

                .build();
    }

    public static Action collectArtifacts(MecanumDrive drive, Pose2d currentPose, String side) {

        Pose2d NewPose = drive.localizer.getPose();

        switch (side){

            case "Left":

                return drive.actionBuilder(currentPose)

                        .setReversed(false)
                        .splineTo(new Vector2d(60, -50), Math.toRadians(0), defaultSpeedConstraint)

                        .build();

            case "Middle":

                return drive.actionBuilder(currentPose)

                        .setReversed(false)
                        .splineTo(new Vector2d(60, -55), Math.toRadians(0), defaultSpeedConstraint)

                        .build();

            case "Right":

                return drive.actionBuilder(currentPose)

                        .setReversed(false)
                        .splineTo(new Vector2d(60, -60), Math.toRadians(0), defaultSpeedConstraint)

                        .build();

            default:

                return drive.actionBuilder(currentPose)

                        .setReversed(false)
                        .splineTo(new Vector2d(60, -55), Math.toRadians(0), defaultSpeedConstraint)

                        .build();
        }
    }

    public static Action park(MecanumDrive drive, Pose2d currentPose) {

        Pose2d NewPose = drive.localizer.getPose();

        return drive.actionBuilder(currentPose)

                .setReversed(false)
                .splineTo(new Vector2d(35,-55), Math.toRadians(0), defaultSpeedConstraint)

                .build();
    }



}
