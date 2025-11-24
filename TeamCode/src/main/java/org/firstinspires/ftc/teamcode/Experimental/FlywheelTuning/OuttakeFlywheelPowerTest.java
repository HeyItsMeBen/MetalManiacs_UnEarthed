package org.firstinspires.ftc.teamcode.Experimental.FlywheelTuning;

import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.arcrobotics.ftclib.gamepad.GamepadKeys;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Hardware.Flywheels;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;

// The goal of this file is, strangely enough, determine the feedforward K-value
// Calculate the K_v slope, and set K_f = 1/K_v
@Disabled
@TeleOp (name="Outtake Flywheel Power Test", group="test")
public class OuttakeFlywheelPowerTest extends LinearOpMode {

    public GamepadEx gamepad;

    Flywheels outtake;

    @Override
    public void runOpMode() {

        gamepad = new GamepadEx(gamepad1);
        outtake = new Flywheels(hardwareMap);

        double power = 0.3;

        FtcDashboard dashboard = FtcDashboard.getInstance();

        waitForStart();

        //executing
        while (opModeIsActive()) {
            if (gamepad.getButton(GamepadKeys.Button.A)) {
                outtake.setFlywheelPower(power);
            }
            else if (gamepad.getButton(GamepadKeys.Button.B)) {
                outtake.setFlywheelPower(0);
            }

            //Send data to FTC dashbaord. The dashboard will graph it in real-time
            TelemetryPacket packet = new TelemetryPacket();
            packet.put("Left Motor Velocity", outtake.getCurrentWheelVelocity("left"));
            packet.put("Right Motor Velocity", outtake.getCurrentWheelVelocity("right"));
            dashboard.sendTelemetryPacket(packet);

            //Sends data to the driver station as quantitative values
            telemetry.addData("Current velocity Left: ", outtake.getCurrentWheelVelocity("left"));
            telemetry.addData("Current velocity Right: ", outtake.getCurrentWheelVelocity("right"));
            telemetry.update();

            sleep(500);  // prevent spam
        }
    }
}
