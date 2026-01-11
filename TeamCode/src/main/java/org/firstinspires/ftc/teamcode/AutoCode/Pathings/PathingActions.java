
        package org.firstinspires.ftc.teamcode.AutoCode.Pathings;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Hardware.Flywheels;
import org.firstinspires.ftc.teamcode.Hardware.Intake;
import org.firstinspires.ftc.teamcode.Hardware.Transfer;
import org.firstinspires.ftc.teamcode.Hardware.Turret;

//See if this sends through

public class PathingActions {

    public static class runIntake implements Action {
        private final Intake intake;


        public runIntake(Intake intake) {

            this.intake = intake;

        }

        @Override
        public boolean run(@NonNull TelemetryPacket telemetryPacket) {
            intake.runIntakeFullPower();
            return false;
        }
    }

    public static class maintainIntake implements Action {
        private final Intake intake;

        public maintainIntake(Intake intake) {

            this.intake = intake;

        }

        @Override
        public boolean run(@NonNull TelemetryPacket telemetryPacket) {
            intake.maintainIntakePower();
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
            intake.stopIntake();
            return false;
        }
    }

    public static class InitializeTurretPositionZoneOneRed implements Action {

        private final Turret turret;

        public InitializeTurretPositionZoneOneRed(Turret turret) {
            this.turret = turret;
        }
        @Override
        public boolean run(@NonNull TelemetryPacket telemetryPacket) {
            turret.resetPosition();
            turret.rotateToPosition(375);
            return false;
        }
    }

    public static class InitializeTurretPositionZoneTwoRed implements Action {

        private final Turret turret;

        public InitializeTurretPositionZoneTwoRed(Turret turret) {
            this.turret = turret;
        }
        @Override
        public boolean run(@NonNull TelemetryPacket telemetryPacket) {
            turret.resetPosition();
            turret.rotateToPosition(375);
            return false;
        }
    }

    public static class InitializeTurretPositionZoneOneBlue implements Action {

        private final Turret turret;

        public InitializeTurretPositionZoneOneBlue(Turret turret) {
            this.turret = turret;
        }
        @Override
        public boolean run(@NonNull TelemetryPacket telemetryPacket) {
            turret.resetPosition();
            turret.rotateToPosition(1125);
            return false;
        }
    }

    public static class InitializeTurretPositionZoneTwoBlue implements Action {

        private final Turret turret;

        public InitializeTurretPositionZoneTwoBlue(Turret turret) {
            this.turret = turret;
        }
        @Override
        public boolean run(@NonNull TelemetryPacket telemetryPacket) {
            turret.resetPosition();
            turret.rotateToPosition(1125);
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
        private final Transfer transfer;
        private final Flywheels flywheels;
        private final int zone;
        private final Telemetry telemetry;

        private boolean initialized = false;
        private double targetRPM = 0;

        private int shotsFired = 0;
        private long lastShotTime = 0;

        public firingSequence(Intake intake, Flywheels flywheels, Transfer transfer, int launchZone, Telemetry telemetry) {
            this.intake = intake;
            this.flywheels = flywheels;
            this.transfer = transfer;
            this.zone = launchZone;
            this.telemetry = telemetry;
        }

        @Override
        public boolean run(@NonNull TelemetryPacket telemetryPacket) {

            telemetry.addData("Current RPM: ", flywheels.getFlywheelVelocity());
            telemetry.addData("Target RPM: ", targetRPM);
            telemetry.update();

            intake.maintainIntakePower();

            if (!initialized) {
                int launchDistance = (zone == 1) ? 48 : 1000;
                flywheels.launchFromDistance(launchDistance);
                targetRPM = flywheels.getRPMFromDistance(launchDistance);
                initialized = true;
            }

            transfer.runTransfer();

            // Wait for flywheels to reach speed
            if (flywheels.getFlywheelVelocity() < targetRPM*0.75) {

                telemetry.addData("Status: ", "Waiting for flywheel");
                telemetry.update();

                transfer.stopTransfer();
                return true;   // keep waiting
            }

            // Fire 4 shots, one every 500ms
            long now = System.currentTimeMillis();
            if (shotsFired <= 3 && now - lastShotTime > 500) {
                shotsFired++;
                lastShotTime = now;
            }

            // When done, shut everything down
            if (shotsFired > 3) {
                flywheels.setFlywheelSpeed(targetRPM/2);
                return false;   // Action finished
            }
            return true;
        }
    }

    // ---------------------------------------------------------------------------------------------

    @Deprecated
    public static class firingSequenceWithTrapdoorAndBelt implements Action {

        private final Intake intake;
        private final Transfer belt;
        private final Flywheels flywheels;
        private final Transfer trapdoor;
        private final int zone;

        private boolean initialized = false;
        private double targetRPM = 0;

        private int shotsFired = 0;
        private long lastShotTime = 0;

        public firingSequenceWithTrapdoorAndBelt(Intake intake, Flywheels flywheels, Transfer belt, Transfer trapdoor, int launchZone) {
            this.intake = intake;
            this.flywheels = flywheels;
            this.belt = belt;
            this.trapdoor = trapdoor;
            this.zone = launchZone;
        }

        @Override
        public boolean run(@NonNull TelemetryPacket telemetryPacket) {

            if (!initialized) {
                int launchDistance = (zone == 1) ? 48 : 1000;
                flywheels.launchFromDistance(launchDistance);
                targetRPM = flywheels.getRPMFromDistance(launchDistance);

                initialized = true;
            }

            // Wait for flywheels to reach speed
            if (flywheels.getFlywheelVelocity() < targetRPM) {
                intake.stopIntake();
                belt.stopBelt();
                return true;   // keep waiting
            }

            // Fire 4 shots, one every 300ms
            long now = System.currentTimeMillis();
            if (shotsFired < 4 && now - lastShotTime > 250) {
                intake.runIntakeFullPower();
                belt.runBelt();
                shotsFired++;
                lastShotTime = now;
            } else {
                intake.stopIntake();
                belt.stopBelt();
            }

            // When done, shut everything down
            if (shotsFired >= 4) {
                flywheels.stopFlywheel();
                return false;   // Action finished
            }

            //closes trapdoor
            //trapdoor.trapdoorClose();

            return false;
        }
    }

    @Deprecated
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

    @Deprecated
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

}

