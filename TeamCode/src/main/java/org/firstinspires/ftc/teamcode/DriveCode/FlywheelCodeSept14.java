package org.firstinspires.ftc.teamcode.DriveCode;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;

@TeleOp(name = "48 Hour: Flywheels", group = "48")
@Disabled
public class FlywheelCodeSept14 extends LinearOpMode{

    //IMPORTANT: needs to be tested!!!

    DcMotor leftFly, rightFly;

    double launchPower = 1; // in the future adjust based on distance from goal or battery level
    boolean toggleFly = false; //starts on off mode

    @Override
    public void runOpMode(){
        leftFly = hardwareMap.get(DcMotor.class, "leftFly");
        rightFly = hardwareMap.get(DcMotor.class, "rightFly");

        leftFly.setDirection(DcMotor.Direction.REVERSE); //left wheel should be going opposite direction to launch forward

        telemetry.addData("Status:", "Ready to start code");
        telemetry.update();
        waitForStart();

        while (opModeIsActive()){
            telemetry.update();
            flyWheelToggleTest();

        }


    }
    public void flyWheelToggleTest(){
        if(gamepad2.aWasPressed()){ //a toggles the flywheels on or off
            toggleFly = !toggleFly; // if true make false, if false make true
        }

        if(toggleFly){
            telemetry.addData("Fly Wheels:", "On");
            leftFly.setPower(launchPower);
            rightFly.setPower(launchPower);
        }else {
            telemetry.addData("Fly Wheels:", "Off");
            leftFly.setPower(0);
            rightFly.setPower(0);
        }
    }


}
