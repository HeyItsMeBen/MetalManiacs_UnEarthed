package org.firstinspires.ftc.teamcode.DriveCode;
import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.hardwareMap;

import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.arcrobotics.ftclib.gamepad.GamepadKeys;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

@Config

public class Outtake{

    public DcMotor rightFlyWheel = null;
    public DcMotor leftFlyWheel = null;
    //Outtake subsystem
    public Outtake() {
        rightFlyWheel = hardwareMap.get(DcMotor.class, "rightFlyWheel");
        leftFlyWheel = hardwareMap.get(DcMotor.class, "leftFlyWheel");

        rightFlyWheel.setDirection(DcMotor.Direction.FORWARD);
        leftFlyWheel.setDirection(DcMotor.Direction.FORWARD);
    }

    public void fire(float power) {


    }
}