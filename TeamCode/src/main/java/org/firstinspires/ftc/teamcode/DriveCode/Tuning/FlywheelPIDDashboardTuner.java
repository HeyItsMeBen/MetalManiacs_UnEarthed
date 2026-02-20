package org.firstinspires.ftc.teamcode.DriveCode.Tuning;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Controllers.IntakeController;

@Config
@TeleOp(name = "Flywheel PID Dashboard Tuner", group = "Tuning")
public class FlywheelPIDDashboardTuner extends LinearOpMode {

    // PIDF coefficients editable on Dashboard
    public static double P = 100.0;
    public static double I = 0.0;
    public static double D = 0.0;
    public static double F = 14.12;

    // Target velocity (RPM), editable live
    public static double targetVelocity = 3000;

    ElapsedTime flywheelTimer;
    double rampSeconds = 5;

    // Maximum velocity change per loop for smooth ramping
    public static double MINIMUM_SPEED = 50.0; // RPM per loop

    // Motor
    private DcMotorEx flywheel;
    private FtcDashboard dashboard;

    public DcMotor intake;
    public DcMotor transferWheels;

    @Override
    public void runOpMode() throws InterruptedException {

        // Hardware init
        flywheel = hardwareMap.get(DcMotorEx.class, "flywheel");
        flywheel.setDirection(DcMotorEx.Direction.REVERSE);
        flywheel.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
        flywheel.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);

        intake = hardwareMap.get(DcMotorEx.class, "intake");
        intake.setDirection(DcMotorEx.Direction.FORWARD);
        transferWheels = hardwareMap.get(DcMotorEx.class, "kickWheel");
        transferWheels.setDirection(DcMotorSimple.Direction.REVERSE);

        dashboard = FtcDashboard.getInstance();

        telemetry.addLine("Init complete");
        telemetry.update();

        ElapsedTime timer = new ElapsedTime();
        timer.reset();

        waitForStart();

        double rampedTarget = flywheel.getVelocity(); // start at current velocity

        while (opModeIsActive()) {

            // Update PIDF live from Dashboard
            PIDFCoefficients updatedCoeffs = new PIDFCoefficients(P, I, D, F);
            flywheel.setPIDFCoefficients(DcMotorEx.RunMode.RUN_USING_ENCODER, updatedCoeffs);

            // --- Ramp target velocity smoothly ---
            double rampTime = rampSeconds;        // 5 seconds
            double maxVelocity = targetVelocity;
            double elapsed = timer.seconds();
            double progress = Math.min(elapsed / rampTime, 1.0);
            double newVelocity = maxVelocity * progress;
            flywheel.setVelocity(newVelocity);

            //intake.setPower(0.3);
            //transferWheels.setPower(0.3);

            // --- Dashboard telemetry ---
            TelemetryPacket packet = new TelemetryPacket();
            packet.put("Target Velocity", targetVelocity);
            packet.put("Ramped Target", rampedTarget);
            packet.put("Current Velocity", flywheel.getVelocity());
            packet.put("Error", targetVelocity - flywheel.getVelocity());
            dashboard.sendTelemetryPacket(packet);

            // --- Driver Station telemetry ---
            telemetry.addData("Target Velocity", targetVelocity);
            telemetry.addData("Ramped Target", rampedTarget);
            telemetry.addData("Current Velocity", flywheel.getVelocity());
            telemetry.addData("Error", targetVelocity - flywheel.getVelocity());
            telemetry.addData("P", P);
            telemetry.addData("I", I);
            telemetry.addData("D", D);
            telemetry.addData("F", F);
            telemetry.update();
        }
    }
}
