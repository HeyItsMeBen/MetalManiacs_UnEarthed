package org.firstinspires.ftc.teamcode.DriveCode;

import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.arcrobotics.ftclib.gamepad.GamepadKeys;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Hardware.Flywheels;
import org.firstinspires.ftc.teamcode.Hardware.Lights;
import org.firstinspires.ftc.teamcode.Hardware.Transfer;
import org.firstinspires.ftc.teamcode.Hardware.Turret;
import org.firstinspires.ftc.teamcode.Hardware.Intake;

//@Disabled
@TeleOp (name="Outtake Tester", group="test")
public class OuttakeTest extends LinearOpMode{
    public GamepadEx gamepad;
    public Flywheels flywheel;
    public Turret turret;
    public Transfer transfer;
    public Intake intake;
    public Lights lights;

    public int intakePower;
    public int distance = 6;
//    public OuttakeHood hood;
    int rpm = 0;
    int slope =90;
    int yint = 1000;

    @Override
    public void runOpMode() {

        flywheel = new Flywheels(hardwareMap);
        turret = new Turret(hardwareMap);
        transfer = new Transfer(hardwareMap);
        intake = new Intake(hardwareMap);
        lights = new Lights(hardwareMap);
//        hood = new OuttakeHood(hardwareMap);

        gamepad = new GamepadEx(gamepad1);

        waitForStart();

        //executing
        while (opModeIsActive()) {

            if(gamepad.getButton(GamepadKeys.Button.RIGHT_BUMPER)){
                telemetry.addData("Optimal Launch Speed",flywheel.getRPMFromDistance(distance));
            }else{
                flywheel.setFlywheelSpeedRaw(0);
            }

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

            turret.setMotorPower(-gamepad.getLeftX()*0.5);

            if(gamepad.getButton(GamepadKeys.Button.B)){
                turret.resetPosition();
            }

            telemetry.addData("turret rotation", turret.getTurretPosition());

            if(gamepad.wasJustPressed(GamepadKeys.Button.DPAD_UP)){
                distance+=1;
            }else if (gamepad.wasJustPressed(GamepadKeys.Button.DPAD_DOWN)){
                distance-=1;
            }

            if(gamepad.wasJustPressed(GamepadKeys.Button.DPAD_RIGHT)){
                yint+=100;
            }else if (gamepad.wasJustPressed(GamepadKeys.Button.DPAD_LEFT)){
                yint-=100;
            }

            rpm += (int) ((int) gamepad.getLeftY() * 0.5);

            telemetry.addData("distance", distance);
//            telemetry.addData("rpm",rpm);
            telemetry.addData("velo",flywheel.getFlywheelRPM());
            telemetry.addLine();
            telemetry.addData("Y (RPM)", rpm);
            telemetry.addData("M (Slope)", slope);
            telemetry.addData("X (distance)", distance);
            telemetry.addData("B (y-int)", yint);
            telemetry.addData("equation", rpm + " = " + slope + " * " + distance + yint);




            gamepad.readButtons();
            telemetry.update();

            idle();
        }
    }
}
