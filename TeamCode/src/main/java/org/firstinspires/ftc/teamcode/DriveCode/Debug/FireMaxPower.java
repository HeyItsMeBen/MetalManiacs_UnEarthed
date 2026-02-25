package org.firstinspires.ftc.teamcode.DriveCode.Debug;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.arcrobotics.ftclib.gamepad.GamepadKeys;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Controllers.IntakeController;
import org.firstinspires.ftc.teamcode.Hardware.OuttakeHood;

@Config
@TeleOp(name = "Fire Artifact, Max Power", group = "Debug")
public class FireMaxPower extends LinearOpMode {
    // Motor
    private DcMotorEx flywheel;

    public GamepadEx gamepad;

    public DcMotor intake;
    public DcMotor transferWheels;
    OuttakeHood hood;

    ElapsedTime flywheelTimer;

    double targetPower = 1;
    double rampSeconds = 5;
    boolean reversed;
    double degrees=50;

    @Override
    public void runOpMode() throws InterruptedException {

        // Hardware init
        gamepad = new GamepadEx(gamepad1);
        flywheel = hardwareMap.get(DcMotorEx.class, "flywheel");
        flywheel.setDirection(DcMotorEx.Direction.FORWARD);
        flywheel.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
        flywheel.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);

        intake = hardwareMap.get(DcMotorEx.class, "intake");
        intake.setDirection(DcMotorEx.Direction.FORWARD);

        transferWheels = hardwareMap.get(DcMotor.class, "transferDrum");
        transferWheels.setDirection(DcMotorSimple.Direction.FORWARD);
        hood=new OuttakeHood(hardwareMap);

        telemetry.addLine("Init complete");
        telemetry.update();

        ElapsedTime timer = new ElapsedTime();

        waitForStart();
        while (opModeIsActive()) {

            gamepad.readButtons();

            double rampTime = rampSeconds;        // 5 seconds
            double maxPower = targetPower;  // 2000 ticks per second
            double elapsed = timer.seconds();
            double progress = Math.min(elapsed / rampTime, 1.0);
            double newPower = maxPower * progress;

            if (gamepad.isDown(GamepadKeys.Button.X)) {
                flywheel.setPower(newPower);
            } else {
                flywheel.setPower(0);
            }

            intake.setPower(0.3);
            transferWheels.setPower(1);

            if (gamepad.wasJustPressed(GamepadKeys.Button.Y)) {
                reversed = !reversed;
                flywheel.setDirection(
                        reversed ? DcMotorSimple.Direction.REVERSE
                                : DcMotorSimple.Direction.FORWARD
                );
            }
            if (gamepad.wasJustPressed(GamepadKeys.Button.LEFT_BUMPER)) {
                degrees-=5;
            } else if (gamepad.wasJustPressed(GamepadKeys.Button.RIGHT_BUMPER)) {
                degrees+=5;
            }

            hood.setAngle(Math.toRadians(50));

            // --- Driver Station telemetry ---
            telemetry.addData("Current Power", flywheel.getPower());
            telemetry.addData("Current Velocity", flywheel.getVelocity());
            telemetry.addData("Press Y to reverse direction", "");

            telemetry.addData("Left Bumper: ", gamepad.wasJustPressed(GamepadKeys.Button.LEFT_BUMPER));
            telemetry.addData("Right Bumper: ", gamepad.wasJustPressed(GamepadKeys.Button.RIGHT_BUMPER));

            telemetry.update();
        }
    }
}
