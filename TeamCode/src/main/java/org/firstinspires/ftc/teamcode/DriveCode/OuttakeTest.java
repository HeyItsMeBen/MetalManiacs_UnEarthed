package org.firstinspires.ftc.teamcode.DriveCode;

import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.arcrobotics.ftclib.gamepad.GamepadKeys;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Hardware.Flywheels;

@TeleOp (name="Outtake Tester", group="test")
public class OuttakeTest extends LinearOpMode{
    public GamepadEx gamepad;
    public Flywheels flywheel;
//    public OuttakeHood hood;
    public double angle = 0;

    public double speed = 0.5;
    public int rpm = 3000;

    @Override
    public void runOpMode() {

        flywheel = new Flywheels(hardwareMap);
//        hood = new OuttakeHood(hardwareMap);

        gamepad = new GamepadEx(gamepad1);

        waitForStart();

        //executing
        while (opModeIsActive()) {
//            if (gamepad.getButton(GamepadKeys.Button.RIGHT_BUMPER)) {
//                flywheel.setFlywheelPower(speed);
//            } else {
//                flywheel.stopFlywheel();
//            }
            if(gamepad.getButton(GamepadKeys.Button.RIGHT_BUMPER)){
                flywheel.setFlywheelVelocity(rpm);
            }else{
                flywheel.setFlywheelVelocity(0);
            }


//            angle += gamepad.getLeftY();

//            hood.setAngle(angle);

//            telemetry.addData("angle", angle);
            speed += gamepad.getLeftY()*0.01;
            if (speed >= 1){
                speed = 1;
            }else if(speed<=0.1){
                speed = 0.1;
            }

            rpm += (int) (gamepad.getRightY()*10);
            if (rpm > 6000){
                rpm = 6000;
            }else if (rpm < 1000){
                rpm = 1000;
            }
            telemetry.addData("rpm",flywheel.getCurrentWheelVelocity("neither"));

            telemetry.update();

            idle();
        }
    }
}
