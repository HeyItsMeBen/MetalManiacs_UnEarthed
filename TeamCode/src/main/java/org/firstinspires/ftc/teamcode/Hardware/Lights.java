package org.firstinspires.ftc.teamcode.Hardware;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Prism.Color;
import org.firstinspires.ftc.teamcode.Prism.GoBildaPrismDriver;
import org.firstinspires.ftc.teamcode.Prism.PrismAnimations;

public class Lights {
    GoBildaPrismDriver prism;
    private static final int LEDS_PER_STRIP = 12;
    private static final int NUM_STRIPS = 2;
    private static final int TOTAL_LEDS = LEDS_PER_STRIP * NUM_STRIPS;

    private ElapsedTime debounceTimer = new ElapsedTime();
    private static final double DEBOUNCE_TIME = 0.2; // 200ms between commands

    private String lastSequence = ""; // Track what's currently displayed
    private boolean isInitialized = false;

    public Lights(HardwareMap hardwareMap){
        try {
            prism = hardwareMap.get(GoBildaPrismDriver.class, "prism");
            prism.setStripLength(TOTAL_LEDS);
            isInitialized = true;
            debounceTimer.reset();
        } catch (Exception e) {
            isInitialized = false;
        }
    }

    private boolean canUpdate() {
        return isInitialized && debounceTimer.seconds() >= DEBOUNCE_TIME;
    }

//    public void Light_Green(){
//        if (!canUpdate() || lastSequence.equals("GREEN")) return;
//
//        try {
//            // Instead of clearing, just overwrite layer 0 with full strip
//            PrismAnimations.Solid green = new PrismAnimations.Solid(Color.GREEN, 0, TOTAL_LEDS - 1);
//            prism.insertAndUpdateAnimation(GoBildaPrismDriver.LayerHeight.LAYER_0, green);
//
//            lastSequence = "GREEN";
//            debounceTimer.reset();
//        } catch (Exception e) {
//            isInitialized = false; // Mark as broken
//        }
//    }

    public void Light_Red(){
        PrismAnimations.Solid red = new PrismAnimations.Solid(Color.RED);
        prism.insertAndUpdateAnimation(GoBildaPrismDriver.LayerHeight.LAYER_0, red);
    }
    public void Light_Green(){
        PrismAnimations.Solid green = new PrismAnimations.Solid(Color.GREEN);
        prism.insertAndUpdateAnimation(GoBildaPrismDriver.LayerHeight.LAYER_0, green);
    }
    public void Light_Purple(){
        if (!canUpdate() || lastSequence.equals("PURPLE")) return;

        try {
            PrismAnimations.Solid purple = new PrismAnimations.Solid(Color.PURPLE, 0, TOTAL_LEDS - 1);
            prism.insertAndUpdateAnimation(GoBildaPrismDriver.LayerHeight.LAYER_0, purple);

            lastSequence = "PURPLE";
            debounceTimer.reset();
        } catch (Exception e) {
            isInitialized = false;
        }
    }

    public void Light_Sequence(String sequence){
        if (!canUpdate() || sequence.equals(lastSequence)) return; // Skip if already showing

        try {
            int oneThird = LEDS_PER_STRIP / 3;
            int layerIndex = 0;

            // Process all sections across all strips
            for (int strip = 0; strip < NUM_STRIPS; strip++) {
                int offset = strip * LEDS_PER_STRIP;

                for (int section = 0; section < sequence.length(); section++) {
                    // Safety check - only use first 10 layers
                    if (layerIndex >= 10) break;

                    int startLED = section * oneThird + offset;
                    int endLED = (section + 1) * oneThird - 1 + offset;

                    Color color;
                    char c = sequence.charAt(section);

                    if (c == 'G') {
                        color = Color.GREEN;
                    } else if (c == 'P') {
                        color = Color.PURPLE;
                    } else {
                        color = Color.TRANSPARENT;
                    }

                    // Create and insert animation
                    PrismAnimations.Solid animation = new PrismAnimations.Solid(color, startLED, endLED);

                    // Use specific layer based on index
                    GoBildaPrismDriver.LayerHeight layer = getLayer(layerIndex);
                    if (layer != null) {
                        prism.insertAndUpdateAnimation(layer, animation);
                    }

                    layerIndex++;
                }
            }

            // Clear any unused layers (turn off any sections we're not using)
            for (int i = layerIndex; i < 10; i++) {
                GoBildaPrismDriver.LayerHeight layer = getLayer(i);
                if (layer != null) {
                    PrismAnimations.Solid off = new PrismAnimations.Solid(Color.TRANSPARENT, 0, 0);
                    prism.insertAndUpdateAnimation(layer, off);
                }
            }

            lastSequence = sequence;
            debounceTimer.reset();

        } catch (Exception e) {
            isInitialized = false;
        }
    }

    // Helper method to safely get a layer
    private GoBildaPrismDriver.LayerHeight getLayer(int index) {
        switch(index) {
            case 0: return GoBildaPrismDriver.LayerHeight.LAYER_0;
            case 1: return GoBildaPrismDriver.LayerHeight.LAYER_1;
            case 2: return GoBildaPrismDriver.LayerHeight.LAYER_2;
            case 3: return GoBildaPrismDriver.LayerHeight.LAYER_3;
            case 4: return GoBildaPrismDriver.LayerHeight.LAYER_4;
            case 5: return GoBildaPrismDriver.LayerHeight.LAYER_5;
            case 6: return GoBildaPrismDriver.LayerHeight.LAYER_6;
            case 7: return GoBildaPrismDriver.LayerHeight.LAYER_7;
            case 8: return GoBildaPrismDriver.LayerHeight.LAYER_8;
            case 9: return GoBildaPrismDriver.LayerHeight.LAYER_9;
            default: return null;
        }
    }

    public void Light_Off(){
        if (!canUpdate() || lastSequence.equals("OFF")) return;

        try {
            // Set all layers to transparent instead of clearing
            for (int i = 0; i < 10; i++) {
                GoBildaPrismDriver.LayerHeight layer = getLayer(i);
                if (layer != null) {
                    PrismAnimations.Solid off = new PrismAnimations.Solid(Color.TRANSPARENT, 0, 0);
                    prism.insertAndUpdateAnimation(layer, off);
                }
            }

            lastSequence = "OFF";
            debounceTimer.reset();
        } catch (Exception e) {
            isInitialized = false;
        }
    }

    // Add this method to check if lights are working
    public boolean isWorking() {
        return isInitialized;
    }
}