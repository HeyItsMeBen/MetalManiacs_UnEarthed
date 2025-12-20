package org.firstinspires.ftc.teamcode.AutoCode.Pathings;

import com.acmerobotics.roadrunner.*;
import org.firstinspires.ftc.teamcode.AutoCode.Roadrunner.MecanumDrive;

import com.acmerobotics.roadrunner.Pose2d;

import org.firstinspires.ftc.teamcode.Hardware.Intake;
import org.firstinspires.ftc.teamcode.Hardware.Flywheels;

import java.lang.Math;

public class PathingTrajectories {

    static double defaultVelocity = 50.0;

    static double defaultAngVelocity = Math.PI;

    public static Action initializationPosition(MecanumDrive drive, Pose2d currentPose) {

        MinVelConstraint maxSpeedConstraint = new MinVelConstraint(
                java.util.Arrays.asList(
                        new TranslationalVelConstraint(defaultVelocity),
                        new AngularVelConstraint(defaultAngVelocity)
                )
        );

        return drive.actionBuilder(currentPose)

                .setReversed(true)
                .splineTo(new Vector2d(20, -30), Math.toRadians(90), maxSpeedConstraint)

                .build();
    }

    public static Action firingPosition(MecanumDrive drive, Pose2d currentPose, Flywheels flywheel) {

        MinVelConstraint maxSpeedConstraint = new MinVelConstraint(
                java.util.Arrays.asList(
                        new TranslationalVelConstraint(20),
                        new AngularVelConstraint(defaultAngVelocity)
                )
        );

        return drive.actionBuilder(currentPose)

                .splineTo(new Vector2d(18, 18), Math.toRadians(40), maxSpeedConstraint)
                .setReversed(false)

                .stopAndAdd(new BaseActions.runFlywheels(flywheel))

                .build();
    }

    public static Action PatternCollection(MecanumDrive drive, Pose2d currentPose, String Pattern, Intake intake) {

        MinVelConstraint maxSpeedConstraint = new MinVelConstraint(
                java.util.Arrays.asList(
                        new TranslationalVelConstraint(10),
                        new AngularVelConstraint(defaultAngVelocity)
                )
        );

        if (Pattern.equals("PPG")) {

            return drive.actionBuilder(currentPose)

                    .splineTo(new Vector2d(50,12), Math.toRadians(0), maxSpeedConstraint)
                    .setReversed(true)

                    .stopAndAdd(new BaseActions.runIntake(intake))

                    .build();

        }
        else if (Pattern.equals("PGP")) {

            return drive.actionBuilder(currentPose)

                    .splineTo(new Vector2d(50,-12), Math.toRadians(0), maxSpeedConstraint)
                    .setReversed(true)

                    .stopAndAdd(new BaseActions.runIntake(intake))

                    .build();

        }

        else if (Pattern.equals("GPP")) {

            return drive.actionBuilder(currentPose)

                    .strafeTo(new Vector2d(30, 30), maxSpeedConstraint)
                    .setReversed(true)

                    .build();

        }

        else {
            return drive.actionBuilder(currentPose)

                    .splineTo(new Vector2d(20, -30), Math.toRadians(270))

                    .stopAndAdd(new BaseActions.runIntake(intake))

                    .build();
        }
    }

    public static Action park(MecanumDrive drive, Pose2d currentPose, Flywheels flywheel, Intake intake) {

        MinVelConstraint maxSpeedConstraint = new MinVelConstraint(
                java.util.Arrays.asList(
                        new TranslationalVelConstraint(10),
                        new AngularVelConstraint(defaultAngVelocity)
                )
        );

        return drive.actionBuilder(currentPose)

                .stopAndAdd(new BaseActions.stopFlywheels(flywheel))
                .stopAndAdd(new BaseActions.stopIntake(intake))


                .splineTo(new Vector2d(20, -30), Math.toRadians(270))

                .build();
    }

}
