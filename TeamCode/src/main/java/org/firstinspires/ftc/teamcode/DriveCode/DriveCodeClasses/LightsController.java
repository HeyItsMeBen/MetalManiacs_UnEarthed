package org.firstinspires.ftc.teamcode.DriveCode.DriveCodeClasses;

import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Hardware.Lights;

public class LightsController {

    private Lights lights;

    private ElapsedTime lightTimer = new ElapsedTime();

    private static final long UPDATE_INTERVAL_MS = 500;

    public LightsController(Lights lights) {
        this.lights = lights;
    }

    public void update(boolean targetVisible,
                       boolean intakeRunning,
                       String teamColor) {

        if (lightTimer.milliseconds() < UPDATE_INTERVAL_MS) {
            return;
        }

        lightTimer.reset();

        lights.updateStatus(
                targetVisible,
                intakeRunning,
                teamColor
        );
    }

    public void turnOff() {
        lights.Light_Off();
    }
}
