package org.firstinspires.ftc.teamcode.DriveCode.TestFiles;

import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.arcrobotics.ftclib.gamepad.GamepadKeys;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Controllers.RumbleController;

@Disabled
@TeleOp(name = "[Immersive] Rumble Test", group = "Robot")
public class RumbleTest extends OpMode {
    public GamepadEx driver;
    public RumbleController rumbleController;
    public boolean intakeOn = false;

    // For continuous rumble timing
    private long lastRumbleTime = 0;
    private int rumbleInterval = 150;  // Increased interval so pulses are more distinct

    @Override
    public void init(){

        driver = new GamepadEx(gamepad1);
        rumbleController = new RumbleController(gamepad1);
    }

    @Override
    public void loop(){
        // CRITICAL: readButtons() FIRST!
        driver.readButtons();

        // Toggle intake
        if (driver.wasJustPressed(GamepadKeys.Button.RIGHT_BUMPER)){
            intakeOn = !intakeOn;
        }

        // Continuous rumble while intake is on - STRONGER
        if (intakeOn){
            rumbleController.continuousRumble();
        } else {
            rumbleController.stopContinuosRumbling();
        }

        // HEAVY RECOIL KICK - Multi-stage for maximum impact
        if (driver.wasJustPressed(GamepadKeys.Button.B)){
            rumbleController.ballCollected();
        }

        if (driver.wasJustPressed(GamepadKeys.Button.Y)){
            rumbleController.lightSnap();
        }

        if (driver.wasJustPressed(GamepadKeys.Button.X)){
            rumbleController.ballLaunched();
        }

        // Telemetry for debugging
        telemetry.addData("Intake On", intakeOn);
        telemetry.update();
    }
}