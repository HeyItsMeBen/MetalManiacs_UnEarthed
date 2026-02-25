package org.firstinspires.ftc.teamcode.DriveCode.Tuning;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.arcrobotics.ftclib.controller.PIDController;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.PIDCoefficients;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Controllers.IntakeController;

@Config
@TeleOp(name = "Turret PID Tuner", group = "A - Tuning")
public class TurretPIDTuning extends LinearOpMode {

    // PIDF coefficients editable on Dashboard
    public static double P = 0;
    public static double I = 0.0;
    public static double D = 0.0;

    // Target velocity (RPM), editable live
    public static double targetTicks = 200;

    // Motor
    private FtcDashboard dashboard;

    public DcMotorEx turret;
    private PIDController controller;

    @Override
    public void runOpMode() throws InterruptedException {

        controller = new PIDController(P, I, D);

        // Hardware init
        turret = hardwareMap.get(DcMotorEx.class, "turret");
        turret.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        turret.setDirection(DcMotor.Direction.REVERSE);

        turret.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        turret.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        dashboard = FtcDashboard.getInstance();

        telemetry.addLine("Init complete");
        telemetry.update();

        waitForStart();

        double setPosition = 200; // outside the loop

        while (opModeIsActive()) {
            controller.setPID(P, I, D);

            int currentPos = turret.getCurrentPosition();
            double power = controller.calculate(currentPos, setPosition);

            // clamp power
            power = Math.max(Math.min(power, 0.5), -0.5);
            turret.setPower(power);

            // optional: toggle position every second
            setPosition = (currentPos > 0) ? -200 : 200;

            // telemetry
            TelemetryPacket packet = new TelemetryPacket();
            packet.put("Target Pos", setPosition);
            packet.put("Current Pos", currentPos);
            packet.put("Error", setPosition - currentPos);
            dashboard.sendTelemetryPacket(packet);

            telemetry.addData("Target Pos", setPosition);
            telemetry.addData("Current Pos", currentPos);
            telemetry.addData("Error", setPosition - currentPos);
            telemetry.addData("Power", power);
            telemetry.update();
        }
    }
}

