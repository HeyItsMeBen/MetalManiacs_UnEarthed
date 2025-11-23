package org.firstinspires.ftc.teamcode.Testing;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
@Disabled

@Config
@TeleOp(name = "Linear Slide PID Tuner", group = "Tuning")
public class LinearSlidePIDTuner extends LinearOpMode {

    public static double target = 500; // encoder ticks
    private compLinearSlide slide;

    @Override
    public void runOpMode() throws InterruptedException {
        slide = new compLinearSlide(hardwareMap);
        FtcDashboard dashboard = FtcDashboard.getInstance();

        waitForStart();

        while (opModeIsActive()) {
            slide.setSlidesTarget(target);

            TelemetryPacket packet = new TelemetryPacket();
            packet.put("target", target);
            packet.put("current", slide.rightSlide.getCurrentPosition());
            dashboard.sendTelemetryPacket(packet);

            telemetry.addData("target", target);
            telemetry.addData("current", slide.rightSlide.getCurrentPosition());
            telemetry.update();
        }
    }
}
