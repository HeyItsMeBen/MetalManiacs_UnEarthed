package org.firstinspires.ftc.teamcode.DriveCode;

import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.arcrobotics.ftclib.gamepad.GamepadKeys;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.Hardware.Flywheels;
import org.firstinspires.ftc.teamcode.Hardware.IntakeSystem;
import org.firstinspires.ftc.teamcode.Old_Code.Intake;

@TeleOp (name="Intake Tester", group="test")
public class IntakeTester extends LinearOpMode {

    public GamepadEx gamepad;

    IntakeSystem intake;

    public DcMotor transfer = null;
    public Servo trapdoor = null;
    public int rpm = 3000;
    public double speed = 0.5;

    Flywheels flywheel;



        @Override
    public void runOpMode() {

//        DcMotor Motor = hardwareMap.get(DcMotor.class, "Intake");

        transfer = hardwareMap.get(DcMotor.class, "transfer");
        trapdoor = hardwareMap.get(Servo.class, "trapdoor");

        intake = new IntakeSystem(hardwareMap);

        gamepad = new GamepadEx(gamepad1);

        flywheel = new Flywheels(hardwareMap);


            waitForStart();

        //executing
        while (opModeIsActive()) {
            if (gamepad.getLeftY() > 0) {
                intake.runIntakeFullPower();
                transfer.setPower(1);
            } else if (gamepad.getLeftY() < 0) {
                intake.reverseIntake();
                transfer.setPower(-1);
            }
            intake.stopIntake();
            transfer.setPower(0);

//
//            if(gamepad.getButton(GamepadKeys.Button.RIGHT_BUMPER)){
//                flywheel.setFlywheelVelocity(rpm);
//            }else{
//                flywheel.setFlywheelVelocity(0);
//            }
            if (gamepad.getButton(GamepadKeys.Button.RIGHT_BUMPER)){
//                flywheel.setFlywheelPower(speed);
            }else{
                flywheel.stopFlywheel();
            }

            if (gamepad.getButton(GamepadKeys.Button.A)){
                trapdoor.setPosition(0.3);
            }else{
                trapdoor.setPosition(.1);
            }


//            rpm += (int) (gamepad.getRightY()*10);
//            if (rpm > 6000){
//                rpm = 6000;
//            }else if (rpm < 1000){
//                rpm = 1000;
//            }

            speed += gamepad.getRightY()*0.001;
            if (speed <0.1){
                speed = 0.1;
            }else if (speed > 1){
                speed = 1;
            }
            telemetry.addData("velocity",flywheel.getCurrentWheelRawVelocity("neither"));
            telemetry.addData("rpm",rpm);
            telemetry.addData("speed", speed);
            telemetry.update();

            idle();
        }
    }
}
