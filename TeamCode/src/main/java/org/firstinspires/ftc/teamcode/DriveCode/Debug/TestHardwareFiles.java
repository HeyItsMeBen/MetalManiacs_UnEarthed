package org.firstinspires.ftc.teamcode.DriveCode.Debug;

import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.arcrobotics.ftclib.gamepad.GamepadKeys;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Hardware.Flywheels;
import org.firstinspires.ftc.teamcode.Hardware.Intake;
import org.firstinspires.ftc.teamcode.Hardware.Transfer;
import org.firstinspires.ftc.teamcode.Hardware.Turret;

@Disabled
@TeleOp (name="TestHardwareFiles", group="Debug")
public class TestHardwareFiles extends LinearOpMode {

    public GamepadEx gamepad;

    Intake intake;

    Flywheels flywheels;

    Turret turret;
    Transfer transfer;
    String currentSetMechanism="";

    boolean reversed = false;

    ElapsedTime flywheelTimer;

    double targetPower = 1;
    double rampSeconds = 5;

    @Override
    public void runOpMode() {

        gamepad = new GamepadEx(gamepad1);

        intake = new Intake(hardwareMap);
        flywheels = new Flywheels(hardwareMap);
        turret = new Turret(hardwareMap);
        transfer = new Transfer(hardwareMap);
        flywheels = new Flywheels(hardwareMap);


        ElapsedTime timer = new ElapsedTime();
        timer.reset();

        waitForStart();
        //executing
        while (opModeIsActive()) {

            telemetry.addData("Debug File: ", "Applies max continuous power to a motor");
            telemetry.addData("Check for drops in voltage", "");

            telemetry.addData("Press A: ", " Set motor to intake motor");
            telemetry.addData("Press B: ", " Set motor to flywheel motor");
            telemetry.addData("Press X: ", " Set motor to turret motor");
            telemetry.addData("Press Y: ", " Set motor to transfer motor");
            telemetry.addData("Press Right Bumper: ", " Reverse current motor direction");
            telemetry.addData("Press D PAD Up: ", " Run set motor forward");
            telemetry.addData(" ", "");
            telemetry.addData("Current Set Motor: ", currentSetMechanism);
            telemetry.update();

            if (gamepad.wasJustPressed(GamepadKeys.Button.A)) {
                currentSetMechanism = "intake";
            } else if (gamepad.wasJustPressed(GamepadKeys.Button.B)) {
                currentSetMechanism = "flywheel";
            } else if (gamepad.wasJustPressed(GamepadKeys.Button.X)) {
                currentSetMechanism = "turret";
            } else if (gamepad.wasJustPressed(GamepadKeys.Button.Y)) {
                currentSetMechanism = "transferMotor";
            }

//            if (gamepad.isDown(GamepadKeys.Button.DPAD_UP)){
//                double rampTime = rampSeconds;        // 5 seconds
//                double maxPower = targetPower;
//                double elapsed = timer.seconds();
//                double progress = Math.min(elapsed / rampTime, 1.0);
//                double newPower = maxPower * progress;
//                if (currentSetMechanism.equals("turret")){
//                    turret.setMotorPower(0.4);
//                }
//            } else {
//                turret.setMotorPower(0.4);
//            }
            turret.setMotorPower(0.4);

//            if (gamepad.wasJustPressed(GamepadKeys.Button.RIGHT_BUMPER)) {
//                reversed = !reversed;
//                currentMotor.setDirection(
//                        reversed ? DcMotorSimple.Direction.REVERSE
//                                : DcMotorSimple.Direction.FORWARD
//                );
//            }

            gamepad.readButtons();
            idle();
        }
    }
}
