package org.firstinspires.ftc.teamcode.AutoCode.Testing;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Pose2dDual;
import com.acmerobotics.roadrunner.ProfileParams;
import com.acmerobotics.roadrunner.SequentialAction;
import com.acmerobotics.roadrunner.TrajectoryBuilderParams;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.AutoCode.Pathings.PathingTrajectories;
import org.firstinspires.ftc.teamcode.AutoCode.Roadrunner.MecanumDrive;

@Config
@Autonomous(name = "Trajectory Path Tester", group = "Autonomous")
public class TrajectoryPathTest extends LinearOpMode {

    @Override
    public void runOpMode() {
        Pose2d startPose = new Pose2d(15, -60, Math.toRadians(270)); // x, y, heading in radians
        MecanumDrive drive = new MecanumDrive(hardwareMap, startPose);

        Action directPathing = PathingTrajectories.aimingPosition(drive, startPose);

        //Action nextPathing = PathingTrajectories.forwardMovement(drive, drive.localizer.getPose());

        waitForStart();

        Actions.runBlocking(
                new SequentialAction(directPathing));
        
        Action nextPathing = PathingTrajectories.forwardMovement(drive, drive.localizer.getPose());

        Actions.runBlocking(
                new SequentialAction(nextPathing));

    }
}
