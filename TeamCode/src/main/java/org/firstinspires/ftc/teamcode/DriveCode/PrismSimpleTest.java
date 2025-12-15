package org.firstinspires.ftc.teamcode.DriveCode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import org.firstinspires.ftc.teamcode.Prism.GoBildaPrismDriver;
import org.firstinspires.ftc.teamcode.Prism.PrismAnimations;
import org.firstinspires.ftc.teamcode.Prism.Color;

@TeleOp(name = "Prism Simple Test", group = "Test")
public class PrismSimpleTest extends LinearOpMode {

    GoBildaPrismDriver prism;

    @Override
    public void runOpMode() {
        // Initialize the Prism driver
        prism = hardwareMap.get(GoBildaPrismDriver.class, "prism");

        telemetry.addData("Status", "Prism Initialized");
        telemetry.addData("Device ID", prism.getDeviceID());
        telemetry.addData("Firmware", prism.getFirmwareVersionString());
        telemetry.addData("Hardware", prism.getHardwareVersionString());
        telemetry.addData("LED Count", prism.getNumberOfLEDs());
        telemetry.addData("Info", "Press Play to test colors");
        telemetry.update();

        waitForStart();

        // Clear any existing animations
        prism.clearAllAnimations();

        while (opModeIsActive()) {
            // Test Red - Create a solid color animation
            telemetry.addData("Color", "RED");
            telemetry.update();
            PrismAnimations.Solid red = new PrismAnimations.Solid(Color.RED);
            prism.insertAndUpdateAnimation(GoBildaPrismDriver.LayerHeight.LAYER_0, red);
            sleep(2000);

            // Test Green
            telemetry.addData("Color", "GREEN");
            telemetry.update();
            PrismAnimations.Solid green = new PrismAnimations.Solid(Color.GREEN);
            prism.insertAndUpdateAnimation(GoBildaPrismDriver.LayerHeight.LAYER_0, green);
            sleep(2000);

            // Test Blue
            telemetry.addData("Color", "BLUE");
            telemetry.update();
            PrismAnimations.Solid blue = new PrismAnimations.Solid(Color.BLUE);
            prism.insertAndUpdateAnimation(GoBildaPrismDriver.LayerHeight.LAYER_0, blue);
            sleep(2000);

            // Test White
            telemetry.addData("Color", "WHITE");
            telemetry.update();
            PrismAnimations.Solid white = new PrismAnimations.Solid(Color.WHITE);
            prism.insertAndUpdateAnimation(GoBildaPrismDriver.LayerHeight.LAYER_0, white);
            sleep(2000);

            // Test Rainbow animation
            telemetry.addData("Animation", "RAINBOW");
            telemetry.update();
            PrismAnimations.Rainbow rainbow = new PrismAnimations.Rainbow();
            prism.insertAndUpdateAnimation(GoBildaPrismDriver.LayerHeight.LAYER_0, rainbow);
            sleep(5000);

            // Turn off
            telemetry.addData("Status", "OFF");
            telemetry.update();
            prism.clearAllAnimations();
            sleep(2000);
        }
    }
}