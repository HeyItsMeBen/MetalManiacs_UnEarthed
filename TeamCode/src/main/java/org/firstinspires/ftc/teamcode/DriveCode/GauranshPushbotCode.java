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

import org.firstinspires.ftc.teamcode.Hardware.PushbotArm;

import java.util.List;

@TeleOp(name = "GauranshPushbotCode", group = "A - TeleOP")
public class GauranshPushbotCode extends OpMode {

    public PushbotArm armTuning;
    public GamepadEx driver;
    List<LynxModule> allHubs;

    // Hardware - expansion hub motor port 0/1/2, servo port 0/1
    DcMotor leftDrive;
    DcMotor rightDrive;
    DcMotor armMotor;
    Servo leftClaw;
    Servo rightClaw;

    double driveSpeed = 1.0;

    public static final double HAND_OPEN = 0;
    public static final double HAND_CLOSED = 0.2;

    public static final double LEFT_HAND_OPEN = 0.2;
    public static final double LEFT_HAND_CLOSED = 0;

    public static final double ARM_MANUAL_POWER = 0.8;
    public static final double ARM_GRAVITY_POWER = 0.001;

    @Override
    public void init() {

        driver = new GamepadEx(gamepad1);

        leftDrive = hardwareMap.get(DcMotor.class, "leftDrive");
        rightDrive = hardwareMap.get(DcMotor.class, "rightDrive");
        armMotor = hardwareMap.get(DcMotor.class, "armMotor");
        leftClaw = hardwareMap.get(Servo.class, "leftClaw");
        rightClaw = hardwareMap.get(Servo.class, "rightClaw");

        armTuning = new PushbotArm(hardwareMap);

        leftDrive.setDirection(DcMotorSimple.Direction.REVERSE);

        //leftClaw.setPosition(LEFT_HAND_CLOSED);
        //rightClaw.setPosition(HAND_CLOSED);

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

        double leftPower = Range.clip(forward - turn, -1.0, 1.0) * driveSpeed;
        double rightPower = Range.clip(forward + turn, -1.0, 1.0) * driveSpeed;

        leftDrive.setPower(leftPower);
        rightDrive.setPower(rightPower);

        // Arm
        int currentArmPos = armMotor.getCurrentPosition();
        if (driver.getButton(GamepadKeys.Button.RIGHT_BUMPER)) {
            armTuning.targetPosition = Math.min(400,armTuning.targetPosition + 1);
        } else if (driver.getButton(GamepadKeys.Button.LEFT_BUMPER)) {
            armTuning.targetPosition = Math.max(0,armTuning.targetPosition -1);
        }

        // Hand / Gripper
//        if (driver.wasJustPressed(GamepadKeys.Button.A)) {
//            leftClaw.setPosition(LEFT_HAND_OPEN);
//            rightClaw.setPosition(HAND_OPEN);
//        } else if (driver.wasJustPressed(GamepadKeys.Button.B)) {
//            leftClaw.setPosition(LEFT_HAND_CLOSED);
//            rightCrlaw.setPosition(HAND_CLOSED);
//        }

        armTuning.update();

        // Displays important information for driver
        telemetry.addData("Drive Speed", driveSpeed);
        telemetry.addData("Left Power", leftPower);
        telemetry.addData("Right Power", rightPower);
        telemetry.addData("Arm Position", currentArmPos);
        telemetry.addData("Current Position", armTuning.getArmPosition());
        telemetry.addData("Target Position",armTuning.targetPosition);
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