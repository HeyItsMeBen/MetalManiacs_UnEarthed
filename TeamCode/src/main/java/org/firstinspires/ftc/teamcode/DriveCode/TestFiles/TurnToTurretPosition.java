package org.firstinspires.ftc.teamcode.DriveCode.TestFiles;

import com.acmerobotics.roadrunner.Pose2d;
import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Controllers.AutoAimTurretController;
import org.firstinspires.ftc.teamcode.Hardware.Turret;

@Disabled
@TeleOp (name="Turn To Turret Position", group="a")
public class TurnToTurretPosition extends LinearOpMode {

    Turret turret;

    AutoAimTurretController autoaim;

    @Override
    public void runOpMode() {

        autoaim = new AutoAimTurretController(hardwareMap, new Pose2d(12, -45, Math.toRadians(0)), "Red");

        waitForStart();
        //executing
        while (opModeIsActive()) {

            autoaim.update2(false, false);

            idle();
        }
    }
}
