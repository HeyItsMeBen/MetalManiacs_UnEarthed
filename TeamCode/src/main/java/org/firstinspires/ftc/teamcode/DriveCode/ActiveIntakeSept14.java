package org.firstinspires.ftc.teamcode.DriveCode;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;

@TeleOp(name="48 Hour: Intake", group="48")
@Disabled
public class ActiveIntakeSept14 extends LinearOpMode{

    //IMPORTANT: needs to be tested!!!
    CRServo intake;
    boolean toggleIntake = false; //starts on off mode

    double intakePower = 1; //starting value should be adjusted

    @Override
    public void runOpMode(){

        intake = hardwareMap.crservo.get("intake");
        telemetry.addData("Status:","Ready to run code.");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()){
        telemetry.update();
        intakeToggleTest();


        }


    }
    public void intakeToggleTest(){
        if (gamepad2.yWasPressed()){ // Use Y button to toggle intake on/off
            toggleIntake = !toggleIntake;// if true make false, if false make true
        }

        if (toggleIntake){
            telemetry.addData("Intake: ", "On");
            intake.setPower(intakePower);
        }else{
            telemetry.addData("Intake: ", "Off");
            intake.setPower(0);
        }

    }
}
