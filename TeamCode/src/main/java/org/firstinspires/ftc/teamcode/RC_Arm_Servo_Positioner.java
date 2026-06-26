package org.firstinspires.ftc.teamcode;

import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.arcrobotics.ftclib.gamepad.GamepadKeys;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp(name = "RC Arm: Servo Positioner", group = "Robot")
public class RC_Arm_Servo_Positioner extends OpMode {

    public GamepadEx driver;

    public Servo servo;

    @Override
    public void init() {

        driver = new GamepadEx(gamepad1);

        servo = hardwareMap.get(Servo.class, "Lever");

        telemetry.addData("Status", "Initialized");
        telemetry.update();
    }

    @Override
    public void loop() {

        telemetry.addData("Current Servo Position: ", servo.getPosition());

        if (driver.isDown(GamepadKeys.Button.A)) {
            servo.setPosition(0);
        }

        else if (driver.isDown(GamepadKeys.Button.LEFT_BUMPER)) {
            double currentPosition = servo.getPosition();
            currentPosition++;
            servo.setPosition(currentPosition);
        }

        else if (driver.isDown(GamepadKeys.Button.RIGHT_BUMPER)) {
            double currentPosition = servo.getPosition();
            currentPosition--;
            servo.setPosition(currentPosition);
        }

        telemetry.update();

    }

}
