package org.firstinspires.ftc.teamcode.Experimental.Limelight;



import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import java.util.List;

@Config
@Autonomous(name = "[OLD ROBOT] Limelight Auto Attempt One")
//@Disabled
public class LimelightAutoTest extends LinearOpMode {

    // Drive motors
    DcMotorEx frontLeft, frontRight, backLeft, backRight, motor;

    // Limelight
    Limelight3A limelight;

    // ===== CONFIG =====
    static final String TARGET_CLASS_NAME = "g"; // change to your NN class
    public static double LimelightkP = 0.015;
    public static double maxVisionTurn = 8;
    public static double toleranceDeg = 1.5;

    boolean visionEnabled = true;
    // ==================


    @Override
    public void runOpMode() {

        // Motors
        frontLeft  = hardwareMap.get(DcMotorEx.class, "frontLeft");
        backLeft   = hardwareMap.get(DcMotorEx.class, "backLeft");
        frontRight = hardwareMap.get(DcMotorEx.class, "frontRight");
        backRight  = hardwareMap.get(DcMotorEx.class, "backRight");
        motor = hardwareMap.get(DcMotorEx.class, "intake");

        backLeft.setDirection(DcMotor.Direction.FORWARD);
        frontLeft.setDirection(DcMotor.Direction.FORWARD);
        backRight.setDirection(DcMotor.Direction.REVERSE);
        frontRight.setDirection(DcMotor.Direction.REVERSE);

        // Limelight
        limelight = hardwareMap.get(Limelight3A.class, "limelight");
        limelight.pipelineSwitch(3);
        limelight.start();

        telemetry.addLine("Robot Ready");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {

            // Driver input
            double forward = -0.25;
            double strafe  =  0;
            double rotate  = 0;

            double visionTurn = 0;

            // if (visionEnabled) {

            LLResult result = limelight.getLatestResult();

            if (result != null && result.isValid()) {

                List<LLResultTypes.DetectorResult> detections = result.getDetectorResults();

                LLResultTypes.DetectorResult bestTarget = null;
                double largestArea = 0;

                for (LLResultTypes.DetectorResult det : detections) {
                    if (det.getClassName().equals("g") || det.getClassName().equals("p")) {

                        double area = det.getTargetArea();
                        if (area > largestArea) {
                            largestArea = area;
                            bestTarget = det;
                        }
                    }
                }

                if (bestTarget != null) {

                    double tx = bestTarget.getTargetXDegrees();

                    if (Math.abs(tx) > toleranceDeg) {
                        visionTurn = LimelightkP * -tx;
                        visionTurn = Math.max(-maxVisionTurn,
                                Math.min(maxVisionTurn, visionTurn));
                    }
                    telemetry.addData("tx", tx);
                }
            }
            // }
            if (!visionEnabled){
                visionTurn = 0;
            }

            motor.setPower(1);

            // Combine driver + vision
            double finalRotate = rotate + visionTurn;

            driveMecanum(forward, strafe, finalRotate);

            telemetry.addData("Vision Assist", visionEnabled);
            telemetry.addData("Vision Turn", visionTurn);
            telemetry.addData("Rotate Total", finalRotate);
            telemetry.update();
        }
    }

    //Standard FTC mecanum drive

    private void driveMecanum(double forward, double strafe, double rotate) {

        double fl = forward + strafe + rotate;
        double bl = forward - strafe + rotate;
        double fr = forward - strafe - rotate;
        double br = forward + strafe - rotate;

        double max = Math.max(
                Math.max(Math.abs(fl), Math.abs(bl)),
                Math.max(Math.abs(fr), Math.abs(br))
        );

        if (max > 1.0) {
            fl /= max;
            bl /= max;
            fr /= max;
            br /= max;
        }

        frontLeft.setPower(fl);
        backLeft.setPower(bl);
        frontRight.setPower(fr);
        backRight.setPower(br);
    }
}
