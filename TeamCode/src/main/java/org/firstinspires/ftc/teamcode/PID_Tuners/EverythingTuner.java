package org.firstinspires.ftc.teamcode.PID_Tuners;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.Systems.Arm;
import org.firstinspires.ftc.teamcode.Systems.Hinge;
import org.firstinspires.ftc.teamcode.Systems.Outtake;

@Config
@Autonomous(name = "Everything_Tuner", group = "Autonomous")
public class EverythingTuner extends LinearOpMode {
    public static double armShootPosition=700;
    double velocityPeak=0;
    Outtake outtake;
    Hinge hinge;
    Arm arm;
    public void runOpMode() {
        outtake = new Outtake(hardwareMap);
        hinge = new Hinge(hardwareMap);
        arm = new Arm(hardwareMap);
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
        waitForStart();
        while (opModeIsActive()){
            arm.moveArmTo(armShootPosition, 2);
            outtake.setFlywheelVelocity(3000, 1, armShootPosition);
            hinge.liftHingeAndWait(hinge.firePosition, 1, armShootPosition);
            velocityPeak=outtake.getCurrentWheelRPM();
            outtake.setFlywheelVelocity(0, 0);
            hinge.liftHingeAndWait(hinge.holdPosition, 0);
            telemetry.addData("VELOCITY: ", velocityPeak);
            telemetry.addData("Arm Pos: ", arm.getArmPosition());
            telemetry.addData("Arm Target: ", armShootPosition);
            telemetry.update();
            arm.moveArmTo(100, 2);
            sleep(6000);
        }
    }
}
/*
outtake.setFlywheelVelocity(3000, 1);
            hinge.liftHingeAndWait(hinge.firePosition, 1);
            velocityPeak=outtake.getCurrentWheelRPM();
            outtake.setFlywheelVelocity(0, 0);
            hinge.liftHingeAndWait(hinge.holdPosition, 0);
 */