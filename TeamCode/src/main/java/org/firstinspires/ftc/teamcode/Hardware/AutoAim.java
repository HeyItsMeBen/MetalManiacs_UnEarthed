package org.firstinspires.ftc.teamcode.Hardware;

import com.acmerobotics.roadrunner.Pose2d;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;
import org.firstinspires.ftc.teamcode.Hardware.AutoAimCameraSupport.RotationMatrices;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;

public class AutoAim {
    RotationMatrices rotationMatrices;
    //PID Gains for turning
    final private double TURN_GAIN   =  0.01 ;   //  Turn Control "Gain".  e.g. Ramp up to 25% power at a 25 degree error. (0.25 / 25.0)
    final private double MAX_AUTO_TURN  = 0.9;   //  Clip the turn speed to this max value (adjust for your robot)

    //distances (pre-set inputs)
    private double tagToGoalCenter_Distance=toMeters(10);
    private double robotCenterToArmBase_Distance=toMeters(2.5);
    private double cameraToRobotCenter_Distance=toMeters(4.25);
    public double basketHeight=toMeters(39-16);   //Vertical distance from the shooting mechanism to the goal center.
    private double cameraPitch=0;

    //public variables (outputs. The stuff that we calculate)
    public double turn = 0;

    public double launchPointToGoalCenterX_Distance_Meters =0;  //Vertical distance from the shooting mechanism to the goal center.
    public double launchPointToGoalCenterX_Distance_Inches =0;  //Vertical distance from the shooting mechanism to the goal center.
    public double angleDeviation=0; //The angle that the robot needs to turn away from the tag, in order to point towards the goal center. See NewTransfer_DriveCode for example usage.
    public double distanceToTagTelemetry=0;
    public double yawTelemetry=0;

    public double xpos;
    public double ypos;
    public double posX;
    public double posY;
    public double botAngleThing;
    public double turretAngle;

    public AutoAim(double cameraAngleOfElevation){  //input the camera's angle when creating the autoAim object. So if it's tilted up by 15 degrees, input Math.toRadians(15).
        cameraPitch=cameraAngleOfElevation;
        rotationMatrices = new RotationMatrices();
    }
    private double toMeters(double inches){
        return inches/39.3700787;
    }

    public void calculateEverything(AprilTagDetection desiredTag){
        launchPointToGoalCenterX_Distance_Meters = getCorrectDistance2(toMeters(desiredTag.ftcPose.range), Math.toRadians(desiredTag.ftcPose.yaw)-Math.toRadians(desiredTag.ftcPose.bearing), Math.toRadians(desiredTag.ftcPose.pitch), Math.toRadians(desiredTag.ftcPose.elevation)); //Basically the horizontal distance to the tag
        launchPointToGoalCenterX_Distance_Inches = launchPointToGoalCenterX_Distance_Meters * 39.3700787;
        double  headingError    = (desiredTag.ftcPose.bearing+Math.toDegrees(angleDeviation));
        turn   = Range.clip(headingError * TURN_GAIN, -MAX_AUTO_TURN, MAX_AUTO_TURN);
        //botAngleThing=Math.PI/2-(Math.toRadians(90-35)+(Math.PI-((Math.PI-yawTelemetry)+angleDeviation)));
        //xpos=launchPointToGoalCenterX_Distance*Math.cos(botAngleThing);
        //ypos=launchPointToGoalCenterX_Distance*Math.sin(botAngleThing);
        botAngleThing=Math.PI/2-(Math.toRadians(90-35)+yawTelemetry);
        xpos=distanceToTagTelemetry*Math.cos(botAngleThing);
        ypos=distanceToTagTelemetry*Math.sin(botAngleThing);
    }

    @Deprecated
    public void relocalize(AprilTagDetection desiredTag){
        launchPointToGoalCenterX_Distance_Meters = getCorrectDistance2(toMeters(desiredTag.ftcPose.range), Math.toRadians(desiredTag.ftcPose.yaw)-Math.toRadians(desiredTag.ftcPose.bearing), Math.toRadians(desiredTag.ftcPose.pitch), Math.toRadians(desiredTag.ftcPose.elevation)); //Basically the horizontal distance to the tag
        launchPointToGoalCenterX_Distance_Inches = launchPointToGoalCenterX_Distance_Meters * 39.3700787;
        botAngleThing=Math.toRadians(35)-(yawTelemetry-Math.toRadians(desiredTag.ftcPose.bearing));
        xpos=distanceToTagTelemetry*Math.cos(botAngleThing);
        ypos=distanceToTagTelemetry*Math.sin(botAngleThing);
    }
    public void calculateEverythingWithoutCamera(Pose2d pos, boolean isRed){
        double heading=pos.heading.toDouble();
        if (isRed) {
            posX = toMeters(55 - pos.position.x);
        } else {
            posX = toMeters(-55 - pos.position.x);
        }
        posY = toMeters(59 - pos.position.y);
        launchPointToGoalCenterX_Distance_Meters = Math.sqrt(Math.pow((posX), 2) + Math.pow((posY), 2))+robotCenterToArmBase_Distance;
        launchPointToGoalCenterX_Distance_Inches = launchPointToGoalCenterX_Distance_Meters * 39.3700787;
        turretAngle = Math.atan2(posY, posX)-heading;
    }

    public double getCorrectDistance2(double givenX, double tagYaw, double tagPitch, double tagElevation){ //this function changes the goalLocation from the AprilTag to the goalCenter. It also translates camera-->robotCenter-->armBase distances so the rest of this file can calculate properly.
        //REMINDER: Use rotation matrices for yaw translation
        double robotBaseX=givenX*Math.cos(tagElevation+cameraPitch)+cameraToRobotCenter_Distance;   //robotBaseX is horizontal distance from the center of the robot to the tag.
        distanceToTagTelemetry=robotBaseX*12;
        double [] actualTagYaw=rotationMatrices.getActualYaw(tagYaw, tagPitch, 0, cameraPitch); //uses rotation matrices to find the tag's actual yaw rotation.
        yawTelemetry=actualTagYaw[0];
        double newX=Math.sqrt(Math.pow(robotBaseX, 2)+Math.pow(tagToGoalCenter_Distance, 2)-2*robotBaseX*tagToGoalCenter_Distance*Math.cos(Math.PI-actualTagYaw[0]));   //law of cosines. New X is equal to the distance from the robotBase to the goalCenter
        angleDeviation=Math.asin(tagToGoalCenter_Distance*Math.sin(Math.PI-actualTagYaw[0])/newX);    //law of sines
        return (newX+robotCenterToArmBase_Distance);  //returns horizontal distance from the launchPoint to goalCenter
    }
}