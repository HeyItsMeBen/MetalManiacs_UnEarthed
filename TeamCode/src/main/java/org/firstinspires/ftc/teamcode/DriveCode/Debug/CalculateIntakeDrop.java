package org.firstinspires.ftc.teamcode.DriveCode.Debug;

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

@Config
@TeleOp(name = "Calculate Intake Drop", group = "Debug")
public class CalculateIntakeDrop extends LinearOpMode {

    // Motor
    private DcMotorEx intake;
    private FtcDashboard dashboard;
    public DcMotor transferWheels;

    @Override
    public void runOpMode() throws InterruptedException {

        // Hardware init
        intake = hardwareMap.get(DcMotorEx.class, "intake");
        intake.setDirection(DcMotorEx.Direction.FORWARD);

        transferWheels = hardwareMap.get(DcMotor.class, "transferDrum");
        transferWheels.setDirection(DcMotorSimple.Direction.FORWARD);

        dashboard = FtcDashboard.getInstance();

        telemetry.addLine("Init complete");
        telemetry.update();

        ElapsedTime timer = new ElapsedTime();
        timer.reset();

        waitForStart();

        while (opModeIsActive()) {

            intake.setPower(0.8);
            transferWheels.setPower(0.5);

            // --- Dashboard telemetry ---
            TelemetryPacket packet = new TelemetryPacket();
            packet.put("Current Velocity", intake.getVelocity());
            packet.put("Current Power", intake.getPower());
            dashboard.sendTelemetryPacket(packet);

            // --- Driver Station telemetry ---
            telemetry.addData("Current Velocity", intake.getVelocity());
            telemetry.addData("Current Power", intake.getPower());
            telemetry.update();
        }
    }
}
