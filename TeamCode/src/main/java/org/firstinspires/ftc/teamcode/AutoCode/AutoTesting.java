package org.firstinspires.ftc.teamcode.AutoCode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

//basic imports like motors and opModes
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;


//sets mode to autonomous and makes the main class

@Autonomous(name = "Straight Line Test", group = "Linear OpMode")
public class AutoTesting extends LinearOpMode {
    //defining variables
    public conveyerBelt belt;
    @Override
    //This runs when the program is activated
    public void runOpMode() {
        belt=hardwareMap.get(conveyerBelt.class, "conveyerBelt");
        while (opModeIsActive()){

        }
    }
    public void intakeBall(){
        belt.setMotorPower(0);
        belt.setMotorPower(0.25);
        sleep(500);
        belt.setMotorPower(0);
    }
    public void outtakeBall(){
        belt.setMotorPower(0);
        belt.setMotorPower(0.25);
        sleep(500);
        belt.setMotorPower(0);
    }
}
