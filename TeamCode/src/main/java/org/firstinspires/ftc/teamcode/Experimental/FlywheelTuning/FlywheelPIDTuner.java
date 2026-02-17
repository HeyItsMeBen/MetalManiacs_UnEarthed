package org.firstinspires.ftc.teamcode.Experimental.FlywheelTuning;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.arcrobotics.ftclib.controller.PIDController;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

@Disabled
@Config
@TeleOp(name = "Flywheel Velocity PID Tuner", group = "Tuning")
public class FlywheelPIDTuner extends LinearOpMode {
    public static double Kp = 0.002;
    public static double Ki = 0.0;
    public static double Kd = 0.01;
    public static double Kf = 0.0007142857142857143;

    public static double targetVelocity = 2000;

    private final PIDController controller = new PIDController(Kp, Ki, Kd);

    // Motors
    public DcMotorEx flywheel;
    private FtcDashboard dashboard;

    @Override
    public void runOpMode() throws InterruptedException {

        flywheel = hardwareMap.get(DcMotorEx.class, "flywheel");

        flywheel.setDirection(DcMotorEx.Direction.REVERSE);

        flywheel.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        flywheel.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        dashboard = FtcDashboard.getInstance();

        waitForStart();

        while (opModeIsActive()) {

            controller.setPID(Kp, Ki, Kd);

            double currentVelocity = flywheel.getVelocity();

            double Output = controller.calculate(currentVelocity, targetVelocity);

            double Power = Output + Kf * targetVelocity;

            Power = Math.max(-1, Math.min(1, Power));

            //slower ramp-up
            double maxAccelPerLoop = 10;
            if (currentVelocity < targetVelocity) {
                currentVelocity = Math.min(currentVelocity + maxAccelPerLoop, targetVelocity);
            } else if (currentVelocity > targetVelocity) {
                currentVelocity = Math.max(currentVelocity - maxAccelPerLoop, targetVelocity);
            }
            //slower ramp-up

            flywheel.setVelocity(currentVelocity);


            flywheel.setPower(Power);

            double currentVel = flywheel.getVelocity();

            TelemetryPacket packet = new TelemetryPacket();
            packet.put("targetVelocity", targetVelocity);
            packet.put("currentVelocity", currentVel);
            packet.put("Error", targetVelocity - currentVel);
            dashboard.sendTelemetryPacket(packet);

            telemetry.addData("Target Velocity", targetVelocity);
            telemetry.addData("Left Velocity", currentVel);
            telemetry.addData("Error", targetVelocity - currentVel);
            telemetry.update();

        }
    }
}
