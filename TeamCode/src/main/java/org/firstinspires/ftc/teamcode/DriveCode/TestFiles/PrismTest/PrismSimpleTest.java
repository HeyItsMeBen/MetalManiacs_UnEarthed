package org.firstinspires.ftc.teamcode.DriveCode.TestFiles.PrismTest;

import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.arcrobotics.ftclib.gamepad.GamepadKeys;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Hardware.Lights;
import org.firstinspires.ftc.teamcode.Prism.GoBildaPrismDriver;

@Disabled
@TeleOp(name = "Prism Simple Test", group = "Test")
public class PrismSimpleTest extends OpMode {

    GoBildaPrismDriver prism;
    Lights lights;

    public GamepadEx driver;
    public GamepadEx operator;

    @Override
    public void init() {
        // Initialize the Prism driver
        lights = new Lights(hardwareMap);

        driver = new GamepadEx(gamepad1);
        operator = new GamepadEx(gamepad2);

        telemetry.addData("Status", "Prism Initialized");


        // Clear any existing animations
        lights.Light_Off();
    }

    @Override
    public void loop() {
        driver.readButtons();
        operator.readButtons();

        if(driver.wasJustPressed((GamepadKeys.Button.DPAD_LEFT))){
            lights.Light_Sequence("XGP");
//            lights.Light_Green();
        }

        if(driver.wasJustPressed((GamepadKeys.Button.DPAD_RIGHT))){
            lights.Light_Sequence("PGX");
//            lights.Light_Purple();
        }
        if(driver.wasJustPressed((GamepadKeys.Button.DPAD_DOWN))){
            telemetry.addData("DPAD", "DOWN PRESSED!");

            lights.Light_Off();
        }

        if(driver.wasJustPressed((GamepadKeys.Button.DPAD_UP))){
            telemetry.addData("DPAD", "UP PRESSED!");
//            lights.Light_Sequence("PXX");
        }
        telemetry.update();
    }
}