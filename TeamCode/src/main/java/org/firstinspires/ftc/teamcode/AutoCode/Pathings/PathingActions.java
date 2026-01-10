package org.firstinspires.ftc.teamcode.AutoCode.Pathings;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;

import org.firstinspires.ftc.teamcode.Hardware.Flywheels;
import org.firstinspires.ftc.teamcode.Hardware.Intake;
import org.firstinspires.ftc.teamcode.Hardware.Transfer;
import org.firstinspires.ftc.teamcode.Hardware.Turret;

public class PathingActions {

    public static class runIntakeAndTransfer implements Action {
        private final Intake intake;

        private final Transfer belt;

        public runIntakeAndTransfer(Intake intake, Transfer belt) {

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

    public static class openTrapdoor implements Action {
        private final Transfer trapdoor;

        public openTrapdoor(Transfer trapdoor) {
            this.trapdoor = trapdoor;
        }

        @Override
        public boolean run(@NonNull TelemetryPacket telemetryPacket) {
            trapdoor.trapdoorOpen(); //Find new values later
            return false;
        }
    }

    public static class closeTrapdoor implements Action {
        private final Transfer trapdoor;

        public closeTrapdoor(Transfer trapdoor) {
            this.trapdoor = trapdoor;
        }

        @Override
        public boolean run(@NonNull TelemetryPacket telemetryPacket) {
            trapdoor.trapdoorClose(); //Find new values later
            return false;
        }
    }

    public class setTurretPosition implements Action {

        private final Turret turret;

        public setTurretPosition(Turret turret) {
            this.turret = turret;
        }
        @Override
        public boolean run(@NonNull TelemetryPacket telemetryPacket) {
            turret.resetPosition();
            turret.rotateToPosition(375);
            return false;
        }
    }

    public class endTurretPosition implements Action {

        private final Turret turret;

        public endTurretPosition(Turret turret) {
            this.turret = turret;
        }
        @Override
        public boolean run(@NonNull TelemetryPacket telemetryPacket) {
            turret.rotateToPosition(0);
            turret.resetPosition();
            return false;
        }
    }

    public static class firingSequence implements Action {

        private final Intake intake;
        private final Transfer belt;
        private final Flywheels flywheels;
        private final Transfer trapdoor;

        public firingSequence(Intake intake, Flywheels flywheels, Transfer trapdoor, Transfer belt) {
            this.intake = intake;
            this.flywheels = flywheels;
            this.belt = belt;
            this.trapdoor = trapdoor;
        }

        @Override
        public boolean run(@NonNull TelemetryPacket telemetryPacket) {

            trapdoor.trapdoorOpen();

            flywheels.setFlywheelVelocity(2350);
            
            try { Thread.sleep(500); } catch (InterruptedException e) {}

            intake.runIntakeFullPower();
            belt.runTransfer();

            flywheels.setFlywheelVelocity(0);

            return false;
        }
    }

}

