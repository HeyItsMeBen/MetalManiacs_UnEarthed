/**
 <DRIVER MANUAL FOR DUMMIES>
 *No offense ;)
 --DRIVER CONTROLS--
 [MOVEMENT]
 Y = reset Yaw
 LEFT STICK = translation
 RIGHT STICK = rotation
 LEFT STICK DOWN = toggle field/robot centric drive mode
 RIGHT STICK DOWN = toggle snap/relative rotation mode
 DPAD UP = movement speed up
 DPAD DOWN = movement speed down

 [INTAKE]
 LEFT BUMPER = reverse intake
 RIGHT BUMPER = intake

 [OUTTAKE]
 RIGHT TRIGGER (hold) = charges up flywheels and launches
 DPAD LEFT = turret left
 DPAD RIGHT = turret right
 X = reset turret position (reset at middle)
 A = auto aim
 */

package org.firstinspires.ftc.teamcode.DriveCode;

import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.arcrobotics.ftclib.gamepad.GamepadKeys;
import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Controllers.DriveChassisController;

import java.util.List;

@TeleOp(name = "Basic Chassis DriveCode", group = "A - TeleOP")
public class BasicChassis extends OpMode {

    public GamepadEx driver;
    List<LynxModule> allHubs;

    // Controllers
    DriveChassisController driveController;
    @Override
    public void init() {

        driver = new GamepadEx(gamepad1);

        //create controllers
        driveController = new DriveChassisController(hardwareMap);

        telemetry.addData("Status", "Initialized");
        telemetry.update();


        // Bulk read optimization
        allHubs = hardwareMap.getAll(LynxModule.class);
        for (LynxModule hub : allHubs) {
            hub.setBulkCachingMode(LynxModule.BulkCachingMode.MANUAL);
        }
    }

    @Override
    public void loop() {
        driver.readButtons();


        // Drive Controls
        double forward = driver.getLeftY();
        double right = driver.getLeftX();
        double rightStickX = driver.getRightX();
        double rightStickY = driver.getRightY();
        double rotate;

        //read buttons
        if (driver.getButton(GamepadKeys.Button.Y)) {
            driveController.resetYaw();
        }

        if (driver.wasJustPressed(GamepadKeys.Button.RIGHT_STICK_BUTTON)) {
            driveController.toggleSnapRotation();
        }

        if (driver.wasJustPressed(GamepadKeys.Button.LEFT_STICK_BUTTON)) {
            driveController.toggleFieldCentric();
        }

        if (driveController.isSnapRotation()) {
            rotate = driveController.calculateSnapRotation(
                    rightStickX, rightStickY, false
            );
        } else {
            rotate = rightStickX;
        }

        driveController.drive(forward, right, rotate);

        telemetry.update();

        for (LynxModule hub : allHubs) {
            hub.clearBulkCache();
        }
    }

    @Override
    public void stop() {
    }
}
