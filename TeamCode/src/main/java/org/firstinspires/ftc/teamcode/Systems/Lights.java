package org.firstinspires.ftc.teamcode.Systems;

import com.qualcomm.hardware.rev.RevBlinkinLedDriver;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class Lights {
    Servo RGB_Indicator;

    public Lights(HardwareMap hardwareMap){ //Run this in Int to map the class items
        RGB_Indicator = hardwareMap.get(Servo.class, "LED");
    }

    public void Light_Red(){
        RGB_Indicator.setPosition(0.5);
    }

    public void Light_Green(){
        RGB_Indicator.setPosition(0.75);
    }

    public void Light_Blue(){
        RGB_Indicator.setPosition(0.25);
    }

    public void Light_Off(){
        RGB_Indicator.setPosition(0);
    }
}