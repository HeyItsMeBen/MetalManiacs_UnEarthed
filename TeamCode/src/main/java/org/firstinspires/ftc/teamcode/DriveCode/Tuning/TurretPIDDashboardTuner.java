package org.firstinspires.ftc.teamcode.DriveCode.Tuning;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.arcrobotics.ftclib.controller.PIDController;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;

@Config
@TeleOp(name = "Turret PID Dashboard Tuner", group = "Tuning")
public class TurretPIDDashboardTuner extends LinearOpMode {

    // PIDF coefficients editable on Dashboard
    //public static double p=0.01, i=0, d=0.0005, f=0;  //fast values
    public static double p=0.002, i=0, d=0.0001, f=0;   //safe values
    public static double targetPosition = 3000;
    double ticksPerTurretRevolution=6320;

    // Motor
    private DcMotorEx turretMotor;
    private PIDController controller;

    @Override
    public void runOpMode() throws InterruptedException {

        // Hardware init
        turretMotor = hardwareMap.get(DcMotorEx.class, "turret");
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
        controller = new PIDController(p, i, d);
        turretMotor.setDirection(DcMotorSimple.Direction.FORWARD);
        turretMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        turretMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        waitForStart();
        while (opModeIsActive()) {
            runTowardTargetDistance(targetPosition);
            telemetry.addData("Target Target", targetPosition);
            telemetry.addData("Turret Position", turretMotor.getCurrentPosition());
            telemetry.update();
        }
    }
    public void runTowardTargetDistance(double ticks) {
        controller.setPID(p, i, d);
        int armPos = turretMotor.getCurrentPosition();
        double power = controller.calculate(armPos, ticks);

        turretMotor.setPower(power);
    }
}
