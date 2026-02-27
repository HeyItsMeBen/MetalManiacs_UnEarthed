package org.firstinspires.ftc.teamcode.DriveCode.TestFiles;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.arcrobotics.ftclib.gamepad.GamepadKeys;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Hardware.OuttakeHood;

@Config
@TeleOp (name="HoodTest", group="Debug")
public class HoodTest extends LinearOpMode {
    OuttakeHood hood;
    public static double servoAngle=0;

    @Override
    public void runOpMode() {
        hood = new OuttakeHood(hardwareMap);
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
        waitForStart();
        while (opModeIsActive()) {
            //hood.setAngle(Math.toRadians(servoAngle));
            hood.setServoPosition(servoAngle);
            telemetry.addData("hoodAngle", servoAngle);
            telemetry.update();
        }
    }
}
