package org.firstinspires.ftc.teamcode.DriveCode.Test_Files;

import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.arcrobotics.ftclib.gamepad.GamepadKeys;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.firstinspires.ftc.teamcode.Systems.Outtake;

import java.util.ArrayList;
import java.util.List;

// One of the biggest issue with our flywheels is that they drop in velocity when launching each time. As a result,
// this file will determine the time it takes for the velocities to return

@TeleOp (name="Outtake Flywheel Test", group="test")
public class OuttakeFlywheelTestFile extends LinearOpMode {

    public GamepadEx gamepad;

    Outtake outtake;

//    private DcMotorEx rightFlyWheel = null;
//    private DcMotorEx leftFlyWheel = null;



    @Override
    public void runOpMode() {

//        rightFlyWheel = hardwareMap.get(DcMotorEx.class, "rightFlyWheel");
//        leftFlyWheel = hardwareMap.get(DcMotorEx.class, "leftFlyWheel");
//
//        rightFlyWheel.setDirection(DcMotorEx.Direction.REVERSE);
//        leftFlyWheel.setDirection(DcMotorEx.Direction.FORWARD);
//
//        leftFlyWheel.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
//        leftFlyWheel.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        gamepad = new GamepadEx(gamepad1);
        outtake = new Outtake(hardwareMap);

        waitForStart();

        List<String> logHistory = new ArrayList<>();

        //executing
        while (opModeIsActive()) {
            if (gamepad.getButton(GamepadKeys.Button.A)) {
                outtake.setFlywheelVelocity(2500);
                //logHistory.clear();
            }
            else if (gamepad.getButton(GamepadKeys.Button.B)) {
                outtake.setFlywheelVelocity(0);
                //logHistory.clear();
            }

            logHistory.add("Velocities: " + (int) Math.round(outtake.getCurrentWheelRPM("left")) + " , " + (int) Math.round(outtake.getCurrentWheelRPM("right")));

            // Display the last few entries (so it doesn’t overflow)
            int maxLines = 8;
            int start = Math.max(0, logHistory.size() - maxLines);
            for (int i = start; i < logHistory.size(); i++) {
                telemetry.addLine(logHistory.get(i));
            }

            telemetry.update();
            sleep(500);  // prevent spam
        }
    }
}
