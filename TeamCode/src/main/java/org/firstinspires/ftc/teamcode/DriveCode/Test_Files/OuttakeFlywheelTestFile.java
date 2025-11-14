package org.firstinspires.ftc.teamcode.DriveCode.Test_Files;

import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.arcrobotics.ftclib.gamepad.GamepadKeys;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Systems.Flywheels;

import java.util.ArrayList;
import java.util.List;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;

// One of the biggest issue with our flywheels is that they drop in velocity when launching each time.
// As a result, this file will determine the time it takes for the velocities to return

@TeleOp (name="Outtake Flywheel Test", group="test")
public class OuttakeFlywheelTestFile extends LinearOpMode {

    public GamepadEx gamepad;

    Flywheels outtake;

    @Override
    public void runOpMode() {

        gamepad = new GamepadEx(gamepad1);
        outtake = new Flywheels(hardwareMap);

        FtcDashboard dashboard = FtcDashboard.getInstance();

        List<String> logHistory = new ArrayList<>();

        waitForStart();

        //executing
        while (opModeIsActive()) {
            if (gamepad.getButton(GamepadKeys.Button.A)) {
                outtake.setFlywheelRawVelocity(2350);
            }
            else if (gamepad.getButton(GamepadKeys.Button.B)) {
                outtake.setFlywheelVelocity(0);
            }

            //Send data to FTC dashbaord. The dashboard will graph it in real-time
            TelemetryPacket packet = new TelemetryPacket();
            packet.put("Left Motor Velocity", outtake.getCurrentWheelVelocity("left"));
            packet.put("Right Motor Velocity", outtake.getCurrentWheelVelocity("right"));
            dashboard.sendTelemetryPacket(packet);

            //Sends data to the driver station as quantitative values
            logHistory.add("Velocities: " + (int) Math.round(outtake.getCurrentWheelVelocity("left")) + " , " + (int) Math.round(outtake.getCurrentWheelVelocity("right")));
            int maxLines = 8; // Display the last few entries (so it doesn’t overflow)
            int start = Math.max(0, logHistory.size() - maxLines);
            for (int i = start; i < logHistory.size(); i++) {
                telemetry.addLine(logHistory.get(i));
            }
            telemetry.update();
            sleep(500);  // prevent spam
        }
    }
}
