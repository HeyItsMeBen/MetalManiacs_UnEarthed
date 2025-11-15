package org.firstinspires.ftc.teamcode.PID_Tuners.FlywheelTuning;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp(name = "Flywheel PIDF Sync Tuner", group = "Tuning")
public class PIDFSyncTuner extends LinearOpMode {

    public static double targetVelocity = 1000.0;

    private PIDFSync flywheel;
    private FtcDashboard dashboard;

    @Override
    public void runOpMode() {
        flywheel = new PIDFSync(hardwareMap);
        dashboard = FtcDashboard.getInstance();

        waitForStart();

        while (opModeIsActive()) {
            // Update target velocity
            flywheel.setTargetVelocity(targetVelocity);

            // Run PIDF sync loop
            flywheel.update();

            // Get current velocities
            double leftVel = flywheel.getLeftVelocity();
            double rightVel = flywheel.getRightVelocity();
            double delta = leftVel - rightVel;

            // Send to dashboard
            TelemetryPacket packet = new TelemetryPacket();
            packet.put("targetVelocity", targetVelocity);
            packet.put("leftVelocity", leftVel);
            packet.put("rightVelocity", rightVel);
            packet.put("delta L-R", delta);
            packet.put("Kf * targetVelocity", PIDFSync.Kf * targetVelocity);
            packet.put("K_sync", PIDFSync.K_sync);
            dashboard.sendTelemetryPacket(packet);

            // Driver Station telemetry
            telemetry.addData("Target Velocity", targetVelocity);
            telemetry.addData("Left Velocity", leftVel);
            telemetry.addData("Right Velocity", rightVel);
            telemetry.addData("L-R Delta", delta);
            telemetry.update();
        }
    }
}
