package org.firstinspires.ftc.teamcode.AutoCode;

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

import org.firstinspires.ftc.teamcode.Systems.Arm;
import org.firstinspires.ftc.teamcode.Systems.Intake;
import org.firstinspires.ftc.teamcode.Systems.Outtake;
import org.firstinspires.ftc.teamcode.Systems.Hinge;

@Autonomous(name = "Auto Pathing Direct Red", group = "Concept")
//@Disabled
public class AutoPathingDirectRed extends LinearOpMode {

    Intake intake;
    Arm arm;
    Outtake outtake;
    Hinge hinge;

    @Override
    public void runOpMode() {

        Pose2d beginPose = new Pose2d(12, -60, Math.PI / 2);
        MecanumDrive drive = new MecanumDrive(hardwareMap, beginPose);

        intake = new Intake(hardwareMap);
        arm = new Arm(hardwareMap);
        outtake = new Outtake(hardwareMap);
        hinge = new Hinge(hardwareMap);
        //to do: add another hinge servo transfer servo

        waitForStart();

        Actions.runBlocking(
                drive.actionBuilder(beginPose)

                        .strafeTo(new Vector2d(20, -40))

                        .splineToLinearHeading(new Pose2d(35, 55, Math.toRadians(225)), Math.toRadians(45))

                        .waitSeconds(1)

                        .stopAndAdd(new scoreBallSequence(hardwareMap))

                        .splineTo(new Vector2d(20, -40), Math.toRadians(270))

                        .build());
    }

    public class scoreBallSequence implements Action {
        public scoreBallSequence(HardwareMap hMap) {}
        public boolean run(@NonNull TelemetryPacket telemetryPacket) {

            arm.moveArmTo(600, 2);

            sleep(1000);

            outtake.setFlywheelVelocity(3000, 3);  //sets flywheel Velocity to 2900 rpm, and gives it 1 second to speed up.

            sleep(1000);
            //fires the ball, and brings the hinge back to waiting position

            hinge.liftHingeAndWait(hinge.firePosition, 1);  //pushes the ball into the flywheel. Idk what value it's supposed to be.

            hinge.liftHinge(hinge.holdPosition);  //puts the hinge back, so it can hold another ball. Idk what value it's supposed to be.

            outtake.setFlywheelVelocity(0, 3);  //sets flywheel Velocity to 2900 rpm, and gives it 1 second to speed up.

            sleep(500);

            arm.moveArmTo(300, 2); //change later

            sleep(250);

            arm.moveArmTo(100, 1); //change later

            sleep(250);

            arm.moveArmTo(0, 0.5); //change later

            sleep(250);

            return false;
        }
    }

}  // end class