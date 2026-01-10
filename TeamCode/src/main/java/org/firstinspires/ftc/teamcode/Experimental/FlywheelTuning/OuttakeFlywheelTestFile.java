package org.firstinspires.ftc.teamcode.Experimental.FlywheelTuning;

import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.arcrobotics.ftclib.gamepad.GamepadKeys;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Hardware.Flywheels;

import java.util.ArrayList;
import java.util.List;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;

// One of the biggest issue with our flywheels is that they drop in velocity when launching each time.
// As a result, this file will determine the time it takes for the velocities to return

@Disabled
@TeleOp (name="Outtake Flywheel Test File", group="test")
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
                outtake.setFlywheelVelocity(2350);
            }
            else if (gamepad.getButton(GamepadKeys.Button.B)) {
                outtake.setFlywheelVelocity(0);
            }

            //Send data to FTC dashbaord. The dashboard will graph it in real-time
            TelemetryPacket packet = new TelemetryPacket();
            packet.put("Left Motor Velocity", outtake.getCurrentWheelRawVelocity("left"));
            packet.put("Right Motor Velocity", outtake.getCurrentWheelRawVelocity("right"));
            dashboard.sendTelemetryPacket(packet);

            //Sends data to the driver station as quantitative values
            logHistory.add("Velocities: " + (int) Math.round(outtake.getCurrentWheelRawVelocity("left")) + " , " + (int) Math.round(outtake.getCurrentWheelRawVelocity("right")));
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
