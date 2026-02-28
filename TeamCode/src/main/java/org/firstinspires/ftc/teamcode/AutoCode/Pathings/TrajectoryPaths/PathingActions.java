package org.firstinspires.ftc.teamcode.AutoCode.Pathings.TrajectoryPaths;

import static org.firstinspires.ftc.teamcode.AutoCode.Pathings.TrajectoryPaths.RedFar.RedFarTrajectories.collectArtifactsMiddle;
import static java.lang.Thread.sleep;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.Actions;
import com.acmerobotics.roadrunner.Pose2d;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.AutoCode.Pathings.TrajectoryPaths.BlueFar.BlueFarTrajectories;
import org.firstinspires.ftc.teamcode.AutoCode.Pathings.TrajectoryPaths.RedFar.RedFarTrajectories;
import org.firstinspires.ftc.teamcode.AutoCode.Roadrunner.MecanumDrive;
import org.firstinspires.ftc.teamcode.Controllers.AutoAimTurretController;
import org.firstinspires.ftc.teamcode.Controllers.FlywheelController;
import org.firstinspires.ftc.teamcode.Controllers.IntakeController;
import org.firstinspires.ftc.teamcode.Controllers.LightsController;

import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;

//See if this sends through

public class PathingActions {

    public static class AimTurretAction implements Action {

        private final AutoAimTurretController aprilTagTurretAim;
        private final LightsController lightsController;
        private final IntakeController intakeController;
        private String teamColor;

        private double startTime;
        private boolean initialized = false;
        public String ballSequence = "XXX";


        public AimTurretAction(
                AutoAimTurretController autoAimTurretController,
                LightsController lightsController,
                IntakeController intakeController,
                String teamColor
        ) {
            this.aprilTagTurretAim = autoAimTurretController;
            this.lightsController = lightsController;
            this.intakeController = intakeController;
            this.teamColor = teamColor;
        }

        @Override
        public boolean run(@NonNull TelemetryPacket packet) {

            if (!initialized) {
                startTime = Actions.now();
                initialized = true;
            }

            double elapsed = Actions.now() - startTime;

            if (!aprilTagTurretAim.isTargetFound() || elapsed < 4) {
                aprilTagTurretAim.update2(false, false);
                return false;
            }

            aprilTagTurretAim.stopTurret();

            lightsController.update(
                    aprilTagTurretAim.isTargetFound(),
                    intakeController.isIntakeRunning(),
                    teamColor,
                    ballSequence
            );

            return true;
        }
    }

    public static class FlywheelSequenceAction implements Action {

        private final FlywheelController flywheel;
        private final DoubleSupplier distanceSupplier;
        private final BooleanSupplier targetFoundSupplier;

        private boolean initialized = false;
        private double LoggedDistance = 36;

        public FlywheelSequenceAction(FlywheelController flywheel,
                                      DoubleSupplier distanceSupplier,
                                      BooleanSupplier targetFoundSupplier) {
            this.flywheel = flywheel;
            this.distanceSupplier = distanceSupplier;
            this.targetFoundSupplier = targetFoundSupplier;
        }

        @Override
        public boolean run(@NonNull TelemetryPacket packet) {
            double distance = distanceSupplier.getAsDouble();
            boolean tagVisible = targetFoundSupplier.getAsBoolean();

            if (!initialized) {
                flywheel.update(true, distance, tagVisible); // trigger once
                LoggedDistance = distance;
                initialized = true;
            } else {
                flywheel.update(true, LoggedDistance, true); // continue running
            }

            return (flywheel.getState() == FlywheelController.LaunchState.IDLE);
        }
    }

    public static class LimelightScanAction implements Action {

        private final MecanumDrive drive;
        private Action selectedTrajectory;

        private boolean initialized = false;
        private double startTime;

        boolean isTeamColorRed = false;

        public LimelightScanAction(MecanumDrive drive, boolean isTeamColorRed) {
            this.drive = drive;
            this.isTeamColorRed = isTeamColorRed;
        }

        @Override
        public boolean run(@NonNull TelemetryPacket packet) {

            if (!initialized) {
                startTime = Actions.now();
                initialized = true;
            }

            double elapsed = Actions.now() - startTime;

            if (elapsed < 2.0) {
                drive.updatePoseEstimate();
                // Run limelight scan code here
                return false;
            }

            if (selectedTrajectory == null) {

                int visionOutputPosition = getVisionResult();

                if (isTeamColorRed) {
                    if (visionOutputPosition == 1) {
                        selectedTrajectory = RedFarTrajectories.collectArtifactsLeft(drive, drive.localizer.getPose());
                    } else if (visionOutputPosition == 2) {
                        selectedTrajectory = RedFarTrajectories.collectArtifactsMiddle(drive, drive.localizer.getPose());
                    } else if (visionOutputPosition == 3) {
                        selectedTrajectory = RedFarTrajectories.collectArtifactsRight(drive, drive.localizer.getPose());
                    } else {
                        selectedTrajectory = RedFarTrajectories.collectArtifactsMiddle(drive, drive.localizer.getPose());
                    }
                } else {
                    if (visionOutputPosition == 1) {
                        selectedTrajectory = BlueFarTrajectories.collectArtifactsLeft(drive, drive.localizer.getPose());
                    } else if (visionOutputPosition == 2) {
                        selectedTrajectory = BlueFarTrajectories.collectArtifactsMiddle(drive, drive.localizer.getPose());
                    } else if (visionOutputPosition == 3) {
                        selectedTrajectory = BlueFarTrajectories.collectArtifactsRight(drive, drive.localizer.getPose());
                    } else {
                        selectedTrajectory = BlueFarTrajectories.collectArtifactsMiddle(drive, drive.localizer.getPose());
                    }
                }
            }

            return selectedTrajectory.run(packet);
        }

        private int getVisionResult() {
            // Replace with your actual Limelight return
            return 2;
        }
    }

    public static abstract class PoseLoggerAction implements Action {
        MecanumDrive drive;
        LinearOpMode opMode;

        public PoseLoggerAction(MecanumDrive drive, LinearOpMode opMode) {
            this.drive = drive;
            this.opMode = opMode;
        }

        @Override
        public boolean run(@NonNull TelemetryPacket packet) {
            Pose2d currentPose = drive.localizer.getPose();
            opMode.telemetry.addData("Initial Estimated Pose: ", currentPose.position.y + ", " + currentPose.position.x + ", " + Math.toRadians(currentPose.heading.toDouble()));
            opMode.telemetry.addData("X", currentPose.position.x);
            opMode.telemetry.addData("Y", currentPose.position.y);
            opMode.telemetry.addData("Heading (degrees)", Math.toDegrees(currentPose.heading.toDouble()));
            opMode.telemetry.addData("Heading (radians)", Math.toRadians(currentPose.heading.toDouble()));
            opMode.telemetry.update();
            return false;
        }
    }

}



