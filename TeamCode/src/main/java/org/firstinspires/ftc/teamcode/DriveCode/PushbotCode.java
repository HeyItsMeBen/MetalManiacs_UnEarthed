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
public class PushbotCode extends OpMode {

    public GamepadEx driver;
    List<LynxModule> allHubs;

    // Hardware - expansion hub motor port 0/1/2, servo port 0/1
    DcMotor leftDrive;
    DcMotor rightDrive;
    DcMotor armMotor;
    Servo leftHand;
    Servo rightHand;

    double driveSpeed = 1.0;

    public static final double HAND_OPEN = 1.0;
    public static final double HAND_CLOSED = 0.2;

    @Override
    public void init() {

        driver = new GamepadEx(gamepad1);

        leftDrive = hardwareMap.get(DcMotor.class, "left_drive");
        rightDrive = hardwareMap.get(DcMotor.class, "right_drive");
        armMotor = hardwareMap.get(DcMotor.class, "arm_motor");
        leftHand = hardwareMap.get(Servo.class, "left_hand");
        rightHand = hardwareMap.get(Servo.class, "right_hand");

        rightDrive.setDirection(DcMotorSimple.Direction.REVERSE);

        leftHand.setPosition(HAND_CLOSED);
        rightHand.setPosition(HAND_CLOSED);

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        allHubs = hardwareMap.getAll(LynxModule.class);
        for (LynxModule hub : allHubs) {
            hub.setBulkCachingMode(LynxModule.BulkCachingMode.MANUAL);
        }
    }

    @Override
    public void loop() {
        driver.readButtons();

        // Drive speed adjust
        if (driver.wasJustPressed(GamepadKeys.Button.DPAD_UP)) {
            driveSpeed = Math.min(1.0, driveSpeed + 0.1);
        } else if (driver.wasJustPressed(GamepadKeys.Button.DPAD_DOWN)) {
            driveSpeed = Math.max(0.1, driveSpeed - 0.1);
        }

        // Drive
        double forward = -driver.getLeftY();
        double turn = driver.getRightX();

        double leftPower = Range.clip(forward + turn, -1.0, 1.0) * driveSpeed;
        double rightPower = Range.clip(forward - turn, -1.0, 1.0) * driveSpeed;

        leftDrive.setPower(leftPower);
        rightDrive.setPower(rightPower);

        // Arm
        if (driver.getButton(GamepadKeys.Button.RIGHT_BUMPER)) {
            armMotor.setPower(1.0);
        } else if (driver.getButton(GamepadKeys.Button.LEFT_BUMPER)) {
            armMotor.setPower(-1.0);
        } else {
            armMotor.setPower(0.0);
        }

        // Hand / Gripper
        if (driver.wasJustPressed(GamepadKeys.Button.A)) {
            leftHand.setPosition(HAND_OPEN);
            rightHand.setPosition(HAND_OPEN);
        } else if (driver.wasJustPressed(GamepadKeys.Button.B)) {
            leftHand.setPosition(HAND_CLOSED);
            rightHand.setPosition(HAND_CLOSED);
        }

        // Displays important information for driver
        telemetry.addData("Drive Speed", driveSpeed);
        telemetry.addData("Left Power", leftPower);
        telemetry.addData("Right Power", rightPower);
        telemetry.update();

        for (LynxModule hub : allHubs) {
            hub.clearBulkCache();
        }
    }

    @Override
    public void stop() {
        leftDrive.setPower(0);
        rightDrive.setPower(0);
        armMotor.setPower(0);
    }
}
