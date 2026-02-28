package org.firstinspires.ftc.teamcode.Experimental.Limelight;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.LLStatus;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.Pose3D;

import java.util.List;

//This is the fastest file I've ever written
@Autonomous(name = "Sensor: Limelight3A", group = "Sensor")
public class Limelight3Way extends LinearOpMode {

    private Limelight3A limelight;
    public int PipelineLeft = 0;
    public int PipelineMid = 1;
    public int PipelineRight = 2;

    public int LeftDetections = -1;
    public int RightDetections = -1;
    public int MidDetections = -1;


    @Override
    public void runOpMode() throws InterruptedException
    {
        limelight = hardwareMap.get(Limelight3A.class, "limelight");

        telemetry.setMsTransmissionInterval(11);

        limelight.pipelineSwitch(PipelineLeft); //the artifact detection pipeline is set to index 3

        /*
         * Starts polling for data.  If you neglect to call start(), getLatestResult() will return null.
         */
        limelight.start();

        telemetry.addData(">", "Robot Ready.  Press Play.");
        telemetry.update();
        waitForStart();

        while (opModeIsActive()) {
            limelight.pipelineSwitch(PipelineLeft);
            LLResult result = limelight.getLatestResult();

            if (result != null && result.isValid()) {

                List<LLResultTypes.DetectorResult> detections = result.getDetectorResults();

                LeftDetections = detections.size();



                } else {
                LeftDetections = -1;
            }

            limelight.pipelineSwitch(PipelineMid);
             result = limelight.getLatestResult();

            if (result != null && result.isValid()) {

                List<LLResultTypes.DetectorResult> detections = result.getDetectorResults();

                MidDetections = detections.size();



            } else {
                MidDetections = -1;
            }

            limelight.pipelineSwitch(PipelineRight);
            result = limelight.getLatestResult();

            if (result != null && result.isValid()) {

                List<LLResultTypes.DetectorResult> detections = result.getDetectorResults();

                RightDetections = detections.size();



            } else {
                RightDetections = -1;
            }

            telemetry.addData("Left", " "+LeftDetections);
            telemetry.addData("Mid", " "+MidDetections);
            telemetry.addData("Right", " "+RightDetections);

            if(RightDetections > MidDetections && RightDetections > LeftDetections){
                telemetry.addData("Greatest", "Right");
            } else if (MidDetections > LeftDetections) {
                telemetry.addData("Greatest:", "Center");
            }else{
                telemetry.addData("Greatest", "Left");
            }
            telemetry.update();
        }
        limelight.stop();


        }

    }

