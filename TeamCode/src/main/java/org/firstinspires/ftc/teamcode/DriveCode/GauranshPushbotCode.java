/**
 *  <DRIVER MANUAL>
 *
 *  --DRIVER CONTROLS--
 *
 *  [MOVEMENT]
 *  LEFT STICK Y = forward / backward
 *  RIGHT STICK X = turn
 *  DPAD UP       = drive speed up
 *  DPAD DOWN     = drive speed down
 *
 *  [ARM]
 *  RIGHT BUMPER (hold) = raise arm
 *  LEFT BUMPER (hold)  = lower arm
 *
 *  [HAND / GRIPPER]
 *  A = open gripper
 *  B = close gripper
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

@TeleOp(name = "PushBot v4a DriveCode", group = "A - TeleOP")
public class GauranshPushbotCode extends OpMode {

    public DcMotor frontLeft;
    public DcMotor frontRight;
    public DcMotor armMotor;
    public Servo claw1;
    public Servo claw2;
    public GamepadEx driver;

    @Override
    public void init() {
        frontLeft = hardwareMap.get(DcMotor.class,"frontLeft");
        frontRight = hardwareMap.get(DcMotor.class,"frontRight");
        armMotor = hardwareMap.get(DcMotor.class,"armMotor");
        claw1 = hardwareMap.get(Servo.class,"claw1");
        claw2 = hardwareMap.get(Servo.class,"claw2");
        driver = new GamepadEx(gamepad1);
    }

    @Override
    public void loop() {
        if (driver.getLeftX() < 0){
            frontLeft.setPower(-1);
            frontRight.setPower(1);
        }
        if (driver.getLeftX() > 0){
            frontLeft.setPower(1);
            frontRight.setPower(-1);
        }
    }

    @Override
    public void stop() {
    }
}
