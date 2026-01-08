package org.firstinspires.ftc.teamcode.AutoCode.Pathings;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;

import org.firstinspires.ftc.teamcode.Hardware.Flywheels;
import org.firstinspires.ftc.teamcode.Hardware.Intake;
import org.firstinspires.ftc.teamcode.Hardware.Transfer;

public class PathingActions {

    public static class runIntakeAndTransferForward implements Action {
        private final Intake intake;

        private final Transfer belt;

        public runIntakeAndTransferForward(Intake intake, Transfer belt) {

            this.intake = intake;
            this.belt = belt;

        }

        @Override
        public boolean run(@NonNull TelemetryPacket telemetryPacket) {
            try {
                Thread.sleep(250);
            } catch (InterruptedException e) {
            }
            intake.setIntakePower(1);
            belt.setTransferPower(1);
            return false;
        }
    }

    public static class runIntakeAndTransferBackward implements Action {
        private final Intake intake;

        private final Transfer belt;

        public runIntakeAndTransferBackward(Intake intake, Transfer belt) {

            this.intake = intake;
            this.belt = belt;

        }

        @Override
        public boolean run(@NonNull TelemetryPacket telemetryPacket) {
            try {
                Thread.sleep(250);
            } catch (InterruptedException e) {
            }
            intake.setIntakePower(-1);
            belt.setTransferPower(-1);
            return false;
        }
    }

    public static class stopIntakeAndTransfer implements Action {
        private final Intake intake;

        private final Transfer belt;

        public stopIntakeAndTransfer(Intake intake, Transfer belt) {

            this.intake = intake;
            this.belt = belt;

        }

        @Override
        public boolean run(@NonNull TelemetryPacket telemetryPacket) {
            try {
                Thread.sleep(250);
            } catch (InterruptedException e) {
            }
            intake.setIntakePower(0);
            belt.setTransferPower(0);
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
            flywheels.setFlywheelSpeed(2350); //Find new values later
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
            flywheels.setFlywheelSpeed(2350); //Find new values later
            return false;
        }
    }

}

