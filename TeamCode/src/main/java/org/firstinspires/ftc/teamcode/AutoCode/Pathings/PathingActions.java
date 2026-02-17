package org.firstinspires.ftc.teamcode.AutoCode.Pathings;

import static java.lang.Thread.sleep;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.Actions;

import org.firstinspires.ftc.teamcode.Controllers.AutoAimTurretController;
import org.firstinspires.ftc.teamcode.Controllers.FlywheelController;
import org.firstinspires.ftc.teamcode.Controllers.IntakeController;
import org.firstinspires.ftc.teamcode.Controllers.LightsController;

import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;

//See if this sends through

public class PathingActions {

    public static class AutoAimAction implements Action {

        private final AutoAimTurretController aprilTagTurretAim;
        private final LightsController lightsController;
        private final IntakeController intakeController;
        private String teamColor;

        private double startTime;
        private boolean initialized = false;

        public AutoAimAction(
                AutoAimTurretController aprilTagTurretAim,
                LightsController lightsController,
                IntakeController intakeController,
                String teamColor
        ) {
            this.aprilTagTurretAim = aprilTagTurretAim;
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

            if (!aprilTagTurretAim.isTargetFound() || elapsed < 3.0) {
                aprilTagTurretAim.update(false, false);
                return false;
            }

            // Time finished — execute once
            aprilTagTurretAim.stopTurret();

            lightsController.update(
                    aprilTagTurretAim.isTargetFound(),
                    intakeController.isIntakeRunning(),
                    teamColor
            );

            return true; // action complete
        }
    }

    public static class FlywheelSequenceAction implements Action {

        private final FlywheelController flywheel;
        private final DoubleSupplier distanceSupplier;
        private final BooleanSupplier targetFoundSupplier;

        private boolean initialized = false;

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
                initialized = true;
            } else {
                flywheel.update(true, distance, tagVisible); // continue running
            }

            return (flywheel.getState() == FlywheelController.LaunchState.IDLE);
        }
    }

}



