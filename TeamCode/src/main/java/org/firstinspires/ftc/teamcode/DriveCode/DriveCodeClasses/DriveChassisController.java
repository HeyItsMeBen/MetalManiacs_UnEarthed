package org.firstinspires.ftc.teamcode.DriveCode.DriveCodeClasses;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

public class DriveChassisController {

    private DcMotor frontLeft, frontRight, backLeft, backRight;
    private IMU imu;

    private double targetHeading = 0;
    private boolean useSnapRotation = true;
    private boolean useFieldCentricDrive = true;

    private double rotationKp = 4.0;
    private double rotationMaxSpeed = 1.0;
    private double speedMultiplier = 1.0;

    public DriveChassisController(HardwareMap hardwareMap) {

        frontLeft  = hardwareMap.get(DcMotor.class, "frontLeft");
        frontRight = hardwareMap.get(DcMotor.class, "frontRight");
        backLeft   = hardwareMap.get(DcMotor.class, "backLeft");
        backRight  = hardwareMap.get(DcMotor.class, "backRight");

        frontLeft.setDirection(DcMotor.Direction.FORWARD);
        frontRight.setDirection(DcMotor.Direction.REVERSE);
        backLeft.setDirection(DcMotor.Direction.REVERSE);
        backRight.setDirection(DcMotor.Direction.REVERSE);

        imu = hardwareMap.get(IMU.class, "imu");

        RevHubOrientationOnRobot orientationOnRobot =
                new RevHubOrientationOnRobot(
                        RevHubOrientationOnRobot.LogoFacingDirection.UP,
                        RevHubOrientationOnRobot.UsbFacingDirection.LEFT
                );

        imu.initialize(new IMU.Parameters(orientationOnRobot));

        targetHeading = getHeading();
    }

    public double getHeading() {
        return imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS);
    }

    public void resetYaw() {
        imu.resetYaw();
        targetHeading = 0;
    }

    public void toggleSnapRotation() {
        useSnapRotation = !useSnapRotation;
    }

    public void toggleFieldCentric() {
        useFieldCentricDrive = !useFieldCentricDrive;
    }

    public boolean isSnapRotation() {
        return useSnapRotation;
    }

    public boolean isFieldCentric() {
        return useFieldCentricDrive;
    }

    public void changeSpeedMultiplier(double amount) {
        speedMultiplier += amount;

        if (speedMultiplier > 1) speedMultiplier = 1;
        if (speedMultiplier < 0.1) speedMultiplier = 0.1;
    }

    public double getSpeedMultiplier() {
        return speedMultiplier;
    }

    public double calculateSnapRotation(double rightX, double rightY, boolean outtakeForward) {

        double magnitude = Math.hypot(rightX, rightY);

        if (magnitude < 0.1) {
            return 0;
        }

        double baseHeading = -(Math.atan2(rightY, rightX) - Math.PI / 2);

        targetHeading = outtakeForward
                ? AngleUnit.normalizeRadians(baseHeading + Math.PI)
                : baseHeading;

        return snapToHeading(targetHeading);
    }

    private double snapToHeading(double targetHeading) {

        double currentHeading = getHeading();

        double error = AngleUnit.normalizeRadians(targetHeading - currentHeading);

        double rotatePower = error * rotationKp;

        rotatePower = Math.max(-rotationMaxSpeed,
                Math.min(rotationMaxSpeed, rotatePower));

        if (Math.abs(error) < 0.02) {
            rotatePower = 0;
        }

        return rotatePower;
    }

    public void drive(double forward, double right, double rotate) {

        if (useFieldCentricDrive) {
            driveFieldRelative(forward, right, rotate);
        } else {
            driveRobotRelative(forward, right, rotate);
        }
    }

    private void driveFieldRelative(double forward, double right, double rotate) {

        double theta = Math.atan2(forward, right);
        double r = Math.hypot(right, forward);

        theta = AngleUnit.normalizeRadians(theta - getHeading());

        double newForward = r * Math.sin(theta);
        double newRight = r * Math.cos(theta);

        driveRobotRelative(newForward, newRight, rotate);
    }

    private void driveRobotRelative(double forward, double right, double rotate) {

        double fl = forward + right + rotate;
        double fr = forward - right - rotate;
        double br = forward + right - rotate;
        double bl = forward - right + rotate;

        double max = Math.max(1.0,
                Math.max(Math.abs(fl),
                        Math.max(Math.abs(fr),
                                Math.max(Math.abs(br), Math.abs(bl)))));

        double scale = 0.75 * speedMultiplier;

        setSafePower(frontLeft,  scale * fl / max);
        setSafePower(frontRight, scale * fr / max);
        setSafePower(backRight,  scale * br / max);
        setSafePower(backLeft,   scale * bl / max);
    }

    private void setSafePower(DcMotor motor, double targetPower) {

        final double SLEW_RATE = 0.5;

        double currentPower = motor.getPower();

        double change = targetPower - currentPower;

        change = Math.max(-SLEW_RATE, Math.min(SLEW_RATE, change));

        motor.setPower(currentPower + change);
    }
}
