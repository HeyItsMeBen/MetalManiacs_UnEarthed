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

// In this pathing, the robot drives

// Note that optimate firing spot is roughly around x: 10, y: 10

public class Auto_Path_Simulation_Motif {
    public static void main(String[] args) {

        double firing_position_x = 15;
        double firing_position_y = 15;

        MeepMeep meepMeep = new MeepMeep(800);

        RoadRunnerBotEntity myBot = new DefaultBotBuilder(meepMeep)
                // Set bot constraints: maxVel, maxAccel, maxAngVel, maxAngAccel, track width
                .setDimensions(17,17)
                .setConstraints(70, 60, Math.toRadians(180), Math.toRadians(180), 15)
                .followTrajectorySequence(drive -> drive.trajectorySequenceBuilder(new Pose2d(15, -60, Math.toRadians(90))) //Direction the robot faces is based on radians

                        // Getting into position

//                            .lineToLinearHeading(new Pose2d(15, -50, Math.toRadians(90)))
//
//                        // Pathing to motif
//
//                            .splineTo(new Vector2d(48, 13), 0) // For PPG
//
//                            //.splineTo(new Vector2d(46, -11), 0) // For PGP
//
//                            //.splineTo(new Vector2d(46, -35), 0) //For GPP
//
//                        // Travel to scoring
//
//                            .setTangent(Math.toRadians(180))
//                            .splineTo(new Vector2d(20, 20), Math.toRadians(45))
//                            .strafeTo(new Vector2d(firing_position_x, firing_position_y))
//
//                        // Park outside scoring zone
//
//                            .lineToSplineHeading(new Pose2d(15, -40, Math.toRadians(0)))

//                        .strafeToLinearHeading(new Vector2d(12, -50), Math.toRadians(-90))
//                        .strafeToLinearHeading(new Vector2d(12, -45), Math.toRadians(90))
                        //.splineToConstantHeading(new Vector2d(40, 13), Math.toRadians(0))
                        //.splineToSplineHeading(new Pose2d(48, 13, Math.toRadians(90)), Math.toRadians(0))
                        //.splineTo(new Vector2d(30, 0), 0)
                        .splineTo(new Vector2d(46, 13), 0)

                        //run intake to pick up balls

                        .waitSeconds(0.5f)
                        .setTangent(Math.toRadians(180))
                            .splineTo(new Vector2d(30, 30), Math.toRadians(45))
                        .strafeTo(new Vector2d(firing_position_x, firing_position_y))

                        .build());

        //This is the custom field setup. To see the field PNGs, there is a file in Meepmeep with images, called Field_Backgrounds
        Image img = null;
        try {

            //You can choose a theme by commenting and uncommenting any of the field backgrounds
            //IF YOU GET ANY MEEP MEEP ERRORS TRYING TO RUN THIS CHANGE THE FILE PATH!!!! (=
//            img = ImageIO.read(new File("MeepMeep/Field_Backgrounds/Juice-DECODE-Black.png"));
            img = ImageIO.read(new File("MeepMeep/Field_Backgrounds/Juice-DECODE-Dark.png"));
//            img = ImageIO.read(new File("MeepMeep/Field_Backgrounds/Juice-DECODE-Light.png"));
//            img = ImageIO.read(new File("MeepMeep/Field_Backgrounds/Juice-DECODE-Paper.png"));

        } catch(IOException e) {}

        meepMeep.setBackground(img)
                .setDarkMode(true)
                .setBackgroundAlpha(0.95f)
                .addEntity(myBot)
                .start();
    }
}