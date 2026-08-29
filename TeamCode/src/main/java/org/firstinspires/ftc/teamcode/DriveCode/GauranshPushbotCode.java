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
 *  LEFT TRIGGET (hold)  = claw inwards
 *
 *  [ARM]
 *  RIGHT STICK Y = arm up/down
 */
package org.firstinspires.ftc.teamcode.DriveCode;

import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.arcrobotics.ftclib.gamepad.GamepadKeys;
import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

import java.util.List;

@TeleOp(name = "PushBot v4a DriveCode-Gauransh", group = "A - TeleOP")
public class GauranshPushbotCode extends OpMode {
//variables
    public DcMotor leftDrive
    public DcMotor rightDrive;
    public DcMotor armMotor;
    public Servo leftClaw;
    public Servo rightClaw;
    public GamepadEx driver;

    @Override
    public void init() {
        leftDrive = hardwareMap.get(DcMotor.class,"frontLeft");
        rightDrive = hardwareMap.get(DcMotor.class,"frontRight");
        armMotor = hardwareMap.get(DcMotor.class,"armMotor");
        leftClaw = hardwareMap.get(Servo.class,"leftClaw");
        rightClaw = hardwareMap.get(Servo.class,"rightClaw");
        driver = new GamepadEx(gamepad1);
    }

    @Override
    public void loop() {
        //turning left
        if (driver.getLeftX() < 0) {
            leftDrive.setPower(-1);
            rightDrive.setPower(1);
        }
        //turning right
        if (driver.getLeftX() > 0) {
            leftDrive.setPower(1);
            rightDrive.setPower(-1);
        }
        //going backwards
        if (driver.getLeftY() < 0) {
            leftDrive.setPower(-1);
            rightDrive.setPower(-1);
        }
        //going forwards
        if (driver.getLeftY() > 0) {
            leftDrive.setPower(1);
            rightDrive.setPower(1);
        }
        //move arm up
        if (driver.getRightY() > 0) {
            armMotor.setPower(1);
        }
        //move arm down
        if (driver.getRightY() < 0) {
            armMotor.setPower(-1);
        }
        //claw movement inwards
        if (driver.getTrigger(GamepadKeys.Trigger.LEFT_TRIGGER) > 0.2) {
            leftClaw.setPosition(0.2);
            rightClaw.setPosition(0.2);
        }
        //claw movement outwards
        if (driver.getTrigger(GamepadKeys.Trigger.RIGHT_TRIGGER) > 0.2) {
            leftClaw.setPosition(0);
            rightClaw.setPosition(0);
        }
    }

    @Override
    public void stop() {
        armMotor.setPower(0);
        leftClaw.setPosition(0);
        rightClaw.setPosition(0);
        leftDrive.setPower(0);
        rightDrive.setPower(0);
    }
}
