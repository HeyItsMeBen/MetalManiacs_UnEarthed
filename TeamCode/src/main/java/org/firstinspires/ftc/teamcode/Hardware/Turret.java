package org.firstinspires.ftc.teamcode.Hardware;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Turret {
    private DcMotorEx turretMotor;
    private static final int MIN_POSITION = 0;
    private static final int MAX_POSITION = 1500;
    private static final int CENTER_POSITION = 750;
    private static final double POSITION_TOLERANCE = 10; // ticks

    private boolean isInPositionMode = false;

    public Turret(HardwareMap hMap) {
        turretMotor = hMap.get(DcMotorEx.class, "turret");
        turretMotor.setDirection(DcMotorSimple.Direction.FORWARD);
        turretMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        turretMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

    /**
     * Sets motor power with boundary enforcement
     * Automatically prevents movement beyond 0-1500 range
     */
    public void setMotorPower(double dblPower){
        // Switch back to manual control mode if we were in position mode
        if (isInPositionMode) {
            turretMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            isInPositionMode = false;
        }

        int currentPos = turretMotor.getCurrentPosition();

        // Enforce boundaries
        if (currentPos <= MIN_POSITION && dblPower < 0) {
            // At or beyond left limit, don't allow further left movement
            turretMotor.setPower(0);
        } else if (currentPos >= MAX_POSITION && dblPower > 0) {
            // At or beyond right limit, don't allow further right movement
            turretMotor.setPower(0);
        } else {
            // Within bounds, allow movement
            turretMotor.setPower(dblPower);
        }
    }

    public int getTurretPosition(){
        return turretMotor.getCurrentPosition();
    }

    /**
     * Resets turret to center position (750)
     * Call this when april tag is lost
     */

    public void rotateTowardsTarget(int target){
        if(turretMotor.getCurrentPosition() < target){
            turretMotor.setPower(0.5);
        }else if (turretMotor.getCurrentPosition() > target){
            turretMotor.setPower(-0.5);
        }
    }
    public void resetPosition(){
        rotateToPosition(CENTER_POSITION);
    }

    /**
     * Rotates turret to a specific encoder position
     * Clamps target to valid range (0-1500)
     */
    public void rotateToPosition(int targetPosition){
        // Clamp target position to valid range
        targetPosition = Math.max(MIN_POSITION, Math.min(MAX_POSITION, targetPosition));

        turretMotor.setTargetPosition(targetPosition);
        turretMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        turretMotor.setPower(0.5);
        isInPositionMode = true;
    }

    /**
     * Check if turret has reached its target position
     * Useful for knowing when position commands are complete
     */
    public boolean isAtTargetPosition(double targetPos) {
        if (!isInPositionMode) {
            return true; // Not in position mode, so no target to reach
        }

        int currentPos = turretMotor.getCurrentPosition();
        return Math.abs(currentPos - targetPos) < POSITION_TOLERANCE;
    }

    /**
     * Stops the turret motor
     */
    public void stop() {
        turretMotor.setPower(0);
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
     * Gets whether the motor is currently in RUN_TO_POSITION mode
     */
    public boolean isInPositionMode() {
        return isInPositionMode;
    }
}