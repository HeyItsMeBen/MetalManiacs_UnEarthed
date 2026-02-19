package org.firstinspires.ftc.teamcode.DriveCode.Debug;

import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.util.ElapsedTime;

@TeleOp (name="Flywheel Ramp-Up", group="Debug")
public class RampFlywheelVelocity extends LinearOpMode {

    public GamepadEx gamepad;

    public DcMotorEx flywheel;
    ElapsedTime flywheelTimer;

    double targetVelocity = 2000;
    double rampSeconds = 5;

    @Override
    public void runOpMode() {

        gamepad = new GamepadEx(gamepad1);

        flywheel = hardwareMap.get(DcMotorEx.class, "flywheel");
        flywheel.setDirection(DcMotorEx.Direction.FORWARD);
        flywheel.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        ElapsedTime timer = new ElapsedTime();
        timer.reset();

        waitForStart();
        //executing
        while (opModeIsActive()) {
            while (opModeIsActive()) {

                double rampTime = rampSeconds;        // 5 seconds
                double maxVelocity = targetVelocity;  // 2000 ticks per second
                double elapsed = timer.seconds();
                double progress = Math.min(elapsed / rampTime, 1.0);
                double newVelocity = maxVelocity * progress;

                flywheel.setVelocity(newVelocity);

                telemetry.addData("Target Velocity", targetVelocity);
                telemetry.addData("Current Velocity", flywheel.getVelocity());
                telemetry.update();
            }
        }
    }
}
