package org.firstinspires.ftc.teamcode.AutoCode.Pathings;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;

import org.firstinspires.ftc.teamcode.Hardware.Flywheels;
import org.firstinspires.ftc.teamcode.Hardware.Intake;
import org.firstinspires.ftc.teamcode.Hardware.Transfer;

public class BaseActions {

    public static class runIntake implements Action {
        private final Intake intake;

        public runIntake(Intake intake) {
            this.intake = intake;
        }

        @Override
        public boolean run(@NonNull TelemetryPacket telemetryPacket) {
            try {
                Thread.sleep(250);
            } catch (InterruptedException e) {
            }
            intake.setMotorPower(-1);
            return false;
        }
    }

    public static class stopIntake implements Action {
        private final Intake intake;

        public stopIntake(Intake intake) {
            this.intake = intake;
        }

        @Override
        public boolean run(@NonNull TelemetryPacket telemetryPacket) {
            intake.setMotorPower(0);
            return false;
        }
    }

    public static class runFlywheels implements Action {
        private final Flywheels flywheels;

        public runFlywheels(Flywheels flywheels) {
            this.flywheels = flywheels;
        }

        @Override
        public boolean run(@NonNull TelemetryPacket telemetryPacket) {
            flywheels.runOptimalFlywheelVelocity(); //2350
            return false;
        }
    }

    public static class stopFlywheels implements Action {
        private final Flywheels flywheels;

        public stopFlywheels(Flywheels flywheels) {
            this.flywheels = flywheels;
        }

        @Override
        public boolean run(@NonNull TelemetryPacket telemetryPacket) {
            flywheels.setFlywheelVelocity(0);
            return false;
        }
    }
}

