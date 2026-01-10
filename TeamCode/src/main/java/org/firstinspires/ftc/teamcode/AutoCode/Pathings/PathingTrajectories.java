package org.firstinspires.ftc.teamcode.AutoCode.Pathings;

import com.acmerobotics.roadrunner.*;
import org.firstinspires.ftc.teamcode.AutoCode.Roadrunner.MecanumDrive;

import com.acmerobotics.roadrunner.Pose2d;

import org.firstinspires.ftc.teamcode.Hardware.Intake;
import org.firstinspires.ftc.teamcode.Hardware.Flywheels;
import org.firstinspires.ftc.teamcode.Hardware.Transfer;

import java.lang.Math;

public class PathingTrajectories {

    static double defaultVelocity = 50.0;

    static double defaultAngVelocity = Math.PI;

    public static Action initialFiringFromWallZoneOne(MecanumDrive drive, Pose2d currentPose, Flywheels flywheel) {

        MinVelConstraint maxSpeedConstraint = new MinVelConstraint(
                java.util.Arrays.asList(
                        new TranslationalVelConstraint(defaultVelocity),
                        new AngularVelConstraint(defaultAngVelocity)
                )
        );

        return drive.actionBuilder(currentPose)

                .setReversed(true)
                .splineTo(new Vector2d(20, -30), Math.toRadians(90), maxSpeedConstraint)

                .splineToLinearHeading(new Pose2d(18, 18, Math.toRadians(0)), Math.toRadians(70), maxSpeedConstraint)

                //.stopAndAdd(new PathingActions.runFlywheels(flywheel))

                .build();

    }

    public static Action initialFiringFromGoalZoneOne(MecanumDrive drive, Pose2d currentPose, Flywheels flywheel) {

        MinVelConstraint maxSpeedConstraint = new MinVelConstraint(
                java.util.Arrays.asList(
                        new TranslationalVelConstraint(20),
                        new AngularVelConstraint(defaultAngVelocity)
                )
        );

        return drive.actionBuilder(currentPose)

                .setReversed(true)
                .splineToConstantHeading(new Vector2d(30, 50), Math.toRadians(225), maxSpeedConstraint)

                .splineToSplineHeading(new Pose2d(18, 18, Math.toRadians(0)), Math.toRadians(270), maxSpeedConstraint)

                //.stopAndAdd(new PathingActions.runFlywheels(flywheel))

                .build();

    }

    public static Action firingPositionZoneOne(MecanumDrive drive, Pose2d currentPose, Flywheels flywheel) {

        MinVelConstraint maxSpeedConstraint = new MinVelConstraint(
                java.util.Arrays.asList(
                        new TranslationalVelConstraint(20),
                        new AngularVelConstraint(defaultAngVelocity)
                )
        );

        return drive.actionBuilder(currentPose)

                .setReversed(true)
                .splineToLinearHeading(new Pose2d(18, 18, Math.toRadians(0)), Math.toRadians(45), maxSpeedConstraint)

                //.stopAndAdd(new PathingActions.runFlywheels(flywheel))

                .build();
    }

    public static Action firingPositionZoneTwo(MecanumDrive drive, Pose2d currentPose, Flywheels flywheel) {

        MinVelConstraint maxSpeedConstraint = new MinVelConstraint(
                java.util.Arrays.asList(
                        new TranslationalVelConstraint(20),
                        new AngularVelConstraint(defaultAngVelocity)
                )
        );

        return drive.actionBuilder(currentPose)

                .strafeToLinearHeading(new Vector2d(15, -47), Math.toRadians(90), maxSpeedConstraint)

                //.stopAndAdd(new PathingActions.runFlywheels(flywheel))

                .build();
    }

    public static Action PatternCollection(MecanumDrive drive, Pose2d currentPose, String Pattern, Intake intake, Transfer belt) {

        MinVelConstraint maxSpeedConstraint = new MinVelConstraint(
                java.util.Arrays.asList(
                        new TranslationalVelConstraint(10),
                        new AngularVelConstraint(defaultAngVelocity)
                )
        );

        if (Pattern.equals("PPG")) {

            return drive.actionBuilder(currentPose)

                    //.stopAndAdd(new PathingActions.runIntakeAndTransferForward(intake, belt))

                    .setReversed(false)
                    .splineTo(new Vector2d(50,12), Math.toRadians(0), maxSpeedConstraint)

                    //.strafeTo(new Vector2d(40, 18))
                    .build();

        }
        else if (Pattern.equals("PGP")) {

            return drive.actionBuilder(currentPose)

                    //.stopAndAdd(new PathingActions.runIntakeAndTransferForward(intake, belt))

                    .setReversed(false)
                    .splineTo(new Vector2d(50,-12), Math.toRadians(0), maxSpeedConstraint)

                    .build();

        }

        else if (Pattern.equals("GPP")) {

            return drive.actionBuilder(currentPose)

                    //.stopAndAdd(new PathingActions.runIntakeAndTransferForward(intake, belt))

                    .setReversed(false)
                    .strafeTo(new Vector2d(50, -35), maxSpeedConstraint)

                    .build();

        }

        else {
            return drive.actionBuilder(currentPose)

                    //.stopAndAdd(new PathingActions.runIntakeAndTransferForward(intake, belt))

                    .setReversed(false)
                    .splineTo(new Vector2d(20, -35), Math.toRadians(270), maxSpeedConstraint)

                    .build();
        }
    }

    public static Action collectArtifactsZoneTwo(MecanumDrive drive, Pose2d currentPose, Intake intake, Transfer belt) {

        MinVelConstraint maxSpeedConstraint = new MinVelConstraint(
                java.util.Arrays.asList(
                        new TranslationalVelConstraint(20),
                        new AngularVelConstraint(defaultAngVelocity)
                )
        );

        return drive.actionBuilder(currentPose)

                .strafeTo(new Vector2d(60, -55), maxSpeedConstraint)

                //.stopAndAdd(new PathingActions.runIntakeAndTransferForward(intake, belt))

                .build();
    }

    public static Action openChannel(MecanumDrive drive, Pose2d currentPose, Intake intake, Transfer belt) {

        MinVelConstraint maxSpeedConstraint = new MinVelConstraint(
                java.util.Arrays.asList(
                        new TranslationalVelConstraint(10),
                        new AngularVelConstraint(defaultAngVelocity)
                )
        );

        return drive.actionBuilder(currentPose)

                //.stopAndAdd(new PathingActions.stopIntakeAndTransfer(intake, belt))

                .setReversed(false)
                .strafeTo(new Vector2d(45,8), maxSpeedConstraint)
                .splineToConstantHeading(new Vector2d(52,5), Math.toRadians(0), maxSpeedConstraint)

                .waitSeconds(1)

                .build();
    }

    public static Action park(MecanumDrive drive, Pose2d currentPose, Flywheels flywheel, Intake intake, Transfer belt) {

        MinVelConstraint maxSpeedConstraint = new MinVelConstraint(
                java.util.Arrays.asList(
                        new TranslationalVelConstraint(10),
                        new AngularVelConstraint(defaultAngVelocity)
                )
        );

        return drive.actionBuilder(currentPose)

                //.stopAndAdd(new PathingActions.stopFlywheels(flywheel))
                //.stopAndAdd(new PathingActions.stopIntakeAndTransfer(intake, belt))

                .setReversed(false)
                .splineTo(new Vector2d(20, -35), Math.toRadians(270), maxSpeedConstraint)

                .build();
    }

}
