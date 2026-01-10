package org.firstinspires.ftc.teamcode.DriveCode;

import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.arcrobotics.ftclib.gamepad.GamepadKeys;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Hardware.Flywheels;
import org.firstinspires.ftc.teamcode.Hardware.Transfer;
import org.firstinspires.ftc.teamcode.Hardware.Turret;
import org.firstinspires.ftc.teamcode.Hardware.Intake;

@Disabled
@TeleOp (name="Outtake Tester", group="test")
public class OuttakeTest extends LinearOpMode{
    public GamepadEx gamepad;
    public Flywheels flywheel;
    public Turret turret;
    public Transfer transfer;
    public Intake intake;

    public int intakePower;
    public int distance = 6;
//    public OuttakeHood hood;
    public int rpm = 2100;

    @Override
    public void runOpMode() {

        flywheel = new Flywheels(hardwareMap);
        turret = new Turret(hardwareMap);
        transfer = new Transfer(hardwareMap);
        intake = new Intake(hardwareMap);
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
                telemetry.addData("Optimal Launch Speed",flywheel.launchFromDistance(distance));
            }else{
                flywheel.setFlywheelSpeed(0);
            }

            transfer.trapdoorOpen();

            if (gamepad.wasJustPressed((GamepadKeys.Button.DPAD_RIGHT))) {
                if (Math.abs(intakePower) == 1) {
                    intakePower = 0;
                } else {
                    intakePower = 1;
                }
            } else if (gamepad.wasJustPressed(GamepadKeys.Button.DPAD_LEFT)) {
                if (Math.abs(intakePower) == 1) {
                    intakePower = 0;
                } else {
                    intakePower = -1;
                }
            }
        intake.setIntakePower(intakePower);
        transfer.setTransferPower(intakePower);

        telemetry.addData("Intake speed", intakePower);

        if(gamepad.wasJustPressed(GamepadKeys.Button.Y)){
            turret.resetInitial();
        }

//            angle += gamepad.getLeftY();

//            hood.setAngle(angle);

//            telemetry.addData("angle", angle);
            turret.setMotorPower(gamepad.getLeftX());

            if(gamepad.getButton(GamepadKeys.Button.B)){
                turret.resetPosition();
            }

            telemetry.addData("turret rotation", turret.getTurretPosition());
//            rpm += (int) (gamepad.getRightY()*-10);
//            if (rpm > 3000){
//                rpm = 6000;
//            }else if (rpm < 300){
//                rpm = 300;
//            }
//            distance += (int) -gamepad.getRightY();
            if(gamepad.wasJustPressed(GamepadKeys.Button.DPAD_UP)){
                distance+=1;
            }else if (gamepad.wasJustPressed(GamepadKeys.Button.DPAD_DOWN)){
                distance-=1;
            }
            telemetry.addData("distance", distance);
//            telemetry.addData("rpm",rpm);
            telemetry.addData("velo",flywheel.getCurrentWheelRawVelocity("neither"));

            gamepad.readButtons();
            telemetry.update();

            idle();
        }
    }
}
