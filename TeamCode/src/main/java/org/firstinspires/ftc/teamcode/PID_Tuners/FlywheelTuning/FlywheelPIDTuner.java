package org.firstinspires.ftc.teamcode.PID_Tuners.FlywheelTuning;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

// The "interpreter," in a sense, of the flywheel PID. It will graph the target values and allow you
// to tune your PID values to match

@Config
@TeleOp(name = "Flywheel Velocity PID Tuner", group = "Tuning")
public class FlywheelPIDTuner extends LinearOpMode {

    // Dashboard-tunable target velocity (ticks/second)
    public static double targetVelocity = 2000.0;

    private FlywheelPIDClass flywheel;
    private FtcDashboard dashboard;

    @Override
    public void runOpMode() throws InterruptedException {
        flywheel = new FlywheelPIDClass(hardwareMap);
        dashboard = FtcDashboard.getInstance();

        waitForStart();

        while (opModeIsActive()) {
            // Update the target velocity used by the FlywheelPIDClass
            FlywheelPIDClass.targetVelocity = targetVelocity;

            // Run the PID update loop
            flywheel.setFlywheelVelocity();

            // Read current velocities
            double leftVel = flywheel.leftFlywheel.getVelocity();
            double rightVel = flywheel.rightFlywheel.getVelocity();

            // Send to FTC Dashboard for live plotting
            TelemetryPacket packet = new TelemetryPacket();
            packet.put("targetVelocity", targetVelocity);
            packet.put("leftVelocity", leftVel);
            packet.put("rightVelocity", rightVel);
            dashboard.sendTelemetryPacket(packet);

            // Send to Driver Station telemetry
            telemetry.addData("Target Velocity", targetVelocity);
            telemetry.addData("Left Velocity", leftVel);
            telemetry.addData("Right Velocity", rightVel);
            telemetry.update();
        }
    }
}
