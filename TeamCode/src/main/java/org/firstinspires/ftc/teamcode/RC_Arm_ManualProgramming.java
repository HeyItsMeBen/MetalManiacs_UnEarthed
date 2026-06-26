package org.firstinspires.ftc.teamcode;

import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.arcrobotics.ftclib.gamepad.GamepadKeys;
import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

import java.util.List;

@TeleOp(name = "RC Arm", group = "Robot")
public class RC_Arm_ManualProgramming extends OpMode {

    public GamepadEx driver;

    public Servo lever;

    @Override
    public void init() {

        driver = new GamepadEx(gamepad1);

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        // Bulk read optimization
        List<LynxModule> allHubs = hardwareMap.getAll(LynxModule.class);
        for (LynxModule hub : allHubs) {hub.setBulkCachingMode(LynxModule.BulkCachingMode.MANUAL);}
    }

    @Override
    public void loop() {

        if (driver.isDown(GamepadKeys.Button.A)) {

        }

        // Bulk read optimization
        List<LynxModule> allHubs = hardwareMap.getAll(LynxModule.class);
        for (LynxModule hub : allHubs) { hub.clearBulkCache();}
    }

}
