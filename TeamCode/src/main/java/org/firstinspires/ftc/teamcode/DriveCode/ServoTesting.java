package org.firstinspires.ftc.teamcode.DriveCode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;


@TeleOp (name="Servo Testing", group="test")
public class ServoTesting extends LinearOpMode {

    // Driver Code: Variables

    //If the arm has been moved upwards into the release area of the intake, it will open narrow. This is to prevent collision with the linear slides
    //If the arm has been moved downwards onto the ground, it will open wide. This way, there is more room to pick the sample up
    @Override
    public void runOpMode() {

        Servo servo = hardwareMap.get(Servo.class, "ServoArm");

        waitForStart();

        //executing
        while (opModeIsActive()) {
            if (gamepad1.dpad_up) {
                servo.setPosition(1);
            }
            else if (gamepad1.dpad_down) {
                servo.setPosition(0);
            }
            idle();
        }
    }
}
