package org.firstinspires.ftc.teamcode.DriveCode.Debug;

import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;

@TeleOp (name="Flywheel Debugger", group="Debug")
public class RunFlywheelPower extends LinearOpMode {

    public GamepadEx gamepad;

    public DcMotorEx Motor;
    ElapsedTime rampTimer;
    @Override
    public void runOpMode() {

        gamepad = new GamepadEx(gamepad1);

        Motor = hardwareMap.get(DcMotorEx.class, "flywheel");
        Motor.setDirection(DcMotorEx.Direction.FORWARD);
        Motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        waitForStart();
        //executing
        while (opModeIsActive()) {
            while (opModeIsActive()) {
                Motor.setPower(rampTimer.seconds()/10);
                telemetry.addData("Current Velocity", Motor.getVelocity());
                telemetry.update();
            }
        }
    }
}
