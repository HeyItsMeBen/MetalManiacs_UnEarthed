package org.firstinspires.ftc.teamcode.Systems;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

@Config

public class Transfer {

    public DcMotor rightFlyWheel = null;
    public DcMotor leftFlyWheel = null;

    //Transfer subsystem
    //also for sorting if we eventually add it.
    public Transfer(HardwareMap hMap) {

    }

    public void fire(float power) {
        //eventually we need to detect if the flywheel is fully accelerated then fire

    }
}