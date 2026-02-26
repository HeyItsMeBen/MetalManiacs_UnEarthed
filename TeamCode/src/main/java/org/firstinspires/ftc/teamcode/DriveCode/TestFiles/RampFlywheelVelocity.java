package org.firstinspires.ftc.teamcode.DriveCode.TestFiles;

import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.util.ElapsedTime;

@Disabled
@TeleOp (name="Flywheel Ramp-Up Test 4", group="Debug")
public class RampFlywheelVelocity extends LinearOpMode {

    public GamepadEx gamepad;

    public DcMotorEx flywheel;
    ElapsedTime flywheelTimer;

    double targetVelocity = 2000;
    double rampSeconds = 5;

    boolean reversed;

    @Override
    public void runOpMode() {

        gamepad = new GamepadEx(gamepad1);

        flywheel = hardwareMap.get(DcMotorEx.class, "flywheel");
        flywheel.setDirection(DcMotorEx.Direction.FORWARD);
        //flywheel.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
        flywheel.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        //PIDFCoefficients pidfCoefficients = new PIDFCoefficients(10, 0, 0, 14.12);
        //flywheel.setPIDFCoefficients(DcMotorEx.RunMode.RUN_USING_ENCODER, pidfCoefficients);

        ElapsedTime timer = new ElapsedTime();
        timer.reset();

        waitForStart();
        //executing
        while (opModeIsActive()) {
            while (opModeIsActive()) {


                flywheel.setVelocity(targetVelocity);
                //flywheel.setPower(0.5);



                telemetry.addData("Target Velocity", targetVelocity);
                telemetry.addData("Current Velocity", flywheel.getVelocity());
                telemetry.addData("Press Y to reverse direction", "");
                telemetry.update();
            }
        }
    }
}
