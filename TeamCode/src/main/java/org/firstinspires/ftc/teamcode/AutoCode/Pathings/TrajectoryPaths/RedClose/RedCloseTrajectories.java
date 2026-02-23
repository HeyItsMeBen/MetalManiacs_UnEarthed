package org.firstinspires.ftc.teamcode.AutoCode.Pathings.TrajectoryPaths.RedClose;

import com.acmerobotics.roadrunner.AngularVelConstraint;
import com.acmerobotics.roadrunner.MinVelConstraint;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.TrajectoryActionBuilder;
import com.acmerobotics.roadrunner.TranslationalVelConstraint;
import com.acmerobotics.roadrunner.Vector2d;
import org.firstinspires.ftc.teamcode.AutoCode.Roadrunner.MecanumDrive;

public class RedCloseTrajectories {

    private MecanumDrive drive;
    private Pose2d startPose;

    // Default velocity constraints (can be tuned)
    private static final double DEFAULT_VELOCITY = 30.0;
    private static final double DEFAULT_ANG_VELOCITY = Math.PI;
    private static final double PATTERN_COLLECTION_VELOCITY = 10.0;
    private static final double PATTERN_COLLECTION_ANG_VELOCITY = Math.PI;

    static MinVelConstraint defaultSpeedConstraint = new MinVelConstraint(
            java.util.Arrays.asList(
                    new TranslationalVelConstraint(DEFAULT_VELOCITY),
                    new AngularVelConstraint(DEFAULT_ANG_VELOCITY)
            )
    );

    static MinVelConstraint patternCollectionConstraint = new MinVelConstraint(
            java.util.Arrays.asList(
                    new TranslationalVelConstraint(PATTERN_COLLECTION_VELOCITY),
                    new AngularVelConstraint(PATTERN_COLLECTION_ANG_VELOCITY)
            )
    );

    public RedCloseTrajectories(MecanumDrive drive, Pose2d startPose) {
        this.drive = drive;
        this.startPose = startPose;
    }

    public TrajectoryActionBuilder initialMoveToPosition() {
        return drive.actionBuilder(startPose)
                .setReversed(true)
                .splineToSplineHeading(new Pose2d(15, 10, Math.toRadians(0)), Math.toRadians(270), defaultSpeedConstraint);
    }


    public TrajectoryActionBuilder collectPatternPPG(TrajectoryActionBuilder previousTrajectory) {
        return previousTrajectory.endTrajectory().fresh()
                .setReversed(false)
                .splineToSplineHeading(new Pose2d(50, 12, Math.toRadians(0)), Math.toRadians(0), patternCollectionConstraint);
    }

    public TrajectoryActionBuilder collectPatternPGP(TrajectoryActionBuilder previousTrajectory) {
        return previousTrajectory.endTrajectory().fresh()
                .setReversed(false)
                .splineToConstantHeading(new Vector2d(30, -12), Math.toRadians(0), defaultSpeedConstraint)
                .splineToConstantHeading(new Vector2d(50, -12), Math.toRadians(0), patternCollectionConstraint);
    }

    public TrajectoryActionBuilder collectPatternGPP(TrajectoryActionBuilder previousTrajectory) {
        return previousTrajectory.endTrajectory().fresh()
                .setReversed(false)
                .splineToConstantHeading(new Vector2d(37, -35), Math.toRadians(0), defaultSpeedConstraint)
                .splineToConstantHeading(new Vector2d(50, -35), Math.toRadians(0), patternCollectionConstraint);
    }

    public TrajectoryActionBuilder firingPosition(TrajectoryActionBuilder previousTrajectory) {
        return previousTrajectory.endTrajectory().fresh()
                .setReversed(true)
                .splineToConstantHeading(new Vector2d(15, 10), Math.toRadians(90), defaultSpeedConstraint);
    }

    public TrajectoryActionBuilder openChannel(TrajectoryActionBuilder previousTrajectory) {
        return previousTrajectory.endTrajectory().fresh()
                .setReversed(false)
                .splineToSplineHeading(new Pose2d(58, -10, Math.toRadians(20)), Math.toRadians(0), defaultSpeedConstraint);
    }

    public TrajectoryActionBuilder park(TrajectoryActionBuilder previousTrajectory) {
        return previousTrajectory.endTrajectory().fresh()
                .setReversed(false)
                .splineTo(new Vector2d(45, 6), Math.toRadians(0), defaultSpeedConstraint);
    }
}