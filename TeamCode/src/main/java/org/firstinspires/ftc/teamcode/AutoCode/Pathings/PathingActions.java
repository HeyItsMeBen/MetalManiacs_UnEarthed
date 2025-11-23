package org.firstinspires.ftc.teamcode.AutoCode.Pathings;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.Systems.Transfer;
import org.firstinspires.ftc.teamcode.Systems.Intake;
import org.firstinspires.ftc.teamcode.Systems.Flywheels;

public class PathingActions {

    public static class runIntake implements Action {
        private final Intake intake;
        private final Transfer intakeHinge;

        public runIntake(Intake intake, Transfer intakeHinge) {
            this.intake = intake;
            this.intakeHinge = intakeHinge;
        }

        @Override
        public boolean run(@NonNull TelemetryPacket telemetryPacket) {
            intakeHinge.intakeHingeStandby();
            try { Thread.sleep(250); } catch (InterruptedException e) {}
            intake.setMotorPower(-1);
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
            intake.setMotorPower(-0.5);
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

    public static class maintainFlywheels implements Action {
        private final Flywheels flywheels;

        public maintainFlywheels(Flywheels flywheels) {
            this.flywheels = flywheels;
        }

        @Override
        public boolean run(@NonNull TelemetryPacket telemetryPacket) {
            flywheels.setFlywheelVelocity(800);
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

    public static class scoreBallSequence implements Action {
        private final Transfer intakeHinge;
        private final Transfer outtakeHinge;

        private final Flywheels flywheels;

        public scoreBallSequence(Transfer intakeHinge, Transfer outtakeHinge, Flywheels flywheels) {
            this.intakeHinge = intakeHinge;
            this.outtakeHinge = outtakeHinge;
            this.flywheels = flywheels;
        }

        @Override
        public boolean run(@NonNull TelemetryPacket telemetryPacket) {

            for (int t = 0; t < 12 && flywheels.getCurrentWheelVelocity("left") < 2200 && flywheels.getCurrentWheelVelocity("right") < 2200; t++) {
                try { Thread.sleep(250); } catch (InterruptedException e) { }
            }

            outtakeHinge.outtakeHingeFire();

            intakeHinge.intakeHingeStandby();

            try { Thread.sleep(500); } catch (InterruptedException e) {}

            outtakeHinge.outtakeHingeRelax();

            try { Thread.sleep(250); } catch (InterruptedException e) {}

            intakeHinge.intakeHingeLift();

            try { Thread.sleep(500); } catch (InterruptedException e) {}

            return false;
        }
    }
}
