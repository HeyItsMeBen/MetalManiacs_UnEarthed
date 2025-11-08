package org.firstinspires.ftc.teamcode.Systems;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
@Deprecated
public class Hinge {

    Arm arm;
    private Servo hinge = null;
    public float firePosition=0.44f;
    public float holdPosition=0.29f;
    //Outtake subsystem
    public Hinge(HardwareMap hMap) {
        hinge = hMap.get(Servo.class, "hinge");
        arm = new Arm(hMap);
    }

    public void liftHinge(float position) {hinge.setPosition(position);}
}