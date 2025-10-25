/* Copyright (c) 2025 FIRST. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted (subject to the limitations in the disclaimer below) provided that
 * the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * Neither the name of FIRST nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written permission.
 *
 * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
 * LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.firstinspires.ftc.teamcode.DriveCode;

import static java.lang.Thread.sleep;

import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.arcrobotics.ftclib.gamepad.GamepadKeys;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.Systems.Hinge;
import org.firstinspires.ftc.teamcode.Systems.Intake;
import org.firstinspires.ftc.teamcode.Systems.Outtake;
import org.firstinspires.ftc.teamcode.Systems.Arm;

/*
 * This OpMode illustrates how to program your robot to drive field relative.  This means
 * that the robot drives the direction you push the joystick regardless of the current orientation
 * of the robot.
 *
 * This OpMode assumes that you have four mecanum wheels each on its own motor named:
 *   front_left_motor, front_right_motor, back_left_motor, back_right_motor
 *
 *   and that the left motors are flipped such that when they turn clockwise the wheel moves backwards
 *
 * Use Android Studio to Copy this Class, and Paste it into your team's code folder with a new name.
 * Remove or comment out the @Disabled line to add this OpMode to the Driver Station OpMode list
 *
 */
//0.29//0.44
@TeleOp(name = "Competition DriveCode", group = "Robot")
public class DriveCode extends OpMode {

    // Driver Code
    public GamepadEx driver;
    public GamepadEx operator;

    DcMotor frontLeftDrive;
    DcMotor frontRightDrive;
    DcMotor backLeftDrive;
    DcMotor backRightDrive;

    Intake intake;
    Outtake outtake;
    Arm arm;
    Hinge hinge;

    ElapsedTime timer;

    private int intakePower = 0;
    private boolean flyWheelOn = false;
    double velocityPeak=0;
    boolean holdPosition_Arm=false;
    boolean armIsMoving=false;
    double armTarget=0;

    // This declares the IMU needed to get the current direction the robot is facing
    IMU imu;

    @Override
    public void init() {

        driver = new GamepadEx(gamepad1);
        operator = new GamepadEx(gamepad2);

        frontLeftDrive = hardwareMap.get(DcMotor.class, "frontLeft");
        frontRightDrive = hardwareMap.get(DcMotor.class, "frontRight");
        backLeftDrive = hardwareMap.get(DcMotor.class, "backLeft");
        backRightDrive = hardwareMap.get(DcMotor.class, "backRight");

        intake = new Intake(hardwareMap);
        arm = new Arm(hardwareMap);
        outtake = new Outtake(hardwareMap);
        hinge = new Hinge(hardwareMap);

        arm.resetArmEncoders();

        // We set the left motors in reverse which is needed for drive trains where the left
        // motors are opposite to the right ones.
        backLeftDrive.setDirection(DcMotor.Direction.FORWARD);
        frontLeftDrive.setDirection(DcMotor.Direction.FORWARD);
        backRightDrive.setDirection(DcMotor.Direction.REVERSE);
        frontRightDrive.setDirection(DcMotor.Direction.REVERSE);
        // This uses RUN_USING_ENCODER to be more accurate.   If you don't have the encoder
        // wires, you should remove these

        imu = hardwareMap.get(IMU.class, "imu");
        // This needs to be changed to match the orientation on your robot
        RevHubOrientationOnRobot.LogoFacingDirection logoDirection = RevHubOrientationOnRobot.LogoFacingDirection.UP;
        RevHubOrientationOnRobot.UsbFacingDirection usbDirection = RevHubOrientationOnRobot.UsbFacingDirection.LEFT;

        RevHubOrientationOnRobot orientationOnRobot = new RevHubOrientationOnRobot(logoDirection, usbDirection);
        imu.initialize(new IMU.Parameters(orientationOnRobot));
        timer = new ElapsedTime();
    }

