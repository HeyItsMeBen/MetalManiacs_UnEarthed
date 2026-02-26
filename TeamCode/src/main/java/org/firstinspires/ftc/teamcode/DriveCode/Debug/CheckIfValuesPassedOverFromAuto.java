package org.firstinspires.ftc.teamcode.DriveCode.Debug;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.DriveCode.PassOnFromAutoValues;
import org.firstinspires.ftc.teamcode.Hardware.Lights;

@TeleOp (name="Check If Values Passed Over Properly From Auto", group="Debug")
public class CheckIfValuesPassedOverFromAuto extends LinearOpMode {

    Lights lights;

    @Override
    public void runOpMode() {

        waitForStart();
        //executing
        while (opModeIsActive()) {

            telemetry.addData("Position: ", PassOnFromAutoValues.currentPose.position.x + ", "
                    + PassOnFromAutoValues.currentPose.position.y + ", " + PassOnFromAutoValues.currentPose.heading);

            if (PassOnFromAutoValues.teamColor == PassOnFromAutoValues.TeamColor.RED) {
                lights.updateLights("Red", false, "XXX", false);
            } else {
                lights.updateLights("Blue", false, "XXX", false);
            }

            telemetry.update();


            idle();
        }
    }
}

