package org.firstinspires.ftc.teamcode.AutoCode.Pathings.Blue;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.TrajectoryBuilder;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.acmerobotics.roadrunner.Action;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.Roadrunner.MecanumDrive;

import org.firstinspires.ftc.teamcode.Systems.Intake;
import org.firstinspires.ftc.teamcode.Systems.Flywheels;
import org.firstinspires.ftc.teamcode.Systems.Transfer;

@Autonomous(name = "Competition Pathing: Auto Direct Blue", group = "Auto Pathing")
//@Disabled
public class AutoPathingDirectBlue extends LinearOpMode {

    Intake intake;
    Flywheels outtake;
    Transfer intakeHinge;
    Transfer outtakeHinge;

    double firing_position_x = -15;
    double firing_position_y = 15;

    @Override
    public void runOpMode() {

        Pose2d beginPose = new Pose2d(-15, -60, 3*Math.PI / 2);
        MecanumDrive drive = new MecanumDrive(hardwareMap, beginPose);

        intake = new Intake(hardwareMap);
        outtake = new Flywheels(hardwareMap);
        intakeHinge = new Transfer(hardwareMap);
        outtakeHinge = new Transfer(hardwareMap);

        waitForStart();

        Actions.runBlocking(
                drive.actionBuilder(beginPose)

                        .setReversed(true)

                        .splineTo(new Vector2d(-37, 37), Math.toRadians(135))


                        .strafeTo(new Vector2d(firing_position_x, firing_position_y))

                        .waitSeconds(1)

                        .stopAndAdd(new runFlywheels(hardwareMap))

                        .stopAndAdd(new scoreBallSequence(hardwareMap))
                        .stopAndAdd(new scoreBallSequence(hardwareMap))
                        .stopAndAdd(new scoreBallSequence(hardwareMap))

                        .stopAndAdd(new stopFlywheels(hardwareMap))

                        .setReversed(false)

                        .strafeTo(new Vector2d(-15, -40))

                        .build());
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

            while (outtake.getCurrentWheelVelocity("left") < 2300) {
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