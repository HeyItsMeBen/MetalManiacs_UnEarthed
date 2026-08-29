/**
 *  <DRIVER MANUAL>
 *
 *  --DRIVER CONTROLS--
 *
 *  [MOVEMENT]
 *  LEFT STICK Y = forward / backward
 *  LEFT STICK X = turn
 *
 *  [HAND / GRIPPER]
 *  RIGHT TRIGGER (hold) = claw outwards
 *  LEFT TRIGGER (hold)  = claw inwards
 *
 *  [ARM]
 *  RIGHT STICK Y = arm up/down
 */

        package org.firstinspires.ftc.teamcode.DriveCode;

import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.arcrobotics.ftclib.gamepad.GamepadKeys;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

@TeleOp(name = "PushBot v4b DriveCode-Gauransh", group = "A - TeleOP")
public class GauranshPushbotCode extends OpMode {

    //variables

    public static final double ARM_MANUAL_POWER = 0.7;
    public static final double ARM_GRAVITY_POWER = 0.1;

    public static final double DEADZONE = 0.25;

    public DcMotor leftDrive;
    public DcMotor rightDrive;
    public DcMotor armMotor;
    public Servo leftClaw;
    public Servo rightClaw;
    public GamepadEx driver;



    @Override
    public void init() {

        leftDrive = hardwareMap.get(DcMotor.class, "leftDrive");
        rightDrive = hardwareMap.get(DcMotor.class, "rightDrive");

        armMotor = hardwareMap.get(DcMotor.class, "armMotor");

        leftClaw = hardwareMap.get(Servo.class, "leftClaw");
        rightClaw = hardwareMap.get(Servo.class, "rightClaw");

        driver = new GamepadEx(gamepad1);
        // Start with the motors stopped
        leftDrive.setPower(0);
        rightDrive.setPower(0);
        armMotor.setPower(0);
    }

    @Override
    public void loop() {

        double drive = -driver.getLeftY();
        double turn = driver.getLeftX();

        // Deadzone
        if (Math.abs(drive) < DEADZONE) {
            drive = 0;
        }

        if (Math.abs(turn) < DEADZONE) {
            turn = 0;
        }

        // drive
        double leftPower = drive + turn;
        double rightPower = drive - turn;

        // Keep motor power between -1 and 1
        leftPower = Range.clip(leftPower, -1, 1);
        rightPower = Range.clip(rightPower, -1, 1);

        leftDrive.setPower(leftPower);
        rightDrive.setPower(rightPower);

        // arm
        double armInput = -driver.getRightY();

        if (Math.abs(armInput) > DEADZONE) {

            if (armInput > 0) {
                armMotor.setPower(ARM_MANUAL_POWER + ARM_GRAVITY_POWER);
            } else {
                armMotor.setPower(-ARM_MANUAL_POWER + ARM_GRAVITY_POWER);
            }

        } else {

            // Hold arm against gravity
            armMotor.setPower(ARM_GRAVITY_POWER);
        }

        double leftTrigger =
                driver.getTrigger(GamepadKeys.Trigger.LEFT_TRIGGER);

        double rightTrigger =
                driver.getTrigger(GamepadKeys.Trigger.RIGHT_TRIGGER);

        // Left trigger = claw inward
        if (leftTrigger > 0.2) {

            leftClaw.setPosition(0.2);
            rightClaw.setPosition(0.2);
        }

        // Right trigger = claw outward
        else if (rightTrigger > 0.2) {

            leftClaw.setPosition(0);
            rightClaw.setPosition(0);
        }

        telemetry.addData("Left Drive", leftPower);
        telemetry.addData("Right Drive", rightPower);
        telemetry.addData("Arm", armMotor.getPower());
        telemetry.addData("Left Claw", leftTrigger);
        telemetry.addData("Right Claw", rightTrigger);
        telemetry.update();
    }

    @Override
    public void stop() {

        armMotor.setPower(0);

        leftDrive.setPower(0);
        rightDrive.setPower(0);

        leftClaw.setPosition(0);
        rightClaw.setPosition(0);
    }
}