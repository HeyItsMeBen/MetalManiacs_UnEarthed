package org.firstinspires.ftc.teamcode.AutoCode.Pathings;

import static java.lang.Thread.sleep;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.arcrobotics.ftclib.gamepad.GamepadKeys;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Hardware.Flywheels;
import org.firstinspires.ftc.teamcode.Hardware.Intake;
import org.firstinspires.ftc.teamcode.Hardware.Transfer;

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

    public static class powerUpFlywheels implements Action {
        private final Flywheels flywheels;
        private final int zone;

        public powerUpFlywheels(Flywheels flywheels, int zone) {
            this.flywheels = flywheels;
            this.zone = zone;
        }

        @Override
        public boolean run(@NonNull TelemetryPacket telemetryPacket) {
            double launchDistance = (zone == 1) ? 5.2 : 12.8;
            flywheels.launchFromDistance(launchDistance * 0.7);
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
        private int timeBetweenLaunches = 525;
        private double launchDistance = 6; // in feet

        public firingSequence(Intake intake, Flywheels flywheels, Transfer transfer, int launchZone, Telemetry telemetry) {
            this.intake = intake;
            this.flywheels = flywheels;
            this.transfer = transfer;
            this.zone = launchZone;
            this.telemetry = telemetry;
        }

        @Override
        public boolean run(@NonNull TelemetryPacket telemetryPacket) {

            if (!initialized) {
                launchDistance = (zone == 1) ? 5.8 : 12.6;
                targetRPM = flywheels.launchFromDistance(launchDistance);

                spinUpStartTime = System.currentTimeMillis();
                lastShotTime = spinUpStartTime;
                initialized = true;
            }

            flywheelIsReady=false;

//            autoAim.calculateEverything(desiredTag);
            double targetRPM = flywheels.launchFromDistance(launchDistance); //Use auto-aim to calculate and set the flywheel velocity.

            //wait 1 second to startup flywheels
            ElapsedTime transferTimer= new ElapsedTime();
            while (!flywheelIsReady) {
                if (flywheels.getCurrentWheelVelocity("") >= targetRPM * 0.85) {
                    flywheelIsReady = true;
                } else if (transferTimer.milliseconds()>1000) {
                    flywheelIsReady = true;
                }
            }
            double outtakeSpeedBeforeDrop = flywheels.getCurrentWheelVelocity("");

            //send the balls into the flywheel to launch

            for (int i = 0; i<3; i++) {
                if (i>0){
                    intake.setIntakePower(1);
                }
                transfer.setTransferPower(1);
                long startTime = System.currentTimeMillis();
                long timeout = 500;
                while (true) {
                    if (flywheels.getCurrentWheelVelocity("") < outtakeSpeedBeforeDrop - 150) {
                        break;
                    }
                    if (System.currentTimeMillis() - startTime > timeout) {
                        break;
                    }
                }
                transfer.setTransferPower(0);
                try {
                    sleep(500);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            intake.stopIntake();
            return false;

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

