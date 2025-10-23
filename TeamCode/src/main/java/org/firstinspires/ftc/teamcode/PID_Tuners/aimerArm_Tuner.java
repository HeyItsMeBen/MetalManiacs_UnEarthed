package org.firstinspires.ftc.teamcode.PID_Tuners;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.arcrobotics.ftclib.controller.PIDController;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

@Disabled

@Config
@Autonomous(name = "aimerArm_Tuner", group = "Autonomous")
public class aimerArm_Tuner extends LinearOpMode {
    private PIDController controller;
    public static double p = 0.001, i = 0, d = 0;
    public static double f = 0;
    public static double inputTarget=0;
    private final double ticks_in_degree = 1120 / 360;
    private DcMotorEx arm_motor;

    public void runOpMode() {
        waitForStart();
        controller = new PIDController(p, i, d);

        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());

        arm_motor = hardwareMap.get(DcMotorEx.class, "arm");   //real name?
        arm_motor.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
        arm_motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        arm_motor.setDirection(DcMotor.Direction.REVERSE);
        arm_motor.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);

        while (opModeIsActive()){
            setArmTarget(inputTarget);
            telemetry.addData("Target: ", inputTarget);
            telemetry.addData("Position: ", arm_motor.getCurrentPosition());
            telemetry.update();
        }
    }


    public void setArmTarget(double givenTarget) {
        double target=givenTarget*1;
        controller.setPID(p, i, d);
        int armPos = arm_motor.getCurrentPosition();
        double pid = controller.calculate(armPos, target);
        double ff = Math.cos(Math.toRadians(target / ticks_in_degree)) * f;

        double power = pid + ff;

        arm_motor.setPower(power);
    }
    public void stopMotor(){
        arm_motor.setPower(0);
    }
}