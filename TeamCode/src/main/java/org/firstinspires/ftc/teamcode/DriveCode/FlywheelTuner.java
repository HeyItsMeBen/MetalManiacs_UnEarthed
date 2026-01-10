package org.firstinspires.ftc.teamcode.DriveCode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.PIDCoefficients;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;

@Disabled
@TeleOp(name = "Flywheel PID Tuner", group = "Tuning")
public class FlywheelTuner extends OpMode {
    public DcMotorEx flywheel;

    public double highVelo = 2100;
    public double lowVelo = 700;

    public double curTargetVelo = highVelo;

    //f = 14.12
    //p = 100
    public double f = 14.12;
    public double p = 100;

    public double[] stepSizes = {10.0, 1.0, 0.1, 0.01, 0.001, 0.0001};

    int stepIndex = 1;

    @Override
    public void init(){
        flywheel = hardwareMap.get(DcMotorEx.class, "flywheel");
        flywheel.setDirection(DcMotorEx.Direction.REVERSE);
        flywheel.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
        flywheel.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER); // Keep this

        PIDFCoefficients pidfCoefficients = new PIDFCoefficients(p, 0, 0, f);
        flywheel.setPIDFCoefficients(DcMotorEx.RunMode.RUN_USING_ENCODER, pidfCoefficients);
        telemetry.addLine("Init complete");
    }

    @Override
    public void loop(){
        if (gamepad1.yWasPressed()){
            if(curTargetVelo == highVelo){
                curTargetVelo = lowVelo;
            }else{
                curTargetVelo = highVelo;
            }
        }

        if (gamepad1.bWasPressed()){
            stepIndex = (stepIndex + 1) % stepSizes.length;
        }

        if (gamepad1.dpadLeftWasPressed()){
            f -= stepSizes[stepIndex];
        }

        if (gamepad1.dpadRightWasPressed()){
            f += stepSizes[stepIndex];
        }

        if (gamepad1.dpadDownWasPressed()){
            p += stepSizes[stepIndex];
        }

        if (gamepad1.dpadUpWasPressed()){
            p -= stepSizes[stepIndex];
        }

        PIDFCoefficients pidfCoefficients = new PIDFCoefficients(p, 0, 0, f);
        flywheel.setDirection(DcMotorEx.Direction.REVERSE); // Add this line
        flywheel.setPIDFCoefficients(DcMotorEx.RunMode.RUN_USING_ENCODER, pidfCoefficients);

        flywheel.setVelocity(curTargetVelo);

        double curVelo = flywheel.getVelocity();
        double error = curTargetVelo - curVelo;

        int curPosition = flywheel.getCurrentPosition(); // Add this for debugging

        telemetry.addData("Current Position", curPosition); // Add this
        telemetry.addData("Target Velocity", curTargetVelo);
        telemetry.addData("Current Velocity", "%.2f", curVelo);
        telemetry.addData("Error", "%.2f", error);
        telemetry.addLine("-----------------------------");
        telemetry.addData("Tuning P", "%.4f (D-Pad U/D)", p);
        telemetry.addData("Tuning F", "%.4f (D-Pad L/R)", f);
        telemetry.addData("Step Size", "%.4f (B Button)", stepSizes[stepIndex]);
        telemetry.update();
    }
}
