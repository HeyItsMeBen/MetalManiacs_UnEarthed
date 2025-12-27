package org.firstinspires.ftc.teamcode.DriveCode;

import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.arcrobotics.ftclib.gamepad.GamepadKeys;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import org.firstinspires.ftc.teamcode.Hardware.TransferBelt;

@TeleOp (name="Intake Tester", group="test")
public class TransferTester extends LinearOpMode {

    public GamepadEx gamepad;

    TransferBelt belt;

    TransferBelt trapdoor;

    @Override
    public void runOpMode() {

        DcMotor Motor = hardwareMap.get(DcMotor.class, "Intake");

        belt = new TransferBelt(hardwareMap);

        trapdoor = new TransferBelt(hardwareMap);

        gamepad = new GamepadEx(gamepad1);

        waitForStart();

        //executing
        while (opModeIsActive()) {
            if (gamepad.getLeftY() > 0) {
                belt.runConveyorMotor();
            } else if (gamepad.getLeftY() < 0) {
                belt.reverseConveyorMotor();
            }
            belt.stopConveyorMotor();

            if (gamepad.wasJustPressed(GamepadKeys.Button.X)) {
                belt.trapdoorServoOpen();
            } else if (gamepad.wasJustPressed(GamepadKeys.Button.A)) {
                belt.trapdoorServoClose();
            }

            idle();
        }
    }
}
