package org.firstinspires.ftc.teamcode.DriveCode.Tuning;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.arcrobotics.ftclib.controller.PIDController;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;

//@Disabled
@Config
@TeleOp(name = "Flywheel PID Dashboard Tuner", group = "Tuning")
public class FlywheelPIDDashboardTuner extends OpMode {

    //public static double p = 100;
    public static double i = 0.0;
    public static double d = 0.0;
    public static double f = 14.12;

    public static double targetVelocity = 2000;

    // Motors
    public DcMotorEx flywheel;
    private FtcDashboard dashboard;

    @Override
    public void init(){
        flywheel = hardwareMap.get(DcMotorEx.class, "flywheel");
        flywheel.setDirection(DcMotorEx.Direction.REVERSE);
        flywheel.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
        flywheel.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER); // Keep this

        dashboard = FtcDashboard.getInstance();

//        PIDFCoefficients pidfCoefficients = new PIDFCoefficients(p, i, d, f);
//        flywheel.setPIDFCoefficients(DcMotorEx.RunMode.RUN_USING_ENCODER, pidfCoefficients);
        telemetry.addLine("Init complete");
    }

    @Override
    public void loop(){

        //flywheel.setPIDFCoefficients(DcMotorEx.RunMode.RUN_USING_ENCODER, pidfCoefficients);

        int curPosition = flywheel.getCurrentPosition(); // Add this for debugging

        //slower ramp-up
//        double maxAccelPerLoop = 10;
//        if (currentVelocity < targetVelocity) {
//            currentVelocity = Math.min(currentVelocity + maxAccelPerLoop, targetVelocity);
//        } else if (currentVelocity > targetVelocity) {
//            currentVelocity = Math.max(currentVelocity - maxAccelPerLoop, targetVelocity);
//        }
        //slower ramp-up

        flywheel.setVelocity(targetVelocity);

        double currentVel = flywheel.getVelocity();

        TelemetryPacket packet = new TelemetryPacket();
        packet.put("targetVelocity", targetVelocity);
        packet.put("currentVelocity", currentVel);
        packet.put("Error", targetVelocity - currentVel);
        dashboard.sendTelemetryPacket(packet);

        telemetry.addData("Current Position", curPosition); // Add this
        telemetry.addData("Target Velocity", targetVelocity);
        telemetry.addData("Current Velocity", currentVel);
        telemetry.addData("Error", targetVelocity - currentVel);
        telemetry.update();
    }
}
