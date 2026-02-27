package org.firstinspires.ftc.teamcode.AutoCode.Pathings.TrajectoryPaths;

import static java.lang.Thread.sleep;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.Actions;
import com.acmerobotics.roadrunner.Pose2d;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

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
                startTime = Actions.now();  // Road Runner time in seconds
                initialized = true;
            }

            double elapsed = Actions.now() - startTime;

            if (!aprilTagTurretAim.isTargetFound() || elapsed < 2) {
                aprilTagTurretAim.update2(false, false);
                return false;
            }

            // Time finished — execute once
            aprilTagTurretAim.stopTurret();

            lightsController.update(
                    aprilTagTurretAim.isTargetFound(),
                    intakeController.isIntakeRunning(),
                    teamColor,
                    ballSequence
            );

            return true; // action complete
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


        public LimelightScanAction(FlywheelController flywheel,
                                      DoubleSupplier distanceSupplier,
                                      BooleanSupplier targetFoundSupplier) {

        }

        @Override
        public boolean run(@NonNull TelemetryPacket packet) {

            return true;
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



