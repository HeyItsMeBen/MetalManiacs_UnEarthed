package org.firstinspires.ftc.teamcode.DriveCode.TestFiles.PrismTest;

import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.arcrobotics.ftclib.gamepad.GamepadKeys;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Controllers.LightsController;
import org.firstinspires.ftc.teamcode.Hardware.Lights;
import org.firstinspires.ftc.teamcode.Prism.GoBildaPrismDriver;

@Disabled
@TeleOp(name = "Prism Simple Test", group = "Test")
public class PrismSimpleTest extends OpMode {

    GoBildaPrismDriver prism;
    Lights lights;
    LightsController lightsController;


    public GamepadEx driver;
    public GamepadEx operator;
    public String teamColor = "Red";

    public boolean intake = false;
    public boolean target = false;

    public String ballSequence = "XXX";


    @Override
    public void init() {
        // Initialize the Prism driver
        lights = new Lights(hardwareMap);

        driver = new GamepadEx(gamepad1);

        telemetry.addData("Status", "Prism Initialized");


        // Clear any existing animations
        lights.Light_Off();
    }

    @Override
    public void loop() {
        driver.readButtons();
        operator.readButtons();

        if(driver.wasJustPressed((GamepadKeys.Button.DPAD_LEFT))){
            ballSequence = "GXX";
        }

        if(driver.wasJustPressed((GamepadKeys.Button.DPAD_RIGHT))){
            ballSequence = "GGG";
            //            lights.Light_Purple();
        }
        if(driver.wasJustPressed((GamepadKeys.Button.DPAD_DOWN))){

            lightsController.turnOff();
        }

        if(driver.wasJustPressed((GamepadKeys.Button.DPAD_UP))){
            ballSequence = "PPX";
        }

        lightsController.update(
                target,
                intake,
                teamColor,
                ballSequence
        );

        telemetry.update();
    }
}