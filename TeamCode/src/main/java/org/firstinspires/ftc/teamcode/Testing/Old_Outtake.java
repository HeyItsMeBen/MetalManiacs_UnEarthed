package org.firstinspires.ftc.teamcode.Testing;

import com.acmerobotics.dashboard.config.Config;
import com.arcrobotics.ftclib.controller.PIDController;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

@Config

public class Old_Outtake {

    private DcMotor leftSlide = null;
    private DcMotor rightSlide = null;

    private Servo outtakeClawServo = null;
    private Servo leftOuttakeArm = null;
    private Servo rightOuttakeArm = null;

    private PIDController slideController;

    public static double Kp = 0.02, Ki = 0, Kd = 0.0005;
    double Kf = 0 ;
    public static int slidetarget = 0;

    ElapsedTime runtime = new ElapsedTime();
    //pick up arm servo pos
    double[] STATE_1 = {1,0};

    //Stand-by arm servo pos
    double[] STATE_2 = {0.8,.2};

    //ready to score arm servo pos
    double[] STATE_3 = {.2,.8};

    //Scored arm servo pos
    double[] STATE_4 = {0.1,0.9};

    double[] STATE_5 = {0.5, 0.5};

    //Outtake subsystem
    public Old_Outtake(HardwareMap hwMap) {
        leftSlide = hwMap.get(DcMotor.class, "leftSlide");
        rightSlide = hwMap.get(DcMotor.class, "rightSlide");
        leftOuttakeArm = hwMap.get(Servo.class, "leftOuttake");
        rightOuttakeArm = hwMap.get(Servo.class, "rightOuttake");
        outtakeClawServo = hwMap.get(Servo.class, "outtakeServo");

        leftSlide.setDirection(DcMotorSimple.Direction.FORWARD);
        rightSlide.setDirection(DcMotorSimple.Direction.FORWARD);

        leftSlide.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        leftSlide.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        rightSlide.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightSlide.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        leftSlide.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightSlide.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        slideController = new PIDController(Kp, Ki, Kd);
    }

    public void slidesMove(int slidetarget) {
        slideController.setPID(Kp, Ki, Kd);
        double ticks_in_degree = 537.7 / 360;
        int slidePos = rightSlide.getCurrentPosition();
        double slidePID = slideController.calculate(slidePos, slidetarget);
        //Gobilda 202 19.2:1

        double slideFF = Math.cos(Math.toRadians(slidetarget / ticks_in_degree)) * Kf;

        double slidePower = slidePID + slideFF;

        if (slidetarget == 0) {
            leftSlide.setPower(slidePower * 0.8);
            rightSlide.setPower(slidePower * 0.8);
        } else {
            leftSlide.setPower(slidePower);
            rightSlide.setPower(slidePower);
        }

    }
    public void outtakeServoOpen(){
        runtime.reset();
        outtakeClawServo.setPosition(0.2);
        if (slidetarget == -3300){
            if (runtime.milliseconds() > 300){
                outtakearmPosState3();
            }
        }
    }

    public void outtakeServoClose(){outtakeClawServo.setPosition(0.035);}

    public void outtakeServoClosetight(){outtakeClawServo.setPosition(0);}

    public void outtakearmPosState1(){
        leftOuttakeArm.setPosition(STATE_1[0]);
        rightOuttakeArm.setPosition(STATE_1[1]);

    }

    public void outtakearmPosState2(){
        leftOuttakeArm.setPosition(STATE_2[0]);
        rightOuttakeArm.setPosition(STATE_2[1]);
    }

    public void outtakearmPosState3(){
        leftOuttakeArm.setPosition(STATE_3[0]);
        rightOuttakeArm.setPosition(STATE_3[1]);
    }

    public void outtakearmPosState4(){
        leftOuttakeArm.setPosition(STATE_4[0]);
        rightOuttakeArm.setPosition(STATE_4[1]);
    }

    public void manualSlidesMove(double slidePower){
        rightSlide.setPower(slidePower);
        leftSlide.setPower(slidePower);
    }
    public void outtakearmPosState5() {
        leftOuttakeArm.setPosition(STATE_5[0]);
        rightOuttakeArm.setPosition(STATE_5[1]);
    }
    //end
}
