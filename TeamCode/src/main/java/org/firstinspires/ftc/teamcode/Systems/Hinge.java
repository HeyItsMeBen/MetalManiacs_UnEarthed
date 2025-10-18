package org.firstinspires.ftc.teamcode.Systems;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

@Config

public class Hinge {

    public Servo hinge = null;
    //Outtake subsystem
    public Hinge(HardwareMap hMap) {
        hinge = hMap.get(Servo.class, "hinge");
    }

    public void liftHinge(float position) {
        hinge.setPosition(position);
    }
}