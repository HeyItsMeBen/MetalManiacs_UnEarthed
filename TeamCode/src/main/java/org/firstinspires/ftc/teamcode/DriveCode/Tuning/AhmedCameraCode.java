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

 [INTAKE]
 LEFT BUMPER = reverse intake
 RIGHT BUMPER = intake
 LEFT TRIGGER = transfer kick manual control
 */

package org.firstinspires.ftc.teamcode.DriveCode;

import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.arcrobotics.ftclib.gamepad.GamepadKeys;
import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Controllers.DriveChassisController;
import org.firstinspires.ftc.teamcode.Controllers.IntakeController;
import org.firstinspires.ftc.teamcode.Controllers.LightsController;
import org.firstinspires.ftc.teamcode.Controllers.RumbleController;
import org.firstinspires.ftc.teamcode.Hardware.Intake;
import org.firstinspires.ftc.teamcode.Hardware.Lights;

import java.util.List;

@TeleOp(name = "AhmedDriveCode", group = "A - TeleOP")
public class AhmedDriveCode extends OpMode {

    public GamepadEx driver;
    List<LynxModule> allHubs;

    // Mechanisms
    Intake intake;
    Lights lights;

    // Controllers
    DriveChassisController driveController;
    IntakeController intakeController;
    LightsController lightsController;
    RumbleController rumbleController;

    public String ballSequence = "XXX";

    String teamColor = "Red";
    double oldTime = 0;


    @Override
    public void init() {

        driver = new GamepadEx(gamepad1);
        rumbleController = new RumbleController(gamepad1);

        intake = new Intake(hardwareMap);
        lights = new Lights(hardwareMap);

        //Set the appropriate team color based on the last auto that was run.
        if (PassOnFromAutoValues.teamColor == PassOnFromAutoValues.TeamColor.RED) {
            teamColor = "Red";
        } else {
            teamColor = "Blue";
        }

        //create controllers
        driveController = new DriveChassisController(hardwareMap);

        // FIXED: Re-instantiated intakeController so it does not throw a NullPointerException
        // Note: Adjusted arguments based on your active mechanisms (Intake, null for missing transfers)
        intakeController = new IntakeController(intake, null, null);

        lightsController = new LightsController(lights);

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        intakeController.toggleIntake();

        // Bulk read optimization
        allHubs = hardwareMap.getAll(LynxModule.class);
        for (LynxModule hub : allHubs) {
            hub.setBulkCachingMode(LynxModule.BulkCachingMode.MANUAL);
        }
    }

    @Override
    public void loop() {
        driver.readButtons();

        // Team color toggle
        if (driver.wasJustPressed(GamepadKeys.Button.START)){
            teamColor = teamColor.equals("Red") ? "Blue" : "Red";
        }


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


        if (driver.getTrigger(GamepadKeys.Trigger.LEFT_TRIGGER) > 0.1) {
            intakeController.transferKickUp();
        } else {
            intakeController.transferKickDown();
        }


        // Intake
        if (driver.wasJustPressed(GamepadKeys.Button.RIGHT_BUMPER)) {
            intakeController.toggleIntake();
        }

        if (driver.wasJustPressed(GamepadKeys.Button.LEFT_BUMPER)) {
            intakeController.toggleReverse();
        }

        if (intakeController.getIntakePower() >= 0.5) {
            rumbleController.continuousRumble();
        } else {
            rumbleController.stopContinuosRumbling();
        }

        intakeController.update(gamepad1.touchpad, gamepad1.ps);


        // FIXED: Removed the target found boolean parameter because autoAimController was deleted
        // Pass false as a placeholder for the missing autoAim target boolean
        lightsController.update(
                false,                              // Placeholder for target found
                intakeController.isIntakeRunning(), // Intake status
                teamColor,                          // Your teamColor String line works now!
                ballSequence                        // Ball sequence String
        );


        telemetry.update();

        for (LynxModule hub : allHubs) {
            hub.clearBulkCache();
        }
    }

    @Override
    public void stop() {
        if (lightsController != null) {
            lightsController.turnOff();
        }
    }
}
