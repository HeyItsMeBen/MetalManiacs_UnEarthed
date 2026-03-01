package org.firstinspires.ftc.teamcode.AutoCode.Pathings.TrajectoryPaths;

import static org.firstinspires.ftc.teamcode.AutoCode.Pathings.TrajectoryPaths.RedFar.RedFarTrajectories.collectArtifactsMiddle;
import static java.lang.Thread.sleep;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.Actions;
import com.acmerobotics.roadrunner.Pose2d;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.AutoCode.Pathings.TrajectoryPaths.BlueFar.BlueFarTrajectories;
import org.firstinspires.ftc.teamcode.AutoCode.Pathings.TrajectoryPaths.RedFar.RedFarTrajectories;
import org.firstinspires.ftc.teamcode.AutoCode.Roadrunner.MecanumDrive;
import org.firstinspires.ftc.teamcode.Controllers.AutoAimTurretController;
import org.firstinspires.ftc.teamcode.Controllers.FlywheelController;
import org.firstinspires.ftc.teamcode.Controllers.IntakeController;
import org.firstinspires.ftc.teamcode.Controllers.LightsController;
import org.firstinspires.ftc.teamcode.Hardware.Flywheels;
import org.firstinspires.ftc.teamcode.Hardware.Intake;
import org.firstinspires.ftc.teamcode.Hardware.Transfer;

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

            if (startTime < 0) {
                startTime = System.currentTimeMillis();
            }

            aprilTagTurretAim.update2(false, false);

            if (System.currentTimeMillis() > startTime + 2000) {

                aprilTagTurretAim.setTurretPower(0);

                lightsController.update(
                        aprilTagTurretAim.isTargetFound(),
                        intakeController.isIntakeRunning(),
                        teamColor,
                        ballSequence
                );

                return true;  // finished
            }

            return false;  // keep running

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

        private boolean launchStarted = false;
        private boolean launchFinished = false;

        @Override
        public boolean run(@NonNull TelemetryPacket packet) {

            double distance = distanceSupplier.getAsDouble();
            boolean tagVisible = targetFoundSupplier.getAsBoolean();

            // Trigger only once
            if (!initialized) {
                flywheel.updateWithServoKickForAuto(true, distance, tagVisible);
                initialized = true;
                launchStarted = true;
                return true;
            }

            // Continue running state machine
            flywheel.updateWithServoKickForAuto(launchStarted, distance, tagVisible);

            // Detect when it returns to IDLE AFTER launching
            if (launchStarted &&
                    flywheel.getState() == FlywheelController.LaunchState.IDLE) {

                launchStarted = false; // stop retriggering
                launchFinished = true;
            }

            if (launchFinished) {
                // One extra loop with trigger false to cleanly stop
                flywheel.updateWithServoKickForAuto(false, distance, tagVisible);
                return false;
            }

            return true;
        }
    }

    public static class FlywheelSequenceActionDirect implements Action {

        private final Flywheels flywheels;
        private final Intake intake;
        private final Transfer transferDrum;
        private final Transfer transferServo;

        private final DoubleSupplier distanceSupplier;
        private final BooleanSupplier targetFoundSupplier;

        private boolean initialized = false;

        private long spinUpStartTime;
        private long shotStartTime;

        private int shotIndex = 0;

        private double targetSpeed = 1000;
        private double velocityDropThreshold;

        private enum State {
            SPINNING_UP,
            WAITING_FOR_READY,
            SHOOTING,
            DONE
        }

        private State state = State.SPINNING_UP;

        public FlywheelSequenceActionDirect(Flywheels flywheel, Intake intake, Transfer transferDrum, Transfer transferServo, DoubleSupplier distanceSupplier, BooleanSupplier targetFoundSupplier) {
            this.flywheels = flywheel;
            this.intake = intake;
            this.transferDrum = transferDrum;
            this.transferServo = transferServo;
            this.distanceSupplier = distanceSupplier;
            this.targetFoundSupplier = targetFoundSupplier;
        }

        @Override
        public boolean run(@NonNull TelemetryPacket packet) {

            double distance = distanceSupplier.getAsDouble();

            if (!initialized) {
                spinUpStartTime = System.currentTimeMillis();
                targetSpeed = flywheels.getVelocityFromDistance(distance);
                state = State.WAITING_FOR_READY;
                initialized = true;
            }

            switch (state) {

                case WAITING_FOR_READY:

                    flywheels.setFlywheelVelocity(targetSpeed);

                    if (flywheels.getFlywheelVelocity() >= targetSpeed * 0.9 ||
                            System.currentTimeMillis() - spinUpStartTime > 1000) {

                        velocityDropThreshold = flywheels.getFlywheelVelocity() - 150;
                        state = State.SHOOTING;
                        shotStartTime = System.currentTimeMillis();
                    }

                    return true;

                case SHOOTING:

                    flywheels.setFlywheelVelocity(targetSpeed);

                    if (shotIndex < 3) {

                        if (shotIndex > 0) {
                            intake.setIntakePower(1);
                        }

                        transferDrum.runTransferDrum(1);

                        if (flywheels.getFlywheelVelocity() < velocityDropThreshold ||
                                System.currentTimeMillis() - shotStartTime > 1500) {

                            transferDrum.runTransferDrum(0.2);
                            shotStartTime = System.currentTimeMillis();
                            shotIndex++;

                            if (shotIndex == 3) {
                                transferServo.setTransferKickUp();
                                try {
                                    sleep(500);
                                } catch (InterruptedException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        }

                        return true;
                    }

                    state = State.DONE;
                    return true;

                case DONE:

                    transferServo.setTransferKickDown();
                    transferDrum.stopTransferDrum();
                    //intake.setIntakePower(0);

                    return false;
            }

            return false;
        }
    }

    public static class FlywheelAutoAction implements Action {

        private final FlywheelController flywheel;
        private final DoubleSupplier distanceSupplier;
        private final BooleanSupplier tagVisibleSupplier;

        private boolean initialized = false;

        public FlywheelAutoAction(
                FlywheelController flywheel,
                DoubleSupplier distanceSupplier,
                BooleanSupplier tagVisibleSupplier) {

            this.flywheel = flywheel;
            this.distanceSupplier = distanceSupplier;
            this.tagVisibleSupplier = tagVisibleSupplier;
        }

        @Override
        public boolean run(@NonNull TelemetryPacket packet) {

            double distance = distanceSupplier.getAsDouble();
            boolean tagVisible = tagVisibleSupplier.getAsBoolean();

            if (!initialized) {
                flywheel.startAutoLaunch(distance, tagVisible);
                initialized = true;
            }

            flywheel.updateAuto(distance, tagVisible);

            return flywheel.isBusy();
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
            return 3;
        }
    }

    public static class WaitAction implements Action {

        private final long durationMs;
        private long startTime = -1;

        public WaitAction(long durationMs) {
            this.durationMs = durationMs;
        }

        @Override
        public boolean run(@NonNull TelemetryPacket packet) {
            if (startTime < 0) {
                startTime = System.currentTimeMillis();
            }
            return System.currentTimeMillis() - startTime >= durationMs;
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



