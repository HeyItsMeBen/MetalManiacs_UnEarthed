package org.firstinspires.ftc.teamcode.AutoCode.Pathings.Red;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.acmerobotics.roadrunner.Action;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.Roadrunner.MecanumDrive;

import org.firstinspires.ftc.teamcode.Systems.Transfer;

import org.firstinspires.ftc.teamcode.Systems.Intake;
import org.firstinspires.ftc.teamcode.Systems.Flywheels;

@Autonomous(name = "Competition Pathing: Auto Cycle Direct Red", group = "Auto Pathing")
//@Disabled
public class AutoPathingCycleDirectRed extends LinearOpMode {

    Intake intake;
    Flywheels outtake;
    Transfer intakeHinge;
    Transfer outtakeHinge;

    double firing_position_x = 17;
    double firing_position_y = 17;

    @Override
    public void runOpMode() {

        Pose2d beginPose = new Pose2d(15, -60, Math.toRadians(270));
        MecanumDrive drive = new MecanumDrive(hardwareMap, beginPose);

        intake = new Intake(hardwareMap);
        outtake = new Flywheels(hardwareMap);
        intakeHinge = new Transfer(hardwareMap);
        outtakeHinge = new Transfer(hardwareMap);

        waitForStart();

        Actions.runBlocking(
                drive.actionBuilder(beginPose)

                        .setReversed(true)

                        .splineTo(new Vector2d(20, -30), Math.toRadians(90))
                        .splineTo(new Vector2d(firing_position_x, firing_position_y), Math.toRadians(45))

                        .stopAndAdd(new maintainIntake(hardwareMap))

                        .stopAndAdd(new runFlywheels(hardwareMap))

                        .stopAndAdd(new scoreBallSequence(hardwareMap))
                        .stopAndAdd(new scoreBallSequence(hardwareMap))
                        .stopAndAdd(new scoreBallSequence(hardwareMap))

                        .stopAndAdd(new stopFlywheels(hardwareMap))

                        .turn(Math.toRadians(135))

                        .stopAndAdd(new runIntake(hardwareMap))

                        .splineTo(new Vector2d(50, 6), Math.toRadians(0))

                        .stopAndAdd(new runFlywheels(hardwareMap))

                        .setTangent(180)
                        .splineToSplineHeading(new Pose2d(firing_position_x, firing_position_y, Math.toRadians(225)), Math.toRadians(135))

                        .stopAndAdd(new maintainIntake(hardwareMap))

                        .stopAndAdd(new scoreBallSequence(hardwareMap))
                        .stopAndAdd(new scoreBallSequence(hardwareMap))
                        .stopAndAdd(new scoreBallSequence(hardwareMap))

                        .stopAndAdd(new stopFlywheels(hardwareMap))

                        .stopAndAdd(new stopIntake(hardwareMap))

                        .strafeTo(new Vector2d(15, -40))

                        .build());
    }

    public class runIntake implements Action {
        public runIntake(HardwareMap hMap) {}
        public boolean run(@NonNull TelemetryPacket telemtryPacket)  {

            intakeHinge.intakeHingeStandby();

            sleep(250);

            intake.setMotorPower(-0.8);

            return false;
        }
    }

    public class maintainIntake implements Action {
        public maintainIntake(HardwareMap hMap) {}
        public boolean run(@NonNull TelemetryPacket telemtryPacket)  {

            intake.setMotorPower(-0.4);

            return false;
        }
    }

    public class stopIntake implements Action {
        public stopIntake(HardwareMap hMap) {}
        public boolean run(@NonNull TelemetryPacket telemtryPacket)  {

            intake.setMotorPower(0);

            return false;
        }
    }

    public class runFlywheels implements Action {
        public runFlywheels(HardwareMap hMap) {
        }
        public boolean run(@NonNull TelemetryPacket telemetryPacket) {

            outtake.setFlywheelVelocity(2350);

            for (int t = 0; t < 6 && outtake.getCurrentWheelVelocity("right") < 2200; t++) {
                sleep(500);
            }

            return false;
        }
    }

    public class stopFlywheels implements Action {
        public stopFlywheels(HardwareMap hMap) {
        }
        public boolean run(@NonNull TelemetryPacket telemetryPacket) {

            outtake.setFlywheelVelocity(0);

            return false;
        }
    }

    public class scoreBallSequence implements Action {
        public scoreBallSequence(HardwareMap hMap) {}
        public boolean run(@NonNull TelemetryPacket telemetryPacket) {

            outtakeHinge.outtakeHingeFire();
            intakeHinge.intakeHingeStandby();

            sleep(500);

            outtakeHinge.outtakeHingeRelax();

            sleep(500);

            intakeHinge.intakeHingeLift();

            sleep(250);

            intakeHinge.intakeHingeLift();

            sleep(250);

            intakeHinge.intakeHingeLift();

            sleep(500);

            return false;
        }
    }

}  // end class