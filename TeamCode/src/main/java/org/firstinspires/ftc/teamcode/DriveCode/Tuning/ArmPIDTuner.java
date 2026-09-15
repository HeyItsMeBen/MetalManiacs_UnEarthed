package org.firstinspires.ftc.teamcode.DriveCode.Tuning;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

@Config
public class ArmPIDTuner {
    // PID
    public static double P = 0.1;
    public static double I = 0;
    public static double D = 0.0;

    // Gravity feedforward
    public static double F = 0.39;

    // Target arm position in encoder ticks
    public static int targetPosition = 0;
    public static double TICKS_PER_REV = 600;

    private final DcMotorEx armMotor;
    private final FtcDashboard dashboard;
    private final ElapsedTime timer;

    private double integral = 0;
    private double previousError = 0;

    public ArmPIDTuner(HardwareMap hardwareMap) {
        armMotor = hardwareMap.get(DcMotorEx.class, "armMotor");
        armMotor.setDirection(DcMotorEx.Direction.FORWARD);

        armMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        armMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        dashboard = FtcDashboard.getInstance();
        timer = new ElapsedTime();
    }

    public double getArmPosition() {
        return armMotor.getCurrentPosition();
    }

    /** Runs one PID + gravity-feedforward step and drives the arm motor toward targetPosition. */
    public void update() {
        double dt = timer.seconds();
        timer.reset();

        double currentPosition = getArmPosition();
        double error = targetPosition - currentPosition;

        integral += error * dt;
        double derivative = dt > 0 ? (error - previousError) / dt : 0;
        previousError = error;

        // Convert encoder position to angle for the gravity feedforward term
        double angle = (currentPosition / TICKS_PER_REV) * 2 * Math.PI;
        double feedforward = F * Math.cos(angle);

        double output = P * error + I * integral + D * derivative + feedforward;
        output = Math.max(-1, Math.min(1, output));

        armMotor.setPower(output);

        TelemetryPacket packet = new TelemetryPacket();
        packet.put("Arm Target", targetPosition);
        packet.put("Arm Position", currentPosition);
        packet.put("Arm Error", error);
        packet.put("Arm Output", output);
        packet.put("Arm Feedforward", feedforward);
        dashboard.sendTelemetryPacket(packet);
    }

    public void stopMotor() {
        armMotor.setPower(0);
        integral = 0;
        previousError = 0;
    }
}
