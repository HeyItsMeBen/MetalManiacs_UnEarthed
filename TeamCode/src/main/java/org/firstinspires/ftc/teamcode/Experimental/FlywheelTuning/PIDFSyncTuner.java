package org.firstinspires.ftc.teamcode.Experimental.FlywheelTuning;

import static java.lang.Thread.sleep;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Hardware.Transfer;

@Disabled
@TeleOp(name = "Flywheel PIDF Sync Tuner", group = "Tuning")
public class PIDFSyncTuner extends LinearOpMode {

    public static double targetVelocity = 1000.0;

    private PIDFSync flywheel;
    private FtcDashboard dashboard;

    private Transfer hinge;

    @Override
    public void runOpMode() {
        flywheel = new PIDFSync(hardwareMap);
        dashboard = FtcDashboard.getInstance();

        hinge = new Transfer(hardwareMap);

        waitForStart();

        while (opModeIsActive()) {
            // Update target velocity


            // Run PIDF sync loop
            flywheel.update();

            // Get current velocities
            double leftVel = flywheel.getLeftVelocity();
            double rightVel = flywheel.getRightVelocity();
            double delta = leftVel - rightVel;

            // Send to dashboard
            TelemetryPacket packet = new TelemetryPacket();
            packet.put("targetVelocity", flywheel.getTargetVelocity()); //changed to access the targetVelocity in sync object for tuning
            packet.put("leftVelocity", leftVel);
            packet.put("rightVelocity", rightVel);
            packet.put("delta L-R", delta);
            packet.put("Kf * targetVelocity", PIDFSync.Kf * flywheel.getTargetVelocity());
            packet.put("K_sync", PIDFSync.K_sync);
            dashboard.sendTelemetryPacket(packet);

            if (gamepad2.right_bumper) {
                hinge.outtakeHingeFire();

            }

            if (gamepad2.left_bumper) {
                hinge.outtakeHingeRelax();

            }


            // Driver Station telemetry
            telemetry.addData("Target Velocity", flywheel.getTargetVelocity());
            telemetry.addData("Left Velocity", leftVel);
            telemetry.addData("Right Velocity", rightVel);
            telemetry.addData("L-R Delta", delta);
            telemetry.update();
        }
    }
}
