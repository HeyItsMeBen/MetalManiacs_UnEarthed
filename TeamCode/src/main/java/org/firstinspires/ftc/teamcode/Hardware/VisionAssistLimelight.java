package org.firstinspires.ftc.teamcode.Hardware;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.hardware.HardwareMap;

import java.util.List;

public class VisionAssistLimelight {

    private final Limelight3A limelight;

    // ===== CONFIG =====
    private double kP = 0.018;
    private double maxTurn = 0.35;
    private double toleranceDeg = 1.0;
    // ==================

    public VisionAssistLimelight(HardwareMap hardwareMap, int pipeline) {
        limelight = hardwareMap.get(Limelight3A.class, "limelight");
        limelight.pipelineSwitch(pipeline);
        limelight.start();
    }

    /**
     * Returns a rotation correction based on Limelight detections
     */
    public double getTurnCorrection(boolean enabled) {

        if (!enabled) return 0;

        LLResult result = limelight.getLatestResult();
        if (result == null || !result.isValid()) return 0;

        List<LLResultTypes.DetectorResult> detections =
                result.getDetectorResults();

        LLResultTypes.DetectorResult bestTarget = null;
        double largestArea = 0;

        for (LLResultTypes.DetectorResult det : detections) {
            if (det.getClassName().equals("g")
                    || det.getClassName().equals("p")) {

                double area = det.getTargetArea();
                if (area > largestArea) {
                    largestArea = area;
                    bestTarget = det;
                }
            }
        }

        if (bestTarget == null) return 0;

        double tx = bestTarget.getTargetXDegrees();

        if (Math.abs(tx) < toleranceDeg) return 0;

        double turn = -kP * tx;
        return clamp(turn, -maxTurn, maxTurn);
    }

    private double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }
}
