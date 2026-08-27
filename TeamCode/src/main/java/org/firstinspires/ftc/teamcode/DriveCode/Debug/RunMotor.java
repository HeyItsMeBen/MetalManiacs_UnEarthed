package org.firstinspires.ftc.teamcode.DriveCode.Debug;

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

@TeleOp(name = "Run Motor", group = "Debug")
public class RunMotor extends OpMode {

    public GamepadEx driver;
    DcMotor motor;

    double power = 0;


    @Override
    public void init() {

        driver = new GamepadEx(gamepad1);

        motor = hardwareMap.get(DcMotor.class, "linearSlides");

        telemetry.addData("Status", "Initialized");
        telemetry.update();
    }

    @Override
    public void loop() {
        driver.readButtons();

        power = driver.getLeftY();

        motor.setPower(power);

    }

    @Override
    public void stop() {
        motor.setPower(0);
    }
}
