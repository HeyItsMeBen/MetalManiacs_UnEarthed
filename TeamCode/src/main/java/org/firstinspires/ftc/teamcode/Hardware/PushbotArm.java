package org.firstinspires.ftc.teamcode.Hardware;

import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.hardwareMap;
import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.telemetry;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

public class PushbotArm {
        // PID
        public static double P = 0.1;
        public static double I = 0;
        public static double D = 0.0;

        // Gravity feedforward
        public static double F = 0.39;

        // Target arm position in encoder ticks
        public static int targetPosition = 0;
        public static double TICKS_PER_REV = 600;

        private DcMotorEx armMotor;
        private FtcDashboard dashboard;
        private double currentPosition;
        public PushbotArm(HardwareMap hardwareMap) {
            armMotor = hardwareMap.get(DcMotorEx.class, "armMotor");
            armMotor.setDirection(DcMotorEx.Direction.FORWARD);

            armMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            armMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

            currentPosition = armMotor.getCurrentPosition();

            dashboard = FtcDashboard.getInstance();

        }
        public double getArmPosition() {
            return armMotor.getCurrentPosition();
        }
        public void update() {

            double integral = 0;
            double previousError = 0;

            ElapsedTime timer = new ElapsedTime();


                double dt = timer.seconds();
                timer.reset();

                // Current position
                currentPosition = armMotor.getCurrentPosition();

                // Position error
                double error = targetPosition - currentPosition;

                // Integral
                integral += error * dt;

                // Derivative
                double derivative = 0;

                if (dt > 0) {
                    derivative = (error - previousError) / dt;
                }

                previousError = error;

                // Convert encoder position to angle
                // You need to change TICKS_PER_REV to your motor/gearbox setup.
                double angle = (currentPosition / TICKS_PER_REV) * 2 * Math.PI;

                // Gravity feedforward
                double feedforward = F * Math.cos(angle);

                // PID + feedforward
                double output =
                        P * error
                                + I * integral
                                + D * derivative
                                + feedforward;

                // Limit motor power
                output = Math.max(-1, Math.min(1, output));

                armMotor.setPower(output);
        }
}
