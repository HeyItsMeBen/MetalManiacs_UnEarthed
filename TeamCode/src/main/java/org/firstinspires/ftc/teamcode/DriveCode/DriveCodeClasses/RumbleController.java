package org.firstinspires.ftc.teamcode.DriveCode.DriveCodeClasses;

import com.qualcomm.robotcore.hardware.Gamepad;
public class RumbleController {
    private Gamepad gamepad;
    private long lastRumbleTime = 0;

    // Configurable rumble patterns
    private double continuousIntensity = 0.5;
    private int continuousInterval = 150;  // ms between pulses
    private int continuousDuration = 100;  // ms per pulse

    public RumbleController(Gamepad gamepad) {
        this.gamepad = gamepad;
    }

    // ========== CONTINUOUS RUMBLE ==========
    public void continuousRumble() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastRumbleTime > continuousInterval) {
            gamepad.rumble(continuousIntensity, continuousIntensity, continuousDuration);
            lastRumbleTime = currentTime;
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

    public void mediumPunch() {
        gamepad.rumble(1.0, 1.0, 100);
    }

    public void heavyKick() {
        gamepad.rumble(1.0, 1.0, 200);
    }
}
