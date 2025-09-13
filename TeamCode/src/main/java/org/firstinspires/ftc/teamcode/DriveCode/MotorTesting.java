package org.firstinspires.ftc.teamcode.DriveCode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.Servo;
import org.firstinspires.ftc.ftccommon.internal.manualcontrol.parameters.MotorAlertLevelParameters;

@Disabled
@TeleOp (name="Motor Testing", group="test")
public class MotorTesting extends LinearOpMode {

    // Driver Code: Variables

    //If the arm has been moved upwards into the release area of the intake, it will open narrow. This is to prevent collision with the linear slides
    //If the arm has been moved downwards onto the ground, it will open wide. This way, there is more room to pick the sample up
    @Override
    public void runOpMode() {

        DcMotor Motor = hardwareMap.get(DcMotor.class, "Motor");

        waitForStart();

        //executing
        while (opModeIsActive()) {
            if (gamepad1.left_stick_y > 0) {

                Motor.setPower(1);
            }
            Motor.setPower(0);

            idle();
        }
    }
}
