package org.firstinspires.ftc.teamcode.DriveCode.Test_Files;

import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.arcrobotics.ftclib.gamepad.GamepadKeys;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Hardware.Flywheels;
import org.firstinspires.ftc.teamcode.Hardware.Intake;
import org.firstinspires.ftc.teamcode.Hardware.Transfer;

@TeleOp (name="Test Issue", group="test")
public class TurretTesterIssue extends LinearOpMode {

    public GamepadEx gamepad;

    Intake intake;
    Transfer transfer;
    Flywheels flywheels;

    private boolean initialized = false;
    private boolean flywheelIsReady = false;
    private int artifactsLaunched = 0;
    private long lastShotTime = 0;
    private long spinUpStartTime = 0;
    private final long spinUpTimeout = 3000; // max 3 seconds to reach speed
    private double targetRPM = 0;
    private int timeBetweenLaunches = 525;
    private double launchDistance = 6; // in feet

    @Override
    public void runOpMode() {
        gamepad = new GamepadEx(gamepad1);

        intake = new Intake(hardwareMap);
        transfer = new Transfer(hardwareMap);
        flywheels = new Flywheels(hardwareMap);

        waitForStart();

        while (opModeIsActive()) {
            if (gamepad.isDown(GamepadKeys.Button.Y)) {

                intake.runIntakeFullPower();

                if (!initialized) {
                    launchDistance = 5.2;
                    targetRPM = flywheels.launchFromDistance(launchDistance);

                    spinUpStartTime = System.currentTimeMillis();
                    lastShotTime = spinUpStartTime;
                    initialized = true;
                }

                double currentRPM = flywheels.getFlywheelSpeedRaw();

                telemetry.addData("Current RPM", currentRPM);
                telemetry.addData("Target RPM", targetRPM);
                telemetry.addData("Distance", launchDistance);
                telemetry.addData("Artifacts Fired", artifactsLaunched);

                boolean rpmReady = currentRPM >= targetRPM*0.85;
                boolean timedOut = (System.currentTimeMillis() - spinUpStartTime) > spinUpTimeout;

                flywheelIsReady = rpmReady || timedOut;

                if (rpmReady) {
                    transfer.setTransferPower(0.7);     // only allowed when RPM is good
                    telemetry.addData("Transfer", "RUNNING");
                } else {
                    transfer.stopTransfer();    // forced off whenever RPM drops
                    telemetry.addData("Transfer", "STOPPED");
                }

                if (!flywheelIsReady) {
                    telemetry.addData("Status", "Waiting...");
                    telemetry.update();

                }

                long now = System.currentTimeMillis();
                if (rpmReady || artifactsLaunched <= 3 && now - lastShotTime > timeBetweenLaunches) {
                    artifactsLaunched++;
                    lastShotTime = now;
                    telemetry.addData("Shot", artifactsLaunched);
                }

                if (artifactsLaunched > 3) {
                    flywheels.setFlywheelSpeedRaw(targetRPM / 2);
                    transfer.stopTransfer();
                    telemetry.addData("Status", "Finished");
                    telemetry.update();

                }

                telemetry.update();


            } else {
                initialized = false;
                flywheelIsReady = false;
                artifactsLaunched = 0;
                transfer.stopTransfer();
                flywheels.setFlywheelSpeedRaw(0);
            }
        }
    }
}

