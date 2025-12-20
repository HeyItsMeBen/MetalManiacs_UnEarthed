package org.firstinspires.ftc.teamcode.DriveCode.Test_Files;

import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.arcrobotics.ftclib.gamepad.GamepadKeys;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.Hardware.Lights;

@Disabled
@TeleOp (name="Test LED Lights", group="test")
public class LED_Manual_Test extends LinearOpMode {

    public GamepadEx gamepad;
    Lights lights;

    public Servo Light;

    @Override
    public void runOpMode() {


        gamepad = new GamepadEx(gamepad1);
        lights = new Lights(hardwareMap);

        telemetry.addData("Red Light: ", "A");
        telemetry.addData("Green Light: ", "B");
        telemetry.addData("Blue Light: ", "X");
        telemetry.addData("Off: ", "Y");
        telemetry.update();

        waitForStart();

        //executing
        while (opModeIsActive()) {
            if (gamepad.getButton(GamepadKeys.Button.A)) {
                lights.Light_Red();
            }
            else if (gamepad.getButton(GamepadKeys.Button.B)) {
                lights.Light_Green();
            }
            else if (gamepad.getButton(GamepadKeys.Button.X)) {
                lights.Light_Off();
            }
            else if (gamepad.getButton(GamepadKeys.Button.Y)) {
                lights.Light_Off();
            }
        }

    }
}
