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

import org.firstinspires.ftc.teamcode.Controllers.IntakeController;

@Config
@TeleOp(name = "Fire Artifact, Max Power", group = "Debug")
public class FireMaxPower extends LinearOpMode {
    // Motor
    private DcMotorEx flywheel;

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

        telemetry.addLine("Init complete");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {

            flywheel.setPower(1);

            intake.setPower(0.3);
            transferWheels.setPower(0.3);

            // --- Driver Station telemetry ---
            telemetry.addData("Current Power", flywheel.getPower());

            telemetry.addData("Current Velocity", flywheel.getVelocity());

            telemetry.update();
        }
    }
}
