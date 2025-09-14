package org.firstinspires.ftc.teamcode.AutoCode;

import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.AutoCode.Roadrunner.MecanumDrive;

@Autonomous(name = "Auto Pathing", group = "Linear OpMode")
public final class Autonomous_Pathing extends LinearOpMode {
    public void runOpMode() throws InterruptedException {
        Pose2d beginPose = new Pose2d(0, 0, 0);
        MecanumDrive drive = new MecanumDrive(hardwareMap, beginPose);

        waitForStart();
        Actions.runBlocking(
                drive.actionBuilder(beginPose)
                        .splineTo(new Vector2d(0, 30), Math.PI)
                        .splineTo(new Vector2d(0, 60), 0)
                        .build());
    }
}
