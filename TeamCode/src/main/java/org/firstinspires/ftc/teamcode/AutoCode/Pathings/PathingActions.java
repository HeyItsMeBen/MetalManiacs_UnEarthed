
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
        private final Telemetry telemetry;

        public InitializeTurretPositionZoneOneRed(Turret turret, Telemetry telemetry) {
            this.turret = turret;
            this.telemetry = telemetry;
        }
        @Override
        public boolean run(@NonNull TelemetryPacket telemetryPacket) {
            turret.resetInitial();
            turret.rotateToPosition(-375);
            telemetry.addData("Current Position: ", turret.getTurretPosition());
            telemetry.update();
            return false;
        }
    }

    public static class InitializeTurretPositionZoneTwoRed implements Action {

        private final Turret turret;
        private final Telemetry telemetry;

        public InitializeTurretPositionZoneTwoRed(Turret turret, Telemetry telemetry) {
            this.turret = turret;
            this.telemetry = telemetry;
        }
        @Override
        public boolean run(@NonNull TelemetryPacket telemetryPacket) {
            turret.resetInitial();
            turret.rotateToPosition(-450);
            telemetry.addData("Current Position: ", turret.getTurretPosition());
            telemetry.update();
            return false;
        }
    }

    public static class InitializeTurretPositionZoneOneBlue implements Action {

        private final Turret turret;
        private final Telemetry telemetry;

        public InitializeTurretPositionZoneOneBlue(Turret turret, Telemetry telemetry) {
            this.turret = turret;
            this.telemetry = telemetry;
        }
        @Override
        public boolean run(@NonNull TelemetryPacket telemetryPacket) {
            turret.resetInitial();
            turret.rotateToPosition(375);
            telemetry.addData("Current Position: ", turret.getTurretPosition());
            telemetry.update();
            return false;
        }
    }

    public static class InitializeTurretPositionZoneTwoBlue implements Action {

        private final Turret turret;
        private final Telemetry telemetry;


        public InitializeTurretPositionZoneTwoBlue(Turret turret, Telemetry telemetry) {
            this.turret = turret;
            this.telemetry = telemetry;
        }
        @Override
        public boolean run(@NonNull TelemetryPacket telemetryPacket) {
            turret.resetInitial();
            turret.rotateToPosition(450);
            telemetry.addData("Current Position: ", turret.getTurretPosition());
            telemetry.update();
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
            turret.rotateToPosition(0);
            turret.resetInitial();
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
        private boolean flywheelIsReady = false;
        private int artifactsLaunched = 0;
        private long lastShotTime = 0;
        private long spinUpStartTime = 0;
        private final long spinUpTimeout = 3000; // max 3 seconds to reach speed
        private double targetRPM = 0;
        private int timeBetweenLaunches = 250;
        private double launchDistance = 4.4;

        public firingSequence(Intake intake, Flywheels flywheels, Transfer transfer, int launchZone, Telemetry telemetry) {
            this.intake = intake;
            this.flywheels = flywheels;
            this.transfer = transfer;
            this.zone = launchZone;
            this.telemetry = telemetry;
        }

        @Override
        public boolean run(@NonNull TelemetryPacket telemetryPacket) {

            intake.runIntakeFullPower();

            if (!initialized) {
                launchDistance = (zone == 1) ? 4.9 : 10.3;
                targetRPM = flywheels.launchFromDistance(launchDistance);
                initialized = true;
                spinUpStartTime = System.currentTimeMillis();
            }

            // Telemetry for Driver Station
            telemetry.addData("Current RPM: ", flywheels.getFlywheelVelocity());
            telemetry.addData("Target RPM: ", targetRPM);
            telemetry.addData("Distance From Goal: ", launchDistance);
            telemetry.addData("Estimated Artifacts Fired: ", artifactsLaunched);
            telemetry.update();

            // Wait for flywheel to reach speed, but with timeout
            double currentRPM = flywheels.getFlywheelVelocity();
            if (!flywheelIsReady) {
                if (currentRPM >= targetRPM * 0.85) {
                    flywheelIsReady = true;
                    transfer.runTransfer();
                } else if (System.currentTimeMillis() - spinUpStartTime > spinUpTimeout) {
                    telemetry.addData("Status: ", "Timed Out");
                    telemetry.update();
                    flywheelIsReady = true;
                } else {
                    transfer.stopTransfer();
                    telemetry.addData("Status: ", "Waiting");
                    telemetry.update();
                    return true;
                }
            }

            long now = System.currentTimeMillis();
            if (artifactsLaunched <= 3 && now - lastShotTime > timeBetweenLaunches) {
                artifactsLaunched++;
                lastShotTime = now;
                telemetry.addData("Shot Fired", "");
                telemetry.update();
            }

            // Check if finished
            if (artifactsLaunched > 3) {
                flywheels.setFlywheelSpeed(targetRPM / 2);
                transfer.stopTransfer();
                return false;
            }

            return true;
        }
    }

    // ---------------------------------------------------------------------------------------------

//    @Deprecated
//    public static class firingSequenceWithTrapdoorAndBelt implements Action {
//
//        private final Intake intake;
//        private final Transfer belt;
//        private final Flywheels flywheels;
//        private final Transfer trapdoor;
//        private final int zone;
//
//        private boolean initialized = false;
//        private double targetRPM = 0;
//
//        private int shotsFired = 0;
//        private long lastShotTime = 0;
//
//        public firingSequenceWithTrapdoorAndBelt(Intake intake, Flywheels flywheels, Transfer belt, Transfer trapdoor, int launchZone) {
//            this.intake = intake;
//            this.flywheels = flywheels;
//            this.belt = belt;
//            this.trapdoor = trapdoor;
//            this.zone = launchZone;
//        }
//
//        @Override
//        public boolean run(@NonNull TelemetryPacket telemetryPacket) {
//
//            if (!initialized) {
//                int launchDistance = (zone == 1) ? 48 : 1000;
//                flywheels.launchFromDistance(launchDistance);
//                targetRPM = flywheels.getRPMFromDistance(launchDistance);
//
//                initialized = true;
//            }
//
//            // Wait for flywheels to reach speed
//            if (flywheels.getFlywheelVelocity() < targetRPM) {
//                intake.stopIntake();
//                belt.stopBelt();
//                return true;   // keep waiting
//            }
//
//            // Fire 4 shots, one every 300ms
//            long now = System.currentTimeMillis();
//            if (shotsFired < 4 && now - lastShotTime > 250) {
//                intake.runIntakeFullPower();
//                belt.runBelt();
//                shotsFired++;
//                lastShotTime = now;
//            } else {
//                intake.stopIntake();
//                belt.stopBelt();
//            }
//
//            // When done, shut everything down
//            if (shotsFired >= 4) {
//                flywheels.stopFlywheel();
//                return false;   // Action finished
//            }
//
//            //closes trapdoor
//            //trapdoor.trapdoorClose();
//
//            return false;
//        }
//    }
//
//    @Deprecated
//    public static class runIntakeAndTransfer implements Action {
//        private final Intake intake;
//
//        private final Transfer belt;
//
//        public runIntakeAndTransfer(Intake intake, Transfer belt) {
//
//            this.intake = intake;
//            this.belt = belt;
//
//        }
//
//        @Override
//        public boolean run(@NonNull TelemetryPacket telemetryPacket) {
//            try {
//                Thread.sleep(250);
//            } catch (InterruptedException e) {
//            }
//            intake.setIntakePower(1);
//            belt.setTransferPower(1);
//            return false;
//        }
//    }
//
//    @Deprecated
//    public static class stopIntakeAndTransfer implements Action {
//        private final Intake intake;
//
//        private final Transfer belt;
//
//        public stopIntakeAndTransfer(Intake intake, Transfer belt) {
//
//            this.intake = intake;
//            this.belt = belt;
//
//        }
//
//        @Override
//        public boolean run(@NonNull TelemetryPacket telemetryPacket) {
//            try {
//                Thread.sleep(250);
//            } catch (InterruptedException e) {
//            }
//            intake.setIntakePower(0);
//            belt.setTransferPower(0);
//            return false;
//        }
//    }

}

