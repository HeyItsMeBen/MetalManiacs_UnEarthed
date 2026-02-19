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

import org.firstinspires.ftc.teamcode.Controllers.AutoAimTurretController;
import org.firstinspires.ftc.teamcode.Controllers.DriveChassisController;
import org.firstinspires.ftc.teamcode.Controllers.FlywheelController;
import org.firstinspires.ftc.teamcode.Controllers.IntakeController;
import org.firstinspires.ftc.teamcode.Controllers.LightsController;
import org.firstinspires.ftc.teamcode.Controllers.RumbleController;
import org.firstinspires.ftc.teamcode.Hardware.Flywheels;
import org.firstinspires.ftc.teamcode.Hardware.Intake;
import org.firstinspires.ftc.teamcode.Hardware.Lights;
import org.firstinspires.ftc.teamcode.Hardware.Transfer;
import org.firstinspires.ftc.teamcode.Hardware.OuttakeHood;

import java.util.List;

@TeleOp(name = "[Use Me!] Competition DriveCode v4.0.0", group = "TeleOP")
public class CompetitionDriveCode extends OpMode {

    public GamepadEx driver;
    public GamepadEx operator;
    public RumbleController controller;
    // Mechanisms
    Intake intake;
    Flywheels flywheels;
    Transfer transferDrum;
    Transfer transferServo;
    Lights lights;
    OuttakeHood hood;

    // Controllers
    DriveChassisController driveController;
    AutoAimTurretController autoAimController;
    FlywheelController flywheelController;
    IntakeController intakeController;
    LightsController lightsController;

    public String teamColor = "Red";

    double oldTime;

    @Override
    public void init() {

        driver = new GamepadEx(gamepad1);
        operator = new GamepadEx(gamepad2);
        controller = new RumbleController(gamepad1);

        intake = new Intake(hardwareMap);
        flywheels = new Flywheels(hardwareMap);
        transferDrum = new Transfer(hardwareMap);
        transferServo = new Transfer(hardwareMap);
        lights = new Lights(hardwareMap);
        hood = new OuttakeHood(hardwareMap);

        driveController = new DriveChassisController(hardwareMap);
        autoAimController = new AutoAimTurretController(hardwareMap);
        flywheelController = new FlywheelController(flywheels, transferDrum, transferServo, intake, hood);
        intakeController = new IntakeController(intake, transferDrum, transferServo);
        lightsController = new LightsController(lights);

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        // Bulk read optimization
        List<LynxModule> allHubs = hardwareMap.getAll(LynxModule.class);
        for (LynxModule hub : allHubs) {
            hub.setBulkCachingMode(LynxModule.BulkCachingMode.MANUAL);
        }
    }

    @Override
    public void loop() {

        // Team color toggle
        if (driver.wasJustPressed(GamepadKeys.Button.START)){
            teamColor = teamColor.equals("Red") ? "Blue" : "Red";
        }


        // Drive Controls
        double forward = -driver.getLeftY();
        double right = -driver.getLeftX();
        double rightStickX = -driver.getRightX();
        double rightStickY = -driver.getRightY();
        double rotate;

        if (driver.getButton(GamepadKeys.Button.Y)) {
            driveController.resetYaw();
        }

        if (driver.wasJustPressed(GamepadKeys.Button.RIGHT_STICK_BUTTON)) {
            driveController.toggleSnapRotation();
        }

        if (driver.wasJustPressed(GamepadKeys.Button.LEFT_STICK_BUTTON)) {
            driveController.toggleFieldCentric();
        }

        if (driver.getButton(GamepadKeys.Button.DPAD_UP)) {
            driveController.changeSpeedMultiplier(0.5);
        }

        if (driver.getButton(GamepadKeys.Button.DPAD_DOWN)) {
            driveController.changeSpeedMultiplier(-0.5);
        }

        if (driveController.isSnapRotation()) {
            rotate = driveController.calculateSnapRotation(
                    rightStickX, rightStickY, false
            );
        } else {
            rotate = rightStickX;
        }

        driveController.drive(forward, right, rotate);


        // Autoaim and Turret
        if (driver.wasJustPressed(GamepadKeys.Button.A)) {
            autoAimController.toggleAutoAim();
        }

        if (driver.wasJustPressed(GamepadKeys.Button.X)) {
            autoAimController.resetTurret();
        }
        autoAimController.relocalize(
                driver.getButton(GamepadKeys.Button.DPAD_LEFT),
                driver.getButton(GamepadKeys.Button.DPAD_RIGHT)
        );


        // Intake
        if (driver.wasJustPressed(GamepadKeys.Button.RIGHT_BUMPER)) {
            intakeController.toggleIntake();
        }

        if (driver.wasJustPressed(GamepadKeys.Button.LEFT_BUMPER)) {
            intakeController.toggleReverse();
        }

        intakeController.update();


        // Flywheels
        boolean triggerPressed =
                driver.getTrigger(GamepadKeys.Trigger.RIGHT_TRIGGER) > 0.1;

        flywheelController.update(
                triggerPressed,
                autoAimController.getDistanceToGoalInches(),
                autoAimController.isTargetFound()
        );


        telemetry.addData("Auto aiming",
                autoAimController.isAutoAiming());

        telemetry.addData("Sees april tag",
                autoAimController.isTargetFound());

        telemetry.addData("Goal distance (inches)",
                autoAimController.getDistanceToGoalInches());

        telemetry.addData("Target turret angle (degrees)",
                Math.toDegrees(autoAimController.turretAngleTelemetry));

        telemetry.addData("Launcher State",
                flywheelController.getState());

        telemetry.addData("Target RPM",
                flywheelController.getTargetSpeed());

        telemetry.addData("Current RPM",
                flywheels.getFlywheelVelocity());


        // LED
        lightsController.update(
                autoAimController.isTargetFound(),
                intakeController.isIntakeRunning(),
                teamColor
        );


        driver.readButtons();
        operator.readButtons();

        // Frequency check
        double newTime = getRuntime();
        double loopTime = newTime - oldTime;
        double frequency = 1 / loopTime;
        oldTime = newTime;
        telemetry.addData("LoopTime (Hz):", frequency);
        telemetry.addData("Loop Time (ms): ", loopTime * 1000);
        telemetry.update();

        // Bulk read optimization
        List<LynxModule> allHubs =
                hardwareMap.getAll(LynxModule.class);

        for (LynxModule hub : allHubs) {
            hub.clearBulkCache();
        }
    }

    @Override
    public void stop() {
        lightsController.turnOff();
    }
}
