package org.firstinspires.ftc.teamcode.Experimental.Limelight;



import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;



import java.util.List;

@TeleOp(name = "[OLD ROBOT] TeleOp Vision Assist Mecanum")
//@Disabled
public class TeleOpVisionAssistMecanum extends LinearOpMode {

    // Drive motors
    DcMotorEx frontLeft, frontRight, backLeft, backRight, motor;

    // Limelight
    Limelight3A limelight;

    // ===== CONFIG =====
    public static double LimelightkP = 0.012;
    public static double maxVisionTurn = 10;
    public static double toleranceDeg = 2;

    public static String artifactHolding = "xxx";
    public static boolean artifactLastSeen = false;

    public static String lastArtifact = "x";

    boolean visionEnabled = false;
    double visionTurn = 0;

    private ElapsedTime limelightTimer = new ElapsedTime();
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

        artifactHolding = "xxx";

        waitForStart();
        limelightTimer.reset();

        while (opModeIsActive()) {

            // Driver input
            double forward = -gamepad2.left_stick_y;
            double strafe  =  gamepad2.left_stick_x;
            double rotate  =  gamepad2.right_stick_x;

            boolean visionEnabled = gamepad2.right_bumper;



            // if (visionEnabled) {

            LLResult result = limelight.getLatestResult();

            if (result != null && result.isValid()) {

                List<LLResultTypes.DetectorResult> detections = result.getDetectorResults();

                LLResultTypes.DetectorResult bestTarget = null;
                double largestArea = 5000;

                for (LLResultTypes.DetectorResult det : detections) {
                    if (det.getClassName().equals("g") || det.getClassName().equals("p")) { //check for purple or green artifacts

                        // double area = det.getTargetArea();
                        double area = det.getTargetYDegrees();
                        if (area < largestArea) {
                            largestArea = area;
                            bestTarget = det;
                        }
                    }
                }

                if (bestTarget != null) {

                    double tx = bestTarget.getTargetXDegrees();
                    double ty = bestTarget.getTargetYDegrees();

                    if (Math.abs(tx) > toleranceDeg) {
                        visionTurn = LimelightkP * -tx;
                        visionTurn = Math.max(-maxVisionTurn,
                                Math.min(maxVisionTurn, visionTurn));
                    }

                    // check to see if artifact was collected
                    if(ty < -16 && limelightTimer.milliseconds() > 500 ){
                        artifactLastSeen = true;
                        lastArtifact = bestTarget.getClassName();
                        String temp = "";
                        boolean found = false;
                        //tracker for artifacts the robot is currently holding
                        for(int x = 0; x < 3; x++){
                            if(!found && artifactHolding.substring(x, x+1).equals("x")){
                                found = true;
                                temp = temp + lastArtifact;

                            }else{
                                temp = temp + artifactHolding.substring(x, x+1);
                            }
                        }
                        limelightTimer.reset();
                        artifactHolding = temp;
                    }else {
                        artifactLastSeen = false;
                        lastArtifact = "x";
                    }
                    telemetry.addData("tx", tx);
                } /*else if (artifactLastSeen) {
                    String temp = "";
                    boolean found = false;
                    //tracker for artifacts the robot is currently holding
                    for(int x = 0; x < 3; x++){
                        if(!found && artifactHolding.substring(x, x+1).equals("x")){
                            found = true;
                            temp = temp + lastArtifact;

                        }else{
                            temp = temp + artifactHolding.substring(x, x+1);
                        }
                    }
                    artifactHolding = temp;


                }*/
            }
            // }
            if (!visionEnabled){
                visionTurn = 0;
            }

            motor.setPower(1); //runs intake on leDog

            // Combine driver + vision
            double finalRotate = rotate + visionTurn;

            driveMecanum(forward, strafe, finalRotate);

            telemetry.addData("Vision Assist", visionEnabled);
            telemetry.addData("Vision Turn", visionTurn);
            telemetry.addData("Rotate Total", finalRotate);
            telemetry.addData("Current artifacts", artifactHolding);
            telemetry.addData("Currently tracking", lastArtifact);
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
