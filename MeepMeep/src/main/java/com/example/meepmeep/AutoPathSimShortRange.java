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

public class AutoPathSimShortRange {
    public static void main(String[] args) {

        MeepMeep meepMeep = new MeepMeep(800);

        RoadRunnerBotEntity myBot = new DefaultBotBuilder(meepMeep)
                // Set bot constraints: maxVel, maxAccel, maxAngVel, maxAngAccel, track width
                .setConstraints(60, 30, Math.toRadians(180), Math.toRadians(180), 18)

                //.followTrajectorySequence(drive -> drive.trajectorySequenceBuilder(new Pose2d(15, -60, Math.toRadians(270)))
                .followTrajectorySequence(drive -> drive.trajectorySequenceBuilder(new Pose2d(52, 52, Math.toRadians(315)))

//                        .setReversed(true)
//                        .splineTo(new Vector2d(20, -30), Math.toRadians(90))

                        .setReversed(true)
                        .splineToConstantHeading(new Vector2d(30, 50), Math.toRadians(225))

                        .splineToSplineHeading(new Pose2d(18, 18, Math.toRadians(0)), Math.toRadians(270))

//                        .strafeTo(new Vector2d(18, -20))
//                        .splineToConstantHeading(new Vector2d(50, -35), Math.toRadians(0))
//
//                        .setReversed(true)
//                        .splineToConstantHeading(new Vector2d(18, 18), Math.toRadians(90))
//                        .setReversed(false)

                        .strafeTo(new Vector2d(18, 0))
                        .splineToConstantHeading(new Vector2d(50, -12), Math.toRadians(0))

                        .setReversed(true)
                        .splineToConstantHeading(new Vector2d(18, 18), Math.toRadians(90))
                        .setReversed(false)

                        .splineTo(new Vector2d(50,12), Math.toRadians(0))

                        .strafeTo(new Vector2d(45,8))
                        .splineToConstantHeading(new Vector2d(55,5), Math.toRadians(0))

                        .setReversed(true)
                        .splineToConstantHeading(new Vector2d(18, 18), Math.toRadians(90))
                        .setReversed(false)

                        .splineToConstantHeading(new Vector2d(20, -30), Math.toRadians(270))

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