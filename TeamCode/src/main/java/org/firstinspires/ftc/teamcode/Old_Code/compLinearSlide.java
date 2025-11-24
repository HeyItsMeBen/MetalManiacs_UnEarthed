package org.firstinspires.ftc.teamcode.Old_Code;

import com.arcrobotics.ftclib.controller.PIDController;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.acmerobotics.dashboard.config.Config;

@Config
public class compLinearSlide {

    public static double Kp = 0.02;
    public static double Ki = 0.0;
    public static double Kd = 0.0005;
    public static double Kf = 0.0;

    private final double ticks_in_degree = 537.7 / 360.0;
    private final PIDController slideController = new PIDController(Kp, Ki, Kd);

    public DcMotor rightSlide;
    public DcMotor leftSlide;

    public compLinearSlide(HardwareMap hMap) {
        leftSlide = hMap.get(DcMotor.class, "leftSlide");
        rightSlide = hMap.get(DcMotor.class, "rightSlide");

        leftSlide.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        leftSlide.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        rightSlide.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightSlide.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    // ⚠️ This must run in a loop
    public void setSlidesTarget(double target) {
        slideController.setPID(Kp, Ki, Kd);

        int slidePos = rightSlide.getCurrentPosition();
        double slidePID = slideController.calculate(slidePos, target);
        double slideFF = Math.cos(Math.toRadians(target / ticks_in_degree)) * Kf;
        double slidePower = slidePID + slideFF;

        leftSlide.setPower(slidePower);
        rightSlide.setPower(slidePower);
    }
}
