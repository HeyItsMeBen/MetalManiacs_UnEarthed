package org.firstinspires.ftc.teamcode.Hardware;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.Prism.Color;
import org.firstinspires.ftc.teamcode.Prism.GoBildaPrismDriver;
import org.firstinspires.ftc.teamcode.Prism.PrismAnimations;

public class Lights {
    GoBildaPrismDriver prism;
    private String lastSequence = "";

    // LED layout (60 total):
    // [0-5]   Team color A   (6 LEDs)
    // [6-29]  Intake status  (24 LEDs)
    // [30-35] Team color B   (6 LEDs)
    // [36-47] Ball sequence  (12 LEDs, strip is physically reversed)
    // [48-59] April tag      (12 LEDs)

    private static final int TEAM_A_START  = 0;
    private static final int TEAM_A_END    = 5;
    private static final int INTAKE_START  = 6;
    private static final int INTAKE_END    = 29;
    private static final int TEAM_B_START  = 30;
    private static final int TEAM_B_END    = 35;
    private static final int BALL_START    = 36;
    private static final int BALL_END      = 47;
    private static final int APRIL_START   = 48;
    private static final int APRIL_END     = 59;

    public Lights(HardwareMap hardwareMap) {
        prism = hardwareMap.get(GoBildaPrismDriver.class, "prism");
        prism.setStripLength(60);
    }

    public void updateLights(String teamColor, boolean intake, String ballSequence, boolean target) {
        // Build a state key to skip redundant updates
        String stateKey = teamColor + intake + ballSequence + target;
        if (stateKey.equals(lastSequence)) return;
        lastSequence = stateKey;

        // --- Team color (sections A and B) ---
        Color teamColorDisplay = teamColor.equalsIgnoreCase("blue") ? Color.BLUE : Color.RED;

        prism.insertAndUpdateAnimation(GoBildaPrismDriver.LayerHeight.LAYER_0,
                new PrismAnimations.Solid(teamColorDisplay, TEAM_A_START, TEAM_A_END));

        prism.insertAndUpdateAnimation(GoBildaPrismDriver.LayerHeight.LAYER_1,
                new PrismAnimations.Solid(teamColorDisplay, TEAM_B_START, TEAM_B_END));

        // --- Intake status ---
        Color intakeColor = intake ? Color.YELLOW : Color.BLUE;

        prism.insertAndUpdateAnimation(GoBildaPrismDriver.LayerHeight.LAYER_2,
                new PrismAnimations.Solid(intakeColor, INTAKE_START, INTAKE_END));

        // --- Ball sequence (reversed because strip is physically backwards) ---
        // Each character = 4 LEDs (12 LEDs / 3 chars)
        String reversed = new StringBuilder(ballSequence.toUpperCase()).reverse().toString();
        int ledsPerChar = 4; // 12 LEDs / 3 characters

        GoBildaPrismDriver.LayerHeight[] ballLayers = {
                GoBildaPrismDriver.LayerHeight.LAYER_3,
                GoBildaPrismDriver.LayerHeight.LAYER_4,
                GoBildaPrismDriver.LayerHeight.LAYER_5
        };

        for (int i = 0; i < reversed.length(); i++) {
            char c = reversed.charAt(i);
            Color ballColor;
            if (c == 'G') {
                ballColor = Color.GREEN;
            } else if (c == 'P') {
                ballColor = Color.PURPLE;
            } else {
                ballColor = Color.TRANSPARENT; // X = empty slot
            }
            int start = BALL_START + (i * ledsPerChar);
            int end = start + ledsPerChar - 1;
            prism.insertAndUpdateAnimation(ballLayers[i],
                    new PrismAnimations.Solid(ballColor, start, end));
        }

        // --- April tag status ---
        Color aprilColor = target ? Color.GREEN : Color.RED;

        prism.insertAndUpdateAnimation(GoBildaPrismDriver.LayerHeight.LAYER_6,
                new PrismAnimations.Solid(aprilColor, APRIL_START, APRIL_END));
    }

    public void Light_Off() {
        lastSequence = ""; // Reset so next updateLights() call forces a fresh update
        for (int i = 0; i < 7; i++) {
            prism.insertAndUpdateAnimation(getLayer(i),
                    new PrismAnimations.Solid(Color.TRANSPARENT, 0, 59));
        }
    }
    public void Light_Red() {
        lastSequence = ""; // Reset so next updateLights() call forces a fresh update
        for (int i = 0; i < 7; i++) {
            prism.insertAndUpdateAnimation(getLayer(i),
                    new PrismAnimations.Solid(Color.RED, 0, 59));
        }
    }

    public void Light_Green() {
        lastSequence = ""; // Reset so next updateLights() call forces a fresh update
        for (int i = 0; i < 7; i++) {
            prism.insertAndUpdateAnimation(getLayer(i),
                    new PrismAnimations.Solid(Color.GREEN, 0, 59));
        }
    }
    private GoBildaPrismDriver.LayerHeight getLayer(int index) {
        switch (index) {
            case 0: return GoBildaPrismDriver.LayerHeight.LAYER_0;
            case 1: return GoBildaPrismDriver.LayerHeight.LAYER_1;
            case 2: return GoBildaPrismDriver.LayerHeight.LAYER_2;
            case 3: return GoBildaPrismDriver.LayerHeight.LAYER_3;
            case 4: return GoBildaPrismDriver.LayerHeight.LAYER_4;
            case 5: return GoBildaPrismDriver.LayerHeight.LAYER_5;
            case 6: return GoBildaPrismDriver.LayerHeight.LAYER_6;
            default: return null;
        }
    }
}
