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

    public static class setTurretPositionZoneOne implements Action {

        private final Turret turret;

        public setTurretPositionZoneOne(Turret turret) {
            this.turret = turret;
        }
        @Override
        public boolean run(@NonNull TelemetryPacket telemetryPacket) {
            turret.resetPosition();
            turret.rotateToPosition(375);
            return false;
        }
    }

    public static class setTurretPositionZoneTwo implements Action {

        private final Turret turret;

        public setTurretPositionZoneTwo(Turret turret) {
            this.turret = turret;
        }
        @Override
        public boolean run(@NonNull TelemetryPacket telemetryPacket) {
            turret.resetInitial();
            turret.rotateToPosition(375);
            return false;
        }
    }

    public static class endingTurretPosition implements Action {

        private final Turret turret;

        public endingTurretPosition(Turret turret) {
            this.turret = turret;
        }
        @Override
        public boolean run(@NonNull TelemetryPacket telemetryPacket) {
            turret.resetPosition();
            return false;
        }
    }

    public static class firingSequence implements Action {

        private final Intake intake;
        private final Transfer belt;
        private final Flywheels flywheels;
        private final Transfer trapdoor;
        private final int zone;

        public firingSequence(Intake intake, Flywheels flywheels, Transfer belt, Transfer trapdoor, int launchZone) {
            this.intake = intake;
            this.flywheels = flywheels;
            this.belt = belt;
            this.trapdoor = trapdoor;
            this.zone = launchZone;
        }

        @Override
        public boolean run(@NonNull TelemetryPacket telemetryPacket) {

            //opens trapdoor
            trapdoor.trapdoorOpen();

            //prepares flywheels
            int launchDistance = 48;
            if (zone == 1) {
                launchDistance = 48;
            }
            else if (zone == 2) {
                launchDistance = 1000; //not calculated yet
            }

            //sequence runs 3 times
            for (int t = 0; t < 4; t++) {
                flywheels.launchFromDistance(launchDistance);
                for (int i = 0; i < 12 && flywheels.returnWheelVelocity() < flywheels.launchFromDistance(launchDistance); i++) { //waits for flywheels to catch up to speed
                    try { Thread.sleep(250); } catch (InterruptedException e) { }
                    intake.stopIntake();
                    belt.stopBelt();
                }
                intake.runIntakeFullPower();
                belt.runTransfer();
            }

            //turns flywheels off
            flywheels.stopFlywheel();

            //closes trapdoor
            trapdoor.trapdoorClose();

            return false;
        }
    }

}

