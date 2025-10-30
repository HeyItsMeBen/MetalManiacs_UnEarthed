package org.firstinspires.ftc.teamcode.Systems;

import com.qualcomm.hardware.rev.RevBlinkinLedDriver;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Lights {
    RevBlinkinLedDriver blinkinLedDriver;

    public Lights(HardwareMap hardwareMap){ //Run this in Int to map the class items
        blinkinLedDriver = hardwareMap.get(RevBlinkinLedDriver.class, "LED");
    }

    public void Light_Red(){
        blinkinLedDriver.setPattern(RevBlinkinLedDriver.BlinkinPattern.RED);
    }

    public void Light_Green(){
        blinkinLedDriver.setPattern(RevBlinkinLedDriver.BlinkinPattern.GREEN);
    }

    public void Light_Blue(){
        blinkinLedDriver.setPattern(RevBlinkinLedDriver.BlinkinPattern.BLUE);
    }

    public void Light_Off(){
        blinkinLedDriver.setPattern(RevBlinkinLedDriver.BlinkinPattern.BLACK);
    }
}