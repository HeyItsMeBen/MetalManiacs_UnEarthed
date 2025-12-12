package org.firstinspires.ftc.teamcode.AutoCode.Pathings;

import com.acmerobotics.roadrunner.*;
import org.firstinspires.ftc.teamcode.AutoCode.Roadrunner.MecanumDrive;

import java.lang.Math;

public class PathingTrajectories {

    static double defaultVelocity = 50.0;

    static double defaultAngVelocity = Math.PI;

    public static Action aimingPosition(MecanumDrive drive, Pose2d currentPose) {

        MinVelConstraint maxSpeedConstraint = new MinVelConstraint(
                java.util.Arrays.asList(
                        new TranslationalVelConstraint(10),
                        new AngularVelConstraint(defaultAngVelocity)
                )
        );

        return drive.actionBuilder(currentPose)

                .setReversed(true)
                .splineTo(new Vector2d(20, -30), Math.toRadians(90), maxSpeedConstraint)
                .splineTo(new Vector2d(18, 18), Math.toRadians(40), maxSpeedConstraint)

                .build();
    }

    public static Action forwardMovement(MecanumDrive drive, Pose2d currentPose) {

        MinVelConstraint maxSpeedConstraint = new MinVelConstraint(
                java.util.Arrays.asList(
                        new TranslationalVelConstraint(30),
                        new AngularVelConstraint(defaultAngVelocity)
                )
        );

        return drive.actionBuilder(currentPose)

                .setReversed(true)
                .splineTo(new Vector2d(20, -30), Math.toRadians(40), maxSpeedConstraint)

                .build();
    }
}
