package org.firstinspires.ftc.teamcode.DriveCode;

import com.acmerobotics.roadrunner.Pose2d;

public class PassOnFromAutoValues {
    public enum TeamColor {
        RED,
        BLUE
    }
    public static Pose2d currentPose = new Pose2d(0, 0, 90);

    public static TeamColor teamColor = TeamColor.RED;

    public static void reset() {
        currentPose = new Pose2d(0, 0, 0);
        teamColor = TeamColor.RED;
    }

}