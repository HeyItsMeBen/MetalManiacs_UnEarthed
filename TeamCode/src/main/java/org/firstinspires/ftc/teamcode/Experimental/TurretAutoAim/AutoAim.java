package org.firstinspires.ftc.teamcode.Experimental.TurretAutoAim;

import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.teamcode.AutoCode.Testing.RotationMatrices;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;

public class AutoAim {
    RotationMatrices rotationMatrices;
    //autoAim stuff
    final private double TURN_GAIN   =  0.05 ;   //  Turn Control "Gain".  e.g. Ramp up to 25% power at a 25 degree error. (0.25 / 25.0)
    final private double MAX_AUTO_TURN  = 0.3;   //  Clip the turn speed to this max value (adjust for your robot)

    //auto aim calculation variables
    private double gravity = 9.8;
    private double ballVelocity=rpmToMetersPerSecond(ticksToRpm(1000, 28), toMeters(5));

    //distances
    private double tagToGoalCenter_Distance=toMeters(10);
    private double robotCenterToArmBase_Distance=toMeters(2.5);    //0.25-->0
    private double cameraToRobotCenter_Distance=toMeters(4.25);    //will probably be a negative if camera is on turret
    private double basketHeight=toMeters(21);   //NEEDS TO BE: basketHeight from floor, minus the height of launch point from floor. //26
    private double cameraPitch=0;

    //public variables
    public double turn = 0;
    public double hoodAngle=0;
    public double launchPointToGoalCenterX_Distance=0;
    public double angleDeviation=0;
    public double distanceToTagTelemetry=0;
    public double yawTelemetry=0;
    public AutoAim(double cameraAngleOfElevation){
        cameraPitch=cameraAngleOfElevation;
        rotationMatrices = new RotationMatrices();
    }
    private double toMeters(double inches){
        return inches/39.3700787;
    }
    private double toInches(double inches){
        return inches*39.3700787;
    }
    private double ticksToRpm(double ticksPerSecond, double tickPerRevolution){
        return ticksPerSecond*60/tickPerRevolution;
    }
    private double rpmToMetersPerSecond(double givenRpm, double wheelDiameter){
        return wheelDiameter*Math.PI*(givenRpm/60);
    }
    public void calculateEverything(AprilTagDetection desiredTag){
        launchPointToGoalCenterX_Distance = getCorrectDistance2(toMeters(desiredTag.ftcPose.range), Math.toRadians(desiredTag.ftcPose.yaw)-Math.toRadians(desiredTag.ftcPose.bearing), Math.toRadians(desiredTag.ftcPose.pitch), Math.toRadians(desiredTag.ftcPose.elevation)); //Basically the horizontal distance to the tag
        double  headingError    = desiredTag.ftcPose.bearing+Math.toDegrees(angleDeviation);
        turn   = Range.clip(headingError * TURN_GAIN, -MAX_AUTO_TURN, MAX_AUTO_TURN) ;
        //turret.setMotorPower(turn);

        double launchAngle1=Math.atan((Math.pow(ballVelocity, 2)+Math.sqrt(Math.pow(ballVelocity, 4)-gravity*(gravity*Math.pow(launchPointToGoalCenterX_Distance, 2)+2*(basketHeight)*Math.pow(ballVelocity, 2))))/(gravity*launchPointToGoalCenterX_Distance));  //Uses the "trajectory for projectile motion" equation to find the angle needed to score.
        double launchAngle2=Math.atan((Math.pow(ballVelocity, 2)-Math.sqrt(Math.pow(ballVelocity, 4)-gravity*(gravity*Math.pow(launchPointToGoalCenterX_Distance, 2)+2*(basketHeight)*Math.pow(ballVelocity, 2))))/(gravity*launchPointToGoalCenterX_Distance));

        //find the larger theta value.
        if (launchAngle1>=launchAngle2){
            hoodAngle=launchAngle1;
        }
        else if (launchAngle2>launchAngle1){
            hoodAngle=launchAngle2;
        }
        else {  //if this one runs, something went wrong
            hoodAngle=0;
        }
    }
    public double getCorrectDistance2(double givenX, double tagYaw, double tagPitch, double tagElevation){ //this function changes the goalLocation from the AprilTag to the goalCenter. It also translates camera-->robotCenter-->armBase distances so the rest of this file can calculate properly.
        //REMINDER: Use rotation matrices for yaw translation
        double robotBaseX=givenX*Math.cos(tagElevation+cameraPitch)+cameraToRobotCenter_Distance;   //robotBaseX is horizontal distance from the center of the robot to the tag.
        distanceToTagTelemetry=robotBaseX;
        double [] actualTagYaw=rotationMatrices.getActualYaw(tagYaw, tagPitch, 0, cameraPitch);
        yawTelemetry=actualTagYaw[0];
        double newX=Math.sqrt(Math.pow(robotBaseX, 2)+Math.pow(tagToGoalCenter_Distance, 2)-2*robotBaseX*tagToGoalCenter_Distance*Math.cos(Math.PI-actualTagYaw[0]));   //law of cosines. New X is equal to the distance from the robotBase to the goalCenter
        angleDeviation=Math.asin(tagToGoalCenter_Distance*Math.sin(Math.PI-actualTagYaw[0])/newX);    //law of sines
        return (newX+robotCenterToArmBase_Distance);  //returns horizontal distance from the launchPoint to goalCenter
    }
}
