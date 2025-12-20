package org.firstinspires.ftc.teamcode.Hardware;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.Prism.Color;
import org.firstinspires.ftc.teamcode.Prism.GoBildaPrismDriver;
import org.firstinspires.ftc.teamcode.Prism.PrismAnimations;

public class Lights {
    GoBildaPrismDriver prism;
    private static final int LEDS_PER_STRIP = 6;  // LEDs on each physical strip
    private static final int NUM_STRIPS = 2;       // Number of strips connected
    private static final int TOTAL_LEDS = LEDS_PER_STRIP * NUM_STRIPS;

    public Lights(HardwareMap hardwareMap){ //Run this in Init to map the class items
        prism = hardwareMap.get(GoBildaPrismDriver.class, "prism");
        prism.setStripLength(TOTAL_LEDS);
    }

    public void Light_Sequence(String sequence){
        //lights up the leds based on the sequence
        //eg. PPG will light up 2/3 purple and 1/3 green
        prism.clearAllAnimations();

        int oneThird = LEDS_PER_STRIP / 3; //calculate one third of a led strip

        for (int strip = 0; strip < NUM_STRIPS; strip++) { //apply animation for each strip
            int offset = strip * LEDS_PER_STRIP;

            for (int section = 0; section < sequence.length(); section++) {
                int startLED = section * oneThird + offset;
                int endLED = (section + 1) * oneThird - 1 + offset;

                int layerIndex = strip * sequence.length() + section;

                if (sequence.charAt(section) == 'G') {
                    PrismAnimations.Solid green = new PrismAnimations.Solid(
                            Color.GREEN,
                            startLED,
                            endLED
                    );//sets green to green basically
                    prism.insertAndUpdateAnimation(
                            GoBildaPrismDriver.LayerHeight.values()[layerIndex],
                            green
                    );//apply the color
                } else if (sequence.charAt(section) == 'P') {
                    PrismAnimations.Solid purple = new PrismAnimations.Solid(
                            Color.PURPLE,
                            startLED,
                            endLED
                    );//sets purple to purple basically
                    prism.insertAndUpdateAnimation(
                            GoBildaPrismDriver.LayerHeight.values()[layerIndex],
                            purple
                    );//apply the color
                }
            }
        }
    }
    public void Light_Off(){
        prism.clearAllAnimations();
    }
}