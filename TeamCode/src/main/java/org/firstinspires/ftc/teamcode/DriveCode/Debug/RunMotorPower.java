package org.firstinspires.ftc.teamcode.DriveCode.Debug;

import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

@TeleOp (name="Motor Debugger", group="Debug")
public class RunMotorPower extends LinearOpMode {

    public GamepadEx gamepad;

    public DcMotor Motor;

    @Override
    public void runOpMode() {

        gamepad = new GamepadEx(gamepad1);

        Motor = hardwareMap.get(DcMotor.class, "transferDrum");
        Motor.setDirection(DcMotor.Direction.FORWARD);

        waitForStart();
        //executing
        while (opModeIsActive()) {

            if (gamepad.getRightY() > 0){
                Motor.setPower(1);
            }

            else if (gamepad.getRightY() < 0) {
                Motor.setPower(-1);

            } else {
                Motor.setPower(0);
            }

            telemetry.addData("Debug File: ", "Applies continuous power to a motor or continuous servo");
            telemetry.addData("Use right joystick up/down to apply power", "");
            telemetry.update();

            idle();
        }
    }
}
