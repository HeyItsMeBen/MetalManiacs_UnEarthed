package org.firstinspires.ftc.teamcode.AutoCode.Pathings;

import static java.lang.Thread.sleep;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
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

    public static class maintainIntake implements Action {
        private final Intake intake;

        public maintainIntake(Intake intake) {

            this.intake = intake;

        }

        @Override
        public boolean run(@NonNull TelemetryPacket telemetryPacket) {
            intake.setIntakePower(0.25);
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
            double launchDistance = (zone == 1) ? 4.9 : 12.8;
            flywheels.launchFromDistance(launchDistance);
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
            flywheels.setFlywheelSpeedRaw(0);
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
        private double targetSpeed = 0;
        private int timeBetweenLaunches = 525;
        private double launchDistance = 62; // in inches

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
                launchDistance = (zone == 1) ? 62 : 114;
                targetSpeed = flywheels.launchFromDistance(launchDistance);

                spinUpStartTime = System.currentTimeMillis();
                lastShotTime = spinUpStartTime;
                initialized = true;
            }

            flywheelIsReady=false;

//            autoAim.calculateEverything(desiredTag);
            targetSpeed = flywheels.launchFromDistance(launchDistance); //Use auto-aim to calculate and set the flywheel velocity.

            //wait 1 second to startup flywheels
            ElapsedTime transferTimer= new ElapsedTime();
            while (!flywheelIsReady) {
                if (flywheels.getFlywheelSpeedRaw() >= targetSpeed * 0.9) {
                    flywheelIsReady = true;
                } else if (transferTimer.milliseconds()>1000) {
                    flywheelIsReady = true;
                }
            }
            double outtakeSpeedBeforeDrop = flywheels.getFlywheelRPM();

            //send the balls into the flywheel to launch

            for (int i = 0; i<3; i++) {
                if (i>0){
                    intake.setIntakePower(1);
                }
                transfer.setTransferPower(1);
                long startTime = System.currentTimeMillis();
                long timeout = 2000;
                while (true) {
                    if (flywheels.getFlywheelRPM() < outtakeSpeedBeforeDrop - 150) {
                        break;
                    }
                    if (System.currentTimeMillis() - startTime > timeout) {
                        break;
                    }
                }
                transfer.setTransferPower(0);
                try {
                    sleep(250);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            intake.stopIntake();
            transfer.stopTransfer();
            return false;

        }
    }
}

