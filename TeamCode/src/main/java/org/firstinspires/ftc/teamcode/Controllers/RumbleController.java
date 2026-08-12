package org.firstinspires.ftc.teamcode.Controllers;

import com.qualcomm.robotcore.hardware.Gamepad;
public class RumbleController {
    private Gamepad gamepad;
    private long lastRumbleTime = 0;

    // Configurable rumble patterns
    private double continuousIntensity = 0.015;  // 1.5% power for continuous rumble
    private int continuousInterval = 150;  // ms between pulses
    private int continuousDuration = 100;  // ms per pulse
    private boolean continuosRumble = false;


    public RumbleController(Gamepad gamepad) {
        this.gamepad = gamepad;
    }

    // ========== CONTINUOUS RUMBLE ==========
    public void continuousRumble() {
        if (!continuosRumble){
            continuosRumble = true;
        }

        long currentTime = System.currentTimeMillis();
        if (currentTime - lastRumbleTime > continuousInterval) {
            gamepad.rumble(continuousIntensity, continuousIntensity, continuousDuration);
            lastRumbleTime = currentTime;
        }
    }

    public void stopContinuosRumbling() {
        if (continuosRumble) {
            gamepad.stopRumble();
            continuosRumble = false;
        }
    }


    public void setContinuousIntensity(double intensity) {
        this.continuousIntensity = Math.max(0.0, Math.min(1.0, intensity));
    }

    public void setContinuousInterval(int intervalMs) {
        this.continuousInterval = intervalMs;
    }

    public void stop() {
        gamepad.stopRumble();
    }

    // ========== PRESET RUMBLE PATTERNS ==========

    public void lightSnap() {
        gamepad.rumble(0.8, 0.8, 60);
    }

    public void ballLaunched() { // Ball launched
        gamepad.rumble(1.0, 1.0, 100);
    }

    public void ballCollected() { // Ball collected
        gamepad.rumble(1.0, 1.0, 150);
    }

    public void demoRumble() {
        gamepad.rumble(1.0, 1.0, 5000);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Restore interrupted status
        }

        gamepad.rumble(1.0, 1.0, 5000);
    }

    public void demoRumble2() {
        gamepad.rumble(1.0,1.0, 6000);
    }


}