    @Override
    public void loop() {
        telemetry.addLine("Press A to reset Yaw");
        telemetry.addLine("Hold left bumper to drive in robot relative");
        telemetry.addLine("The left joystick sets the robot direction");
        telemetry.addLine("Moving the right joystick left and right turns the robot");

        // If you press the A button, then you reset the Yaw to be zero from the way
        // the robot is currently pointing
        if (driver.getButton(GamepadKeys.Button.A)){
            imu.resetYaw();
        }

        // If you press the left bumper, you get a drive from the point of view of the robot
        // (much like driving an RC vehicle)
        //with operator gamepads:
        if(driver.getButton(GamepadKeys.Button.LEFT_STICK_BUTTON)){
            //normal drive
            drive(-driver.getLeftY(), -driver.getLeftX(),-driver.getRightX());
        }else{
            //field centric
            driveFieldRelative(-driver.getLeftY(), -driver.getLeftX(),-driver.getRightX());
        }


        //manual inttake control
        //right bumper in
        //left bumper out
        //clicking any bumper again will close
        if (driver.wasJustPressed((GamepadKeys.Button.RIGHT_BUMPER))){
            if(Math.abs(intakePower) == 1){
                intakePower = 0;
            }else{
                intakePower = 1;
            }
        } else if (driver.wasJustPressed(GamepadKeys.Button.LEFT_BUMPER)) {
            if(Math.abs(intakePower) == 1){
                intakePower = 0;
            }else{
                intakePower = -1;
            }
        }
        intake.setMotorPower(intakePower);
        //arm
        // ARM CANNOT LIFT SO DONT OVERSTRAIN THE MOTOR WHEN TESTING
        if (operator.getButton(GamepadKeys.Button.DPAD_UP)) {
            //arm.moveArmTo(700, 2);
            armTarget=700;
            timer.reset();
            holdPosition_Arm=true;
        } else if (operator.getButton(GamepadKeys.Button.DPAD_DOWN)) {
            //arm.moveArmTo(100, 2);
            armTarget=100;
            timer.reset();
            holdPosition_Arm=false;
        } //add manual lift later
        else if (operator.getButton(GamepadKeys.Button.DPAD_RIGHT)) {
            armTarget=0;
            arm.raiseArmManual(0.25);
            ElapsedTime timer1;
            timer1 = new ElapsedTime();
            while (timer1.milliseconds()/1000<2){}
            arm.resetArmEncoders();
        }

        if (operator.getButton(GamepadKeys.Button.RIGHT_BUMPER)) {
            outtake.setFlywheelVelocity(3000, 1, 700);
            hinge.liftHingeAndWait(hinge.firePosition, 1, 700);
            velocityPeak=outtake.getCurrentWheelRPM();
            outtake.setFlywheelVelocity(0, 0);
            hinge.liftHingeAndWait(hinge.holdPosition, 0);
        }
        else if (operator.getButton(GamepadKeys.Button.LEFT_BUMPER)) {
            hinge.liftHingeAndWait(hinge.firePosition, 1, 700);
        }

        //flywheels launched with gamepad B
        if (operator.wasJustPressed(GamepadKeys.Button.B)){
            flyWheelOn = !flyWheelOn; // toggle
        }
        if (flyWheelOn) {
            telemetry.addData("Fly Wheel:", "On");
            outtake.setFlywheelVelocity(3000, 0);
        } else {
            telemetry.addData("Fly Wheel:", "Off");
            outtake.setFlywheelVelocity(0, 0);
        }
        telemetry.addData("VELOCITY: ", velocityPeak);
        telemetry.update();

//        operators use left stick to aim the outtake up and down
        //arm.setArmTarget(operator.getLeftY()*400);

        //Constant PID control. Allows mechanisms to hold their position. Right now, just the arm uses PID, but there may be more later.
        if (timer.milliseconds()/1000<2){armIsMoving=true;}
        if (holdPosition_Arm || armIsMoving){arm.raiseArmManual(arm.setArmTarget(armTarget));}


        //sort stuff
        //sort()
        driver.readButtons();
        operator.readButtons();
    }

    // This routine drives the robot field relative
    private void driveFieldRelative(double forward, double right, double rotate) {
        // First, convert direction being asked to drive to polar coordinates
        double theta = Math.atan2(forward, right);
        double r = Math.hypot(right, forward);

        // Second, rotate angle by the angle the robot is pointing
        theta = AngleUnit.normalizeRadians(theta -
                imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS));

        // Third, convert back to cartesian
        double newForward = r * Math.sin(theta);
        double newRight = r * Math.cos(theta);

        // Finally, call the drive method with robot relative forward and right amounts
        drive(newForward, newRight, rotate);
    }

    // This routine drives the robot regularly
    public void drive(double forward, double right, double rotate) {

        double frontLeftPower = forward + right + rotate;
        double frontRightPower = forward - right - rotate;
        double backRightPower = forward + right - rotate;
        double backLeftPower = forward - right + rotate;

        double maxPower = 1.0;
        double maxSpeed = .75;  // make this slower for outreaches

        maxPower = Math.max(maxPower, Math.abs(frontLeftPower));
        maxPower = Math.max(maxPower, Math.abs(frontRightPower));
        maxPower = Math.max(maxPower, Math.abs(backRightPower));
        maxPower = Math.max(maxPower, Math.abs(backLeftPower));

        frontLeftDrive.setPower(maxSpeed * (frontLeftPower / maxPower));
        frontRightDrive.setPower(maxSpeed * (frontRightPower / maxPower));
        backLeftDrive.setPower(maxSpeed * (backLeftPower / maxPower));
        backRightDrive.setPower(maxSpeed * (backRightPower / maxPower));
    }
}
