package com.example.meepmeep;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;

import org.rowlandhall.meepmeep.MeepMeep;
import org.rowlandhall.meepmeep.roadrunner.DefaultBotBuilder;
import org.rowlandhall.meepmeep.roadrunner.entity.RoadRunnerBotEntity;

import java.awt.Image;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

// In this pathing, the robot goes from the goal to launch pre-stored balls, then cycles the ones still on the field

public class Auto_Path_Simulation_Cycle {
    public static void main(String[] args) {

        double firing_position_x = 15;
        double firing_position_y = 15;

        MeepMeep meepMeep = new MeepMeep(800);

        RoadRunnerBotEntity myBot = new DefaultBotBuilder(meepMeep)
                // Set bot constraints: maxVel, maxAccel, maxAngVel, maxAngAccel, track width
                .setConstraints(60, 30, Math.toRadians(180), Math.toRadians(180), 18)
                .followTrajectorySequence(drive -> drive.trajectorySequenceBuilder(new Pose2d(12, -60, Math.toRadians(270)))

                        .setReversed(true)

                        .splineTo(new Vector2d(20, -30), Math.toRadians(90))
                        .splineTo(new Vector2d(firing_position_x, firing_position_y), Math.toRadians(45))
                        //.strafeTo(new Vector2d(firing_position_x, firing_position_y))

                        //.turn(Math.toRadians(135))
                        .setReversed(false)

                        .splineTo(new Vector2d(50, 6), Math.toRadians(0))
                        .setTangent(180)
                        .splineToSplineHeading(new Pose2d(firing_position_x, firing_position_y, Math.toRadians(225)), Math.toRadians(135))
                        .strafeTo(new Vector2d(15, -40))



//                        .setReversed(false)
//                        .splineTo(new Vector2d(46, -35), 0)
//
//                        .setReversed(true)
//
//                        .splineTo(new Vector2d(20, 20), Math.toRadians(45))
//                        .strafeTo(new Vector2d(firing_position_x, firing_position_y))

                        // Travel to scoring
//                            .lineToSplineHeading(new Pose2d(firing_position_x, firing_position_y, Math.toRadians(225)))
//
//                        // Grabs first set of balls
//
//                            .turn(Math.toRadians(100))
//                            .splineTo(new Vector2d(46, 13), 0) // For PPG
//
//                        // Travel to scoring
//
//                            .setReversed(true)
//                            //.lineToSplineHeading(new Pose2d(firing_position_x, firing_position_y, Math.toRadians(225)))
//                            .splineTo(new Vector2d(firing_position_x, firing_position_y), Math.toRadians(225)) // For PPG
//
//
//                        // Grabs second set of balls
//
//                            .setReversed(false)
//                            .splineTo(new Vector2d(46, -11), 0) // For PGP
//
//                        // Travel to scoring
//
//                            .setReversed(true)
//                            .splineTo(new Vector2d(firing_position_x, firing_position_y), 45)
//
//                        // Grabs third set of balls
//
//                            .setReversed(false)
//                            .splineTo(new Vector2d(46, -35), 0) //For GPP
//
//                        // Travel to scoring
//
//                            .setReversed(true)
//                            .splineTo(new Vector2d(firing_position_x, firing_position_y), 45)
//
//                        // Park outside scoring zone
//
//                            .setReversed(false)
//                            .lineToSplineHeading(new Pose2d(15, -40, Math.toRadians(0)))

                        .build());

        //This is the custom field setup. To see the field PNGs, there is a file in Meepmeep with images, called Field_Backgrounds
        Image img = null;
        try {
            //img = ImageIO.read(new File("C:\\Users\\blu62\\OneDrive\\GitHub\\MetalManiacs_UnEarthed\\MeepMeep\\Field_Backgrounds\\Juice-DECODE-Black.png"));
            img = ImageIO.read(new File("MeepMeep/Field_Backgrounds/Juice-DECODE-Dark.png"));
            //img = ImageIO.read(new File("C:\\Users\\blu62\\OneDrive\\GitHub\\MetalManiacs_UnEarthed\\MeepMeep\\Field_Backgrounds\\Juice-DECODE-Light.png"));
            //img = ImageIO.read(new File("C:\\Users\\blu62\\OneDrive\\GitHub\\MetalManiacs_UnEarthed\\MeepMeep\\Field_Backgrounds\\Juice-DECODE-Paper.png"));
        }
        catch(IOException e) {}

        meepMeep.setBackground(img)
                .setDarkMode(true)
                .setBackgroundAlpha(0.95f)
                .addEntity(myBot)
                .start();
    }
}