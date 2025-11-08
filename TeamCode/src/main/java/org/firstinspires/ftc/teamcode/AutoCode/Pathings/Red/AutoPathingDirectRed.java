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
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Roadrunner.MecanumDrive;

import org.firstinspires.ftc.teamcode.Systems.Arm;
import org.firstinspires.ftc.teamcode.Systems.Intake;
import org.firstinspires.ftc.teamcode.Systems.Outtake;
import org.firstinspires.ftc.teamcode.Systems.Hinge;
import org.firstinspires.ftc.teamcode.Systems.Transfer;

@Autonomous(name = "Auto Pathing Direct Red", group = "Concept")
//@Disabled
public class AutoPathingDirectRed extends LinearOpMode {

    Intake intake;
    Outtake outtake;
    Transfer intakeHinge;
    Transfer outtakeHinge;

    @Override
    public void runOpMode() {

        Pose2d beginPose = new Pose2d(12, -60, 3*Math.PI / 2);
        MecanumDrive drive = new MecanumDrive(hardwareMap, beginPose);

        intake = new Intake(hardwareMap);
        outtake = new Outtake(hardwareMap);
        intakeHinge = new Transfer(hardwareMap);
        outtakeHinge = new Transfer(hardwareMap);
        //to do: add another hinge servo transfer servo

        waitForStart();

                Actions.runBlocking(
                drive.actionBuilder(beginPose)

                        .setReversed(true)

                        .splineTo(new Vector2d(37, 37), Math.toRadians(45))

                        .strafeTo(new Vector2d(20,20))

                        .waitSeconds(1)

                        .stopAndAdd(new AutoPathingDirectRed.scoreBallSequence(hardwareMap))

                        .setReversed(false)

                        .strafeTo(new Vector2d(30, -30))

                        .build());
    }


    public class scoreBallSequence implements Action {
        public scoreBallSequence(HardwareMap hMap) {}
        public boolean run(@NonNull TelemetryPacket telemetryPacket) {

            outtakeHinge.outtakeHingeRelax();

            outtake.setFlywheelVelocity(2350);

            while (outtake.getCurrentWheelVelocity("left") < 2300) {
                sleep(500);
            }

            outtakeHinge.outtakeHingeFire();
            intakeHinge.intakeHingeStandby();

            sleep(500);

            outtakeHinge.outtakeHingeRelax();

            sleep(500);

            intakeHinge.intakeHingeLift();

            sleep(500);

            outtakeHinge.outtakeHingeFire();
            intakeHinge.intakeHingeStandby();

            sleep(500);

            outtakeHinge.outtakeHingeRelax();

            sleep(500);

            intakeHinge.intakeHingeLift();

            sleep(500);

            outtakeHinge.outtakeHingeFire();
            intakeHinge.intakeHingeStandby();

            sleep(500);

            outtake.setFlywheelVelocity(0);

            outtakeHinge.outtakeHingeRelax();

            return false;
        }
    }

}  // end class