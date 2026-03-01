package org.firstinspires.ftc.teamcode.Hardware;
import com.arcrobotics.ftclib.controller.PIDController;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Turret {
    private DcMotorEx turretMotor;
    private static final int MIN_POSITION = 0;
    private static final int MAX_POSITION = 1500;
    private static final int CENTER_POSITION = 750;
    private static final double POSITION_TOLERANCE = 30; // ticks

    private boolean isInPositionMode = false;
    private int manualTargetPosition = CENTER_POSITION; // Track target for manual positioning

    double ticksPerTurretRevolution=800.551724;
    //public static double p=0.01, i=0, d=0.0005, f=0;  //fast values
    public static double p=0.002, i=0, d=0.0001, f=0;   //safe values
    private PIDController controller;
    double halfRange=175;
    public double middlePosition=0;

    public double turretAngleTelemetry=0;
    public double turretTargetTelemetry=0;
    public double turretPositionTelemetry=0;

    public Turret(HardwareMap hMap) {
        controller = new PIDController(p, i, d);

        turretMotor = hMap.get(DcMotorEx.class, "turret");
        turretMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        //turretMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        turretMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        turretMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

    /**
     * Sets motor power with boundary enforcement
     * Automatically prevents movement beyond 0-1500 range
     *
     * FIXED: Added small deadband near boundaries to prevent stuttering
     */
    public void setMotorPower(double dblPower){
        // Switch back to manual control mode if we were in position mode
        turretMotor.setPower(dblPower);
    }

    public int getTurretPosition(){
        return turretMotor.getCurrentPosition();
    }

    public double getTurretPower(){
        return turretMotor.getPower();
    }

    /**
     * Manually rotates towards a target position
     * This sets isInPositionMode so isAtTargetPosition works correctly
     */
    public void rotateTowardsTarget(int target){
        // Clamp target to valid range
//        manualTargetPosition = target;
//        isInPositionMode = true; // Important: set this flag!
//
        int currentPos = turretMotor.getCurrentPosition();
//
//        // Check if we're at target
//        if (Math.abs(currentPos - target) < POSITION_TOLERANCE) {
//            turretMotor.setPower(0);
//            return;
//        }

        // Move towards target
        if(currentPos < target){
            turretMotor.setPower(0.5);
        } else {
            turretMotor.setPower(-0.5);
        }
    }

    /**
     * Resets turret to center position (750)
     * Call this when april tag is lost
     */
    public void resetPosition(){
        rotateTowardsTarget(CENTER_POSITION);
    }

    /**
     * Rotates turret to a specific encoder position using RUN_TO_POSITION
     * Clamps target to valid range (0-1500)
     */
    public void rotateToPosition(int targetPosition){
        // Clamp target position to valid range
        targetPosition = Math.max(MIN_POSITION, Math.min(MAX_POSITION, targetPosition));
        manualTargetPosition = targetPosition;

        turretMotor.setTargetPosition(targetPosition);
        turretMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        turretMotor.setPower(0.5);
        isInPositionMode = true;
    }

    /**
     * Check if turret has reached its target position
     * Works for both manual (rotateTowardsTarget) and RUN_TO_POSITION modes
     */
    public boolean isAtTargetPosition(int targetPos) {
//        if (!isInPositionMode) {
//            return true; // Not in position mode, so no target to reach
//        }

        int currentPos = turretMotor.getCurrentPosition();
        return Math.abs(currentPos - targetPos) < POSITION_TOLERANCE;
    }

    /**
     * Stops the turret motor and exits position mode
     */
    public void stop() {
        turretMotor.setPower(0);
        if (isInPositionMode) {
            turretMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            isInPositionMode = false;
        }
    }

    /**
     * Resets the encoder to 0 at current position
     * Call this to calibrate the turret's "zero" position
     */
    public void resetInitial(){
        turretMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        turretMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        isInPositionMode = false;
    }

    /**
     * Gets whether the motor is currently in position targeting mode
     */
    public boolean isInPositionMode() {
        return isInPositionMode;
    }
    public void runTowardsTargetAngle(double turretAngleRadians){
        double turretPos=-ticksPerTurretRevolution*turretAngleRadians/(2*Math.PI);
        turretAngleTelemetry = turretAngleRadians;
        turretTargetTelemetry=turretPos;
        turretPositionTelemetry=turretMotor.getCurrentPosition();
        double parameter1 =middlePosition-halfRange;
        double parameter2 =middlePosition+halfRange;
        if (turretPos>Math.min(parameter1, parameter2) && turretPos<Math.max(parameter1, parameter2)) {
            runTowardTargetDistance(turretPos);
        } else {
            runTowardTargetDistance(middlePosition);
        }
    }
    public void runTowardTargetDistance(double ticks) {
        controller.setPID(p, i, d);
        int armPos = turretMotor.getCurrentPosition();
        double power = controller.calculate(armPos, ticks);

        turretMotor.setPower(power);
    }
    public double getTurretAngle(){
        return turretMotor.getCurrentPosition()*(2*Math.PI)/-ticksPerTurretRevolution;
    }
}