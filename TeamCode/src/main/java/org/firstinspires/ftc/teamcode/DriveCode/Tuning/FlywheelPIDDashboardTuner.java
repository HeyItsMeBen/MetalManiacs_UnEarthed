package org.firstinspires.ftc.teamcode.DriveCode.Tuning;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;

@Config
@TeleOp(name = "Flywheel PID Dashboard Tuner", group = "Tuning")
public class FlywheelPIDDashboardTuner extends LinearOpMode {

    // PIDF coefficients editable on Dashboard
    public static double P = 100.0;
    public static double I = 0.0;
    public static double D = 0.0;
    public static double F = 14.12;

    // Target velocity (RPM), editable live
    public static double targetVelocity = 2000;

    // Maximum velocity change per loop for smooth ramping
    public static double maxAccelPerLoop = 50.0; // RPM per loop

    // Motor
    private DcMotorEx flywheel;
    private FtcDashboard dashboard;

    @Override
    public void runOpMode() throws InterruptedException {

        // Hardware init
        flywheel = hardwareMap.get(DcMotorEx.class, "flywheel");
        flywheel.setDirection(DcMotorEx.Direction.REVERSE);
        flywheel.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
        flywheel.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);

        // Initialize PIDF on the motor
        PIDFCoefficients pidfCoefficients = new PIDFCoefficients(P, I, D, F);
        flywheel.setPIDFCoefficients(DcMotorEx.RunMode.RUN_USING_ENCODER, pidfCoefficients);

        dashboard = FtcDashboard.getInstance();

        telemetry.addLine("Init complete");
        telemetry.update();

        waitForStart();

        double rampedTarget = flywheel.getVelocity(); // start at current velocity

        while (opModeIsActive()) {

            // Update PIDF live from Dashboard
            PIDFCoefficients updatedCoeffs = new PIDFCoefficients(P, I, D, F);
            flywheel.setPIDFCoefficients(DcMotorEx.RunMode.RUN_USING_ENCODER, updatedCoeffs);

            // --- Ramp target velocity smoothly ---
            double currentVel = flywheel.getVelocity();
            if (currentVel < targetVelocity) {
                rampedTarget = Math.min(currentVel + maxAccelPerLoop, targetVelocity);
            } else if (currentVel > targetVelocity) {
                rampedTarget = Math.max(currentVel - maxAccelPerLoop, targetVelocity);
            }

            // Set motor velocity
            flywheel.setVelocity(rampedTarget);

            // --- Dashboard telemetry ---
            TelemetryPacket packet = new TelemetryPacket();
            packet.put("Target Velocity", targetVelocity);
            packet.put("Ramped Target", rampedTarget);
            packet.put("Current Velocity", currentVel);
            packet.put("Error", targetVelocity - currentVel);
            dashboard.sendTelemetryPacket(packet);

            // --- Driver Station telemetry ---
            telemetry.addData("Target Velocity", targetVelocity);
            telemetry.addData("Ramped Target", rampedTarget);
            telemetry.addData("Current Velocity", currentVel);
            telemetry.addData("Error", targetVelocity - currentVel);
            telemetry.addData("P", P);
            telemetry.addData("I", I);
            telemetry.addData("D", D);
            telemetry.addData("F", F);
            telemetry.update();
        }
    }
}
