package org.firstinspires.ftc.teamcode.PID_Tuners.FlywheelTuning;

import com.arcrobotics.ftclib.controller.PIDController;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.acmerobotics.dashboard.config.Config;

@Config
public class PIDFSync {

    // PID constants for target velocity
    public static double Kp = 0.000205; //from tuning
    public static double Ki = 0.0;
    public static double Kd = 0.0001; //needs to be tuned still

    // Feedforward constant
    //public static double Kf = 1.0 / 5000.0; // max motor velocity goes on  0.00042
    public static double Kf = 0.00042; // from testing

    // Left-right sync constant
    public static double K_sync = 0.001; // adjust via dashboard

    // Target velocity (ticks/sec)
    public static double targetVelocity = 1000;

    private final PIDController leftController = new PIDController(Kp, Ki, Kd);
    private final PIDController rightController = new PIDController(Kp, Ki, Kd);

    public DcMotorEx leftFlywheel;
    public DcMotorEx rightFlywheel;

    public PIDFSync(HardwareMap hMap) {
        leftFlywheel = hMap.get(DcMotorEx.class, "leftFlyWheel");
        rightFlywheel = hMap.get(DcMotorEx.class, "rightFlyWheel");

        leftFlywheel.setDirection(DcMotorEx.Direction.FORWARD);
        rightFlywheel.setDirection(DcMotorEx.Direction.REVERSE);

        leftFlywheel.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
        rightFlywheel.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
    }

    /** Call this continuously in loop */
    public void update() {
        leftController.setPID(Kp, Ki, Kd);
        rightController.setPID(Kp, Ki, Kd);

        double leftVel = leftFlywheel.getVelocity();
        double rightVel = rightFlywheel.getVelocity();

        // PID toward target
        double leftOutput = leftController.calculate(targetVelocity, leftVel);
        double rightOutput = rightController.calculate(targetVelocity, rightVel);

        // Cross-coupling term to minimize left-right difference
        double delta = leftVel - rightVel;
        leftOutput  -= K_sync * delta;
        rightOutput += K_sync * delta;

        // Feedforward
        double leftPower = leftOutput + Kf * targetVelocity;
        double rightPower = rightOutput + Kf * targetVelocity;

        // Clip to [-1, 1]
        leftPower = Math.max(-1, Math.min(1, leftPower));
        rightPower = Math.max(-1, Math.min(1, rightPower));

        leftFlywheel.setPower(leftPower);
        rightFlywheel.setPower(rightPower);
    }

    /** Getters */
    public double getLeftVelocity() { return leftFlywheel.getVelocity(); }
    public double getRightVelocity() { return rightFlywheel.getVelocity(); }
    public double getAverageVelocity() { return (getLeftVelocity() + getRightVelocity()) / 2.0; }

    public double getTargetVelocity() {
        return targetVelocity;
    }

    /** Update target velocity dynamically */
    public void setTargetVelocity(double velocity) { targetVelocity = velocity; }
}
