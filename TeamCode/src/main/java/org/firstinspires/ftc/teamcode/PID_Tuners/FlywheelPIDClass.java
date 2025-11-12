package org.firstinspires.ftc.teamcode.PID_Tuners;

import com.arcrobotics.ftclib.controller.PIDController;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.acmerobotics.dashboard.config.Config;

@Config
public class FlywheelPIDClass {

    // PID constants (tunable via Dashboard)
    public static double Kp = 0.002;
    public static double Ki = 0.0;
    public static double Kd = 0.0001;
    public static double Kf = 0.0;

    // Target velocity (ticks per second)
    public static double targetVelocity = 2000;

    // Two PID controllers — one for each flywheel
    private final PIDController leftController = new PIDController(Kp, Ki, Kd);
    private final PIDController rightController = new PIDController(Kp, Ki, Kd);

    // Motors
    public DcMotorEx leftFlywheel;
    public DcMotorEx rightFlywheel;

    public FlywheelPIDClass(HardwareMap hMap) {
        // Make sure to use DcMotorEx for velocity access
        leftFlywheel = hMap.get(DcMotorEx.class, "leftFlywheel");
        rightFlywheel = hMap.get(DcMotorEx.class, "rightFlywheel");

        leftFlywheel.setMode(DcMotorEx.RunMode.RUN_WITHOUT_ENCODER);
        rightFlywheel.setMode(DcMotorEx.RunMode.RUN_WITHOUT_ENCODER);
    }

    // Run this continuously in a loop
    public void setFlywheelVelocity() {
        leftController.setPID(Kp, Ki, Kd);
        rightController.setPID(Kp, Ki, Kd);

        double leftVelocity = leftFlywheel.getVelocity();
        double rightVelocity = rightFlywheel.getVelocity();

        double leftOutput = leftController.calculate(leftVelocity, targetVelocity);
        double rightOutput = rightController.calculate(rightVelocity, targetVelocity);

        // Optional feedforward term (e.g., static power to overcome friction)
        double leftPower = leftOutput + Kf;
        double rightPower = rightOutput + Kf;

        leftFlywheel.setPower(leftPower);
        rightFlywheel.setPower(rightPower);
    }

    /** Returns left flywheel velocity individually. */
    public double getLeftVelocity() {
        return leftFlywheel.getVelocity();
    }

    /** Returns right flywheel velocity individually. */
    public double getRightVelocity() {
        return rightFlywheel.getVelocity();
    }

    public double getAverageVelocity() {
        double leftVel = leftFlywheel.getVelocity();
        double rightVel = rightFlywheel.getVelocity();
        return (leftVel + rightVel) / 2.0; // average
    }

}
