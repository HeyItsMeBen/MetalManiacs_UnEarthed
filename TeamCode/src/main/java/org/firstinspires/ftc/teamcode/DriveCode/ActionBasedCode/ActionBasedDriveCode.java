package org.firstinspires.ftc.teamcode.DriveCode.ActionBasedCode;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.InstantAction;
import com.acmerobotics.roadrunner.SequentialAction;
import com.acmerobotics.roadrunner.SleepAction;
import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.arcrobotics.ftclib.gamepad.GamepadKeys;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

import java.util.ArrayList;
import java.util.List;

@TeleOp(name = "Action Based Drive Code", group = "A - TeleOP")
public class ActionBasedDriveCode extends OpMode {

    private FtcDashboard dash = FtcDashboard.getInstance();
    private List<Action> runningActions = new ArrayList<>();
    public Hardware hardware;
    public GamepadEx driver;

    @Override
    public void init() {
        hardware = new Hardware(hardwareMap);
        driver = new GamepadEx(gamepad1);
    }

    @Override
    public void loop() {
        TelemetryPacket packet = new TelemetryPacket();
        driver.readButtons();

        //gamepad code
        runningActions.add(new SequentialAction(
//                new SleepAction(0.5),
                new InstantAction(() -> hardware.drive(driver.getLeftY())) //drive based off of left joystick up and down
        ));

        runningActions.add(new SequentialAction(
                new InstantAction(() -> hardware.rotate(driver.getRightX())) //rotate direction based off of right joystick
        ));

        if (driver.getButton(GamepadKeys.Button.DPAD_UP)){
            runningActions.add(new SequentialAction(
                    new InstantAction(() -> hardware.clawUp()) //raise claw on dpad up
            ));
        }else if (driver.getButton(GamepadKeys.Button.DPAD_DOWN)){
            runningActions.add(new SequentialAction(
                    new InstantAction(() -> hardware.clawDown()) //lower claw on dpad down
            ));
        }else{
            runningActions.add(new SequentialAction(
                    new InstantAction(() -> hardware.clawStop()) //lower claw on dpad down
            ));
        }

        //update running actions
        List<Action> newActions = new ArrayList<>();
        for (Action action : runningActions) {
            action.preview(packet.fieldOverlay());
            if (action.run(packet)) {
                newActions.add(action);
            }
        }
        runningActions = newActions;

        dash.sendTelemetryPacket(packet);

    }
}
