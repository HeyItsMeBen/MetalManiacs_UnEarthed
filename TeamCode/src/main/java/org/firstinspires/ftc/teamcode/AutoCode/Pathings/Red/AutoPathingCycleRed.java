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

@Autonomous(name = "Competition Pathing: Auto Cycle Red", group = "Auto Pathing")
//@Disabled
public class AutoPathingCycleRed extends LinearOpMode {

    Intake intake;
    Flywheels outtake;
    Transfer intakeHinge;
    Transfer outtakeHinge;

    double firing_position_x = 15;
    double firing_position_y = 15;

    @Override
    public void runOpMode() {

        Pose2d beginPose = new Pose2d(15, -60, Math.toRadians(90));
        MecanumDrive drive = new MecanumDrive(hardwareMap, beginPose);

        intake = new Intake(hardwareMap);
        outtake = new Flywheels(hardwareMap);
        intakeHinge = new Transfer(hardwareMap);
        outtakeHinge = new Transfer(hardwareMap);

        // Wait for the DS start button to be touched.
        telemetry.addData("DS preview on/off", "3 dots, Camera Stream");
        telemetry.addData(">", "Touch START to start OpMode");
        telemetry.update();
        waitForStart();

        if (opModeIsActive()) {


                    waitForStart();
                    Actions.runBlocking(
                            drive.actionBuilder(beginPose)

                                    .stopAndAdd(new runIntake(hardwareMap))

                                    .splineTo(new Vector2d(46, -35), 0)

                                    .waitSeconds(0.5f)
                                    .stopAndAdd(new stopIntake(hardwareMap))
                                    .setTangent(Math.toRadians(180))
                                    .splineTo(new Vector2d(20, 20), Math.toRadians(45))
                                    .strafeTo(new Vector2d(firing_position_x, firing_position_y))

                                    .stopAndAdd(new runFlywheels(hardwareMap))

                                    .stopAndAdd(new scoreBallSequence(hardwareMap))
                                    .stopAndAdd(new scoreBallSequence(hardwareMap))
                                    .stopAndAdd(new scoreBallSequence(hardwareMap))

                                    .stopAndAdd(new stopFlywheels(hardwareMap))

                                    .strafeTo(new Vector2d(15, -40))

                                    .build());
                }

    }

    public class runIntake implements Action {
        public runIntake(HardwareMap hMap) {}
        public boolean run(@NonNull TelemetryPacket telemtryPacket)  {

            intakeHinge.intakeHingeStandby();

            sleep(250);

            intake.setMotorPower(0.5);

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

            sleep(500);

            return false;
        }
    }

}  // end class