package org.firstinspires.ftc.teamcode.AutoCode;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.Roadrunner.MecanumDrive;

import org.firstinspires.ftc.teamcode.Systems.Arm;
import org.firstinspires.ftc.teamcode.Systems.Hinge;
import org.firstinspires.ftc.teamcode.Systems.Intake;
import org.firstinspires.ftc.teamcode.Systems.Outtake;

@Autonomous(name = "Auto Pathing Direct Blue", group = "Concept")
//@Disabled
public class AutoPathingDirectBlue extends LinearOpMode {

    Intake intake;
    Arm arm;
    Outtake flywheel;
    Hinge hinge;

    @Override
    public void runOpMode() {

        Pose2d beginPose = new Pose2d(-12, -60, Math.PI / 2);
        MecanumDrive drive = new MecanumDrive(hardwareMap, beginPose);

        intake = new Intake(hardwareMap);
        arm = new Arm(hardwareMap);
        flywheel = new Outtake(hardwareMap);
        hinge = new Hinge(hardwareMap);

        waitForStart();

        Actions.runBlocking(
                drive.actionBuilder(beginPose)

                        .strafeTo(new Vector2d(-20, -40))
                        .splineToLinearHeading(new Pose2d(-35, 55, Math.toRadians(315)), Math.toRadians(135))

                        .stopAndAdd(new scoreBall(hardwareMap))

                        .splineTo(new Vector2d(-20, -40), Math.toRadians(270))

                        .build());
    }

    public class runIntake implements Action {
        public runIntake(HardwareMap hMap) {}
        public boolean run(@NonNull TelemetryPacket telemtryPacket)  {
            intake.setMotorPower(0.5);
            sleep(3);
            intake.setMotorPower(0);
            return false;
        }
    }

    /*public class raiseArm implements Action {
        public raiseArm(HardwareMap hMap) {}
        public boolean run(@NonNull TelemetryPacket telemtryPacket)  {
            Aim.setArmTarget(1);
            Aim.stopMotor();
            return false;
        }
    }

    public class launchBall implements Action {
        public launchBall(HardwareMap hMap) {}
        public boolean run(@NonNull TelemetryPacket telemetryPacket) {
            Flywheel.fire(1);
            return false;
        }
    }*/

    public class liftHinge implements Action {
        public liftHinge(HardwareMap hMap) {}
        public boolean run(@NonNull TelemetryPacket telemetryPacket) {
            hinge.liftHinge(10);
            return false;
        }
    }
    public class lowerHinge implements Action {
        public lowerHinge(HardwareMap hMap) {}
        public boolean run(@NonNull TelemetryPacket telemetryPacket) {
            hinge.liftHinge(0);
            return false;
        }
    }
    public class scoreBall implements Action {
        public scoreBall(HardwareMap hMap) {}
        public boolean run(@NonNull TelemetryPacket telemetryPacket) {

            flywheel.setFlywheelVelocity(2900, 1);  //sets flywheel Velocity to 2900 rpm, and gives it 1 second to speed up.

            //fires the ball, and brings the hinge back to waiting position
            hinge.liftHinge(0.6f);  //pushes the ball into the flywheel. Idk what value it's supposed to be.
            sleep(1000);
            hinge.liftHinge(0.3f);  //puts the hinge back, so it can hold another ball. Idk what value it's supposed to be.

            return false;
        }
    }

}  // end class