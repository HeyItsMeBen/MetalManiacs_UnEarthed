package org.firstinspires.ftc.teamcode.AutoCode;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
public class conveyerBelt {
    private CRServo conveyerBelt;

    public conveyerBelt(HardwareMap hMap) {
        conveyerBelt = hMap.get(CRServo.class, "conveyerBelt"); //added 7/24/24
    }
    public void setMotorPower(double dblPower){
        conveyerBelt.setPower(dblPower);
    }
}