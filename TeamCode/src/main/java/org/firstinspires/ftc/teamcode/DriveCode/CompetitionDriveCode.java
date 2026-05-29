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
import org.firstinspires.ftc.teamcode.Hardware.AutoAim;
import org.firstinspires.ftc.teamcode.Hardware.Flywheels;
import org.firstinspires.ftc.teamcode.Hardware.Intake;
import org.firstinspires.ftc.teamcode.Hardware.Lights;
import org.firstinspires.ftc.teamcode.Hardware.Transfer;
import org.firstinspires.ftc.teamcode.Hardware.OuttakeHood;
import org.firstinspires.ftc.teamcode.Hardware.Turret;

import java.util.List;

@TeleOp(name = "[Competition] DriveCode v4.0.0", group = "A - TeleOP")
public class CompetitionDriveCode extends OpMode {

    public GamepadEx driver;
    List<LynxModule> allHubs;

    // Mechanisms
    Intake intake;
    Flywheels flywheels;
    Transfer transferKick;
    Transfer transferDrum;
    Lights lights;
    OuttakeHood hood;

    // Controllers
    DriveChassisController driveController;
    AutoAimTurretController autoAimController;
    FlywheelController flywheelController;
    IntakeController intakeController;
    LightsController lightsController;
    RumbleController rumbleController;

    public String ballSequence = "XXX";

    String teamColor= "Red";

    @Override
    public void init() {

        driver = new GamepadEx(gamepad1);
        rumbleController = new RumbleController(gamepad1);

        intake = new Intake(hardwareMap);
        flywheels = new Flywheels(hardwareMap);
        transferKick = new Transfer(hardwareMap);
        transferDrum = new Transfer(hardwareMap);
        hood = new OuttakeHood(hardwareMap);
        lights = new Lights(hardwareMap);

        //Set the appropriate team color based on the last auto that was run.
        if (PassOnFromAutoValues.teamColor == PassOnFromAutoValues.TeamColor.RED) {
            teamColor="Red";
        } else {
            teamColor="Blue";
        }

        //create controllers
        driveController = new DriveChassisController(hardwareMap);
        autoAimController = new AutoAimTurretController(hardwareMap, PassOnFromAutoValues.currentPose, teamColor);
        intakeController = new IntakeController(intake, transferDrum, transferKick);
        flywheelController = new FlywheelController(flywheels, transferDrum, transferKick, intake, hood, intakeController);
        lightsController = new LightsController(lights);

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

        // Team color toggle
        if (driver.wasJustPressed(GamepadKeys.Button.START)){
            teamColor = teamColor.equals("Red") ? "Blue" : "Red";
            autoAimController.changeColorTo(teamColor);
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


        // Autoaim and Turret
        if (driver.wasJustPressed(GamepadKeys.Button.A)) {
            autoAimController.toggleAutoAim();
        }

        if (driver.wasJustPressed(GamepadKeys.Button.X)) {
            autoAimController.resetTurret();
        }
        autoAimController.updateWithTimeout(
                driver.getButton(GamepadKeys.Button.DPAD_LEFT),
                driver.getButton(GamepadKeys.Button.DPAD_RIGHT)
        );

        if (driver.getTrigger(GamepadKeys.Trigger.LEFT_TRIGGER) > 0.1) {
            intakeController.transferKickUp();
        }else{
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

        // Flywheels
        boolean triggerPressed =
                driver.getTrigger(GamepadKeys.Trigger.RIGHT_TRIGGER) > 0.1;

        if (driver.wasJustPressed(GamepadKeys.Button.DPAD_UP)) {
            flywheelController.extraOuttakeSpeed+=25;
        } else if (driver.wasJustPressed(GamepadKeys.Button.DPAD_DOWN)) {
            flywheelController.extraOuttakeSpeed-=25;
        }
        flywheelController.update(
                triggerPressed,
                autoAimController.getDistanceToGoalInches(),
                autoAimController.isTargetFound()
        );
        if (flywheelController.shouldRumble){
            flywheelController.shouldRumble = false;
            rumbleController.ballLaunched();
        }

        if (!autoAimController.isCameraAvailable()) {
            telemetry.addData("WARNING", "Camera disconnected - auto aim disabled");
        }

        //Displays important information for driver
        telemetry.addData("extraOuttakeSpeed: ", flywheelController.extraOuttakeSpeed);
        telemetry.addData("IsRed?: ", autoAimController.isRed);

        // Debug telemetry for intake and transfer amps
        telemetry.addData("Intake RPM", "%.2f", intakeController.getIntakeRPM());
        telemetry.addData("Transfer RPM", "%.2f", intakeController.getTransferRPM());

        telemetry.addData("Transfer Jam", intakeController.isJammed);


        // LED
        lightsController.update(
                autoAimController.isTargetFound(),
                intakeController.isIntakeRunning(),
                teamColor,
                ballSequence
        );

        // Frequency check
//        double newTime = getRuntime();
//        double loopTime = newTime - oldTime;
//        double frequency = 1 / loopTime;
//        oldTime = newTime;
//        telemetry.addData("LoopTime (Hz):", frequency);
//        telemetry.addData("Loop Time (ms): ", loopTime * 1000);
        telemetry.update();

        for (LynxModule hub : allHubs) {
            hub.clearBulkCache();
        }
    }

    @Override
    public void stop() {
        if (autoAimController != null) {
            autoAimController.shutdown();
        }
        if (lightsController != null) {
            lightsController.turnOff();
        }
    }
}
