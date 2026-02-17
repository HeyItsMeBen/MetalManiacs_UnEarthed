//package org.firstinspires.ftc.teamcode.Experimental.FlywheelTuning;
//
//import com.arcrobotics.ftclib.controller.PIDController;
//import com.qualcomm.robotcore.eventloop.opmode.Disabled;
//import com.qualcomm.robotcore.hardware.DcMotor;
//import com.qualcomm.robotcore.hardware.DcMotorEx;
//import com.qualcomm.robotcore.hardware.HardwareMap;
//import com.acmerobotics.dashboard.config.Config;
//
//// A simplified verison of the older outtakeWnheelVelocity_Tuner file
//
//@Disabled
//@Config
//public class FlywheelPIDClass {
//
//    // PID constants (tunable via Dashboard)
//
//    public static double maxVelocity = 1400;
//    public static double Kp = 0.002;
//    public static double Ki = 0.0;
//    public static double Kd = 0.0001;
//    public static double Kf = 1 / maxVelocity;
//
//    // Target velocity (ticks per second)
//    public static double targetVelocity = 1400;
//
//    // Two PID controllers — one for each flywheel
//    private final PIDController controller = new PIDController(Kp, Ki, Kd);
//
//    // Motors
//    public DcMotorEx flywheel;
//    public FlywheelPIDClass(HardwareMap hMap) {
//        // Make sure to use DcMotorEx for velocity access
//        flywheel = hMap.get(DcMotorEx.class, "flywheel");
//
//        flywheel.setDirection(DcMotorEx.Direction.FORWARD);
//
//        flywheel.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
//
//        flywheel.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
//    }
//
//    // Run this continuously in a loop
//    public void setFlywheelVelocity() {
//        controller.setPID(Kp, Ki, Kd);
//
//        double leftVelocity = flywheel.getVelocity();
//
//        double leftOutput = controller.calculate(leftVelocity, targetVelocity);
//
//        // Optional feedforward term (e.g., static power to overcome friction)
//        double leftPower = leftOutput + Kf * targetVelocity;
//
//        // Feedforward term proportional to target velocity
//
//        // Clip power to [-1, 1]
//        leftPower = Math.max(-1, Math.min(1, leftPower));
//
//        flywheel.setPower(leftPower);
//    }
//
//    public double getVelocity() {
//        return flywheel.getVelocity();
//    }
//}
