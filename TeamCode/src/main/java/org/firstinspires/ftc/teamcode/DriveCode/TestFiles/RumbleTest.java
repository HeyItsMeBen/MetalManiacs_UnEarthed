package org.firstinspires.ftc.teamcode.DriveCode.TestFiles;

import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.arcrobotics.ftclib.gamepad.GamepadKeys;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@Disabled
@TeleOp(name = "[Immersive] Rumble Test", group = "Robot")
public class RumbleTest extends OpMode {
    public GamepadEx driver;
    public boolean intakeOn = false;

    // For continuous rumble timing
    private long lastRumbleTime = 0;
    private int rumbleInterval = 150;  // Increased interval so pulses are more distinct

    @Override
    public void init(){
        driver = new GamepadEx(gamepad1);
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
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastRumbleTime > rumbleInterval) {
                gamepad1.rumble(0.01, 0.01, 100);  // Increased from 0.05 to 0.5
                lastRumbleTime = currentTime;
            }
        } else {
            gamepad1.stopRumble();
        }

        // HEAVY RECOIL KICK - Multi-stage for maximum impact
        if (driver.wasJustPressed(GamepadKeys.Button.B)){
            // Initial HARD hit
            gamepad1.rumble(1.0, 1.0, 200);

            // Schedule a second impact for extra "oomph"
            new Thread(() -> {
                try {
                    Thread.sleep(220);  // Slight delay after first rumble
                    gamepad1.rumble(0.8, 0.8, 150);  // Second punch
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }).start();
        }

        // Telemetry for debugging
        telemetry.addData("Intake On", intakeOn);
        telemetry.update();
    }
}