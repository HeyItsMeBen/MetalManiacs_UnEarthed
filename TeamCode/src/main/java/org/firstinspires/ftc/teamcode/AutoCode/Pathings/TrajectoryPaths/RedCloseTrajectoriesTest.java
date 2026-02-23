package org.firstinspires.ftc.teamcode.AutoCode.Pathings.TrajectoryPaths;

import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.TrajectoryActionBuilder;
import com.acmerobotics.roadrunner.Vector2d;
import org.firstinspires.ftc.teamcode.AutoCode.Roadrunner.MecanumDrive;

public class RedCloseTrajectoriesTest {

    private final MecanumDrive drive;
    private final Pose2d startPose;

    private TrajectoryActionBuilder builder;

    public RedCloseTrajectoriesTest(MecanumDrive drive, Pose2d startPose) {
        this.drive = drive;
        this.startPose = startPose;
    }

    public RedCloseTrajectoriesTest initialMoveToPosition() {
        builder = drive.actionBuilder(startPose)
                .setReversed(true)
                .splineToSplineHeading(
                        new Pose2d(15, 10, Math.toRadians(0)),
                        Math.toRadians(270)
                );
        return this;
    }

    public RedCloseTrajectoriesTest collectPatternPGP() {
        builder.setReversed(false)
                .splineToConstantHeading(
                        new Vector2d(30, -12),
                        Math.toRadians(0)
                )
                .splineToConstantHeading(
                        new Vector2d(50, -12),
                        Math.toRadians(0)
                );
        return this;
    }

    public RedCloseTrajectoriesTest collectPatternPPG() {
        builder.setReversed(false)
                .splineToSplineHeading(
                        new Pose2d(50, 12, Math.toRadians(0)),
                        Math.toRadians(0)
                );
        return this;
    }

    public RedCloseTrajectoriesTest collectPatternGPP() {
        builder.setReversed(false)
                .splineToConstantHeading(
                        new Vector2d(37, -35),
                        Math.toRadians(0)
                )
                .splineToConstantHeading(
                        new Vector2d(50, -35),
                        Math.toRadians(0)
                );
        return this;
    }

    public RedCloseTrajectoriesTest firingPosition() {
        builder.setReversed(true)
                .splineToConstantHeading(
                        new Vector2d(15, 10),
                        Math.toRadians(90)
                );
        return this;
    }

    public RedCloseTrajectoriesTest openChannel() {
        builder.setReversed(false)
                .splineToSplineHeading(
                        new Pose2d(58, -10, Math.toRadians(20)),
                        Math.toRadians(0)
                );
        return this;
    }

    public RedCloseTrajectoriesTest park() {
        builder.setReversed(false)
                .splineTo(
                        new Vector2d(45, 6),
                        Math.toRadians(0)
                );
        return this;
    }

    /**
     * Builds and returns the final Action directly.
     */
    public Action build() {
        return builder.build();
    }
}