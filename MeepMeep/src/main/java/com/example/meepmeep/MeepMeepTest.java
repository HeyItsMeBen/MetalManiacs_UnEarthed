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

public class MeepMeepTest {
    public static void main(String[] args) {
        MeepMeep meepMeep = new MeepMeep(800);

        RoadRunnerBotEntity myBot = new DefaultBotBuilder(meepMeep)
                // Set bot constraints: maxVel, maxAccel, maxAngVel, maxAngAccel, track width
                .setDimensions(18,18)
                .setConstraints(60, 60, Math.toRadians(180), Math.toRadians(180), 15)
                .followTrajectorySequence(drive -> drive.trajectorySequenceBuilder(new Pose2d(0, 0, 0))
                        .splineTo(new Vector2d(30, 30), Math.PI / 2)
                        .splineTo(new Vector2d(0, 60), Math.PI)
                        .build());

//                        .strafeTo(new Vector2d(0, 50))
//                        .waitSeconds(1)
//                        .turn(Math.toRadians(90))
//                        .splineTo(new Vector2d(0, 30), 3)
//                        .splineTo(new Vector2d(0, 60), 0)

//                        .strafeTo(new Vector2d(50,50))
////                        .turn(Math.toRadians(90))
//                        .strafeTo(new Vector2d(50,0))
////                        .turn(Math.toRadians(90))
//                        .strafeTo(new Vector2d(0,0))


//                        .build());

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