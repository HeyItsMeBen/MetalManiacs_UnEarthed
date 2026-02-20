package org.firstinspires.ftc.teamcode.AutoCode.Pathings;

import com.acmerobotics.roadrunner.*;

import org.firstinspires.ftc.teamcode.AutoCode.Roadrunner.MecanumDrive;

import com.acmerobotics.roadrunner.Pose2d;

import org.firstinspires.ftc.teamcode.Hardware.Intake;
import org.firstinspires.ftc.teamcode.Hardware.Flywheels;
import org.firstinspires.ftc.teamcode.Hardware.Transfer;

import java.lang.Math;

public class BernieRedPathingClasses {

    static double defaultVelocity = 10.0;

    static double defaultAngVelocity = Math.PI;

    static MinVelConstraint defaultSpeedConstraint = new MinVelConstraint(
            java.util.Arrays.asList(
                    new TranslationalVelConstraint(defaultVelocity),
                    new AngularVelConstraint(defaultAngVelocity)
            )
    );

    public static Action firePreload(MecanumDrive drive, Pose2d currentPose, Intake intake, Flywheels flywheel) {

        return drive.actionBuilder(currentPose)

                .setReversed(true)

                //shoots as it drives backwards
                .splineToConstantHeading(new Vector2d(18, 7), Math.toRadians(270))
//                        .waitSeconds(0.5f)

                .build();

    }

    public static Action getMiddleBalls(MecanumDrive drive, Pose2d currentPose, Intake intake, Flywheels flywheel) {

        return drive.actionBuilder(currentPose)

                .stopAndAdd(new PathingActions.powerUpFlywheels(flywheel))

                .stopAndAdd(new PathingActions.maintainIntake(intake))

                .splineToSplineHeading(new Pose2d(30,-12, Math.toRadians(0)), Math.toRadians(0))
                .splineToConstantHeading(new Vector2d(50, -12), Math.toRadians(0))
                .waitSeconds(0.25f)

                .build();

    }

    public static Action getInPosition1(MecanumDrive drive, Pose2d currentPose, Intake intake, Flywheels flywheel) {

        return drive.actionBuilder(currentPose)

                .stopAndAdd(new PathingActions.maintainIntake(intake))
                .stopAndAdd(new PathingActions.powerUpFlywheels(flywheel))

                .splineToConstantHeading(new Vector2d(15, 2), Math.toRadians(180))
                .waitSeconds(0.5f)
                .setReversed(false)

                .build();
    }

    public static Action getInPosition2(MecanumDrive drive, Pose2d currentPose, Intake intake) {

        return drive.actionBuilder(currentPose)

                .splineToConstantHeading(new Vector2d(15, 12), Math.toRadians(90))
                .waitSeconds(0.5f)
                .setReversed(false)

                .build();
    }

    public static Action getGateBalls(MecanumDrive drive, Pose2d currentPose, Intake intake) {

        return drive.actionBuilder(currentPose)

                //get balls from gate
                .splineToSplineHeading(new Pose2d(58,-10, Math.toRadians(20)), Math.toRadians(0))
                .waitSeconds(0.5f)
                .setReversed(true)

                .build();
    }

    public static Action getTopBalls(MecanumDrive drive, Pose2d currentPose, Intake intake) {

        return drive.actionBuilder(currentPose)

                .splineToSplineHeading(new Pose2d(50,12, Math.toRadians(0)), Math.toRadians(0))
                .waitSeconds(0.25f)
                .setReversed(true)

                .build();
    }

    public static Action getInPosition3(MecanumDrive drive, Pose2d currentPose, Intake intake) {

        return drive.actionBuilder(currentPose)

                .splineToConstantHeading(new Vector2d(15, 12), Math.toRadians(0))
                .waitSeconds(0.5f)
                .setReversed(false)

                .build();
    }

    public static Action getBottomBalls(MecanumDrive drive, Pose2d currentPose, Intake intake) {

        return drive.actionBuilder(currentPose)

                //pick up balls from the bottom
                .splineToConstantHeading(new Vector2d(37, -35), Math.toRadians(0))
                .splineToConstantHeading(new Vector2d(50, -35), Math.toRadians(0))
                .setReversed(true)

                .build();
    }

    public static Action getInPosition4(MecanumDrive drive, Pose2d currentPose, Intake intake) {

        return drive.actionBuilder(currentPose)

                .splineToConstantHeading(new Vector2d(15, 6), Math.toRadians(90))
                .waitSeconds(0.5f)
                .setReversed(false)

                .build();
    }

    public static Action park(MecanumDrive drive, Pose2d currentPose, Intake intake) {

        return drive.actionBuilder(currentPose)

                .splineToLinearHeading(new Pose2d(45,6, Math.toRadians(0)), Math.toRadians(0))

                .build();
    }
    public static Action fire(Intake intake, Flywheels flywheel, Transfer wheels, double distance) {
        return new PathingActions.firingSequence(intake, flywheel, wheels, distance);
    }
    public static Action fireFar(MecanumDrive drive, Pose2d currentPose, Intake intake, Flywheels flywheel, Transfer wheels, double distance) {
        return new PathingActions.firingSequenceFar(intake, flywheel, wheels, distance);
    }

}
