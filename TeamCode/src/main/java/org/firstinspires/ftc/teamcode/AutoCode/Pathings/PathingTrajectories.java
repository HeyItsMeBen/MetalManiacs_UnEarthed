package org.firstinspires.ftc.teamcode.AutoCode.Pathings;

import com.acmerobotics.roadrunner.*;
import org.firstinspires.ftc.teamcode.AutoCode.Roadrunner.MecanumDrive;

import java.lang.Math;

public class PathingTrajectories {

    static double defaultVelocity = 50.0;

    static double defaultAngVelocity = Math.PI;

    public static Action buildTab1(MecanumDrive drive, Pose2d currentPose) {

        MinVelConstraint maxSpeedConstraint = new MinVelConstraint(
                java.util.Arrays.asList(
                        new TranslationalVelConstraint(20),
                        new AngularVelConstraint(Math.PI / 2)
                )
        );

        return drive.actionBuilder(currentPose)
                .strafeToLinearHeading(
                        new Vector2d(11, -26),
                        Math.toRadians(-90),
                        maxSpeedConstraint
                )
                .build();
    }

    public static Action buildTab2(MecanumDrive drive, Pose2d currentPose) {

        MinVelConstraint maxSpeedConstraint = new MinVelConstraint(
                java.util.Arrays.asList(
                        new TranslationalVelConstraint(defaultVelocity),
                        new AngularVelConstraint(defaultAngVelocity)
                )
        );

        return drive.actionBuilder(currentPose)
                .strafeToLinearHeading(
                        new Vector2d(11, -26),
                        Math.toRadians(-90),
                        maxSpeedConstraint
                )
                .build();
    }
}
