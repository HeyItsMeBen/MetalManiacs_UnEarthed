package com.example.meepmeep.Sim;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;

import org.rowlandhall.meepmeep.MeepMeep;
import org.rowlandhall.meepmeep.roadrunner.DefaultBotBuilder;
import org.rowlandhall.meepmeep.roadrunner.entity.RoadRunnerBotEntity;

import java.awt.Image;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class RedFar {
    public static void main(String[] args) {

        MeepMeep meepMeep = new MeepMeep(800);

        RoadRunnerBotEntity myBot = new DefaultBotBuilder(meepMeep)
                .setConstraints(60, 30, Math.toRadians(180), Math.toRadians(180), 18)

                .followTrajectorySequence(drive -> drive.trajectorySequenceBuilder(new Pose2d(12, -60, Math.toRadians(0)))

                        .strafeTo(new Vector2d(12, -45))
                        .waitSeconds(0.5f)

                        //collect artifacts
                        .splineTo(new Vector2d(60, -60), Math.toRadians(0))
                        .waitSeconds(0.5f)

                        //move to firing position
                        .setReversed(true)
                        .splineToConstantHeading(new Vector2d(12, -45), Math.toRadians(180))
                        .waitSeconds(0.5f)

                        //collect artifacts
                        .setReversed(false)
                        .splineTo(new Vector2d(60, -55), Math.toRadians(0))
                        .waitSeconds(0.5f)

                        //move to firing position
                        .setReversed(true)
                        .splineToConstantHeading(new Vector2d(12, -45), Math.toRadians(180))
                        .waitSeconds(0.5f)

                        //park
                        .setReversed(false)
                        .splineTo(new Vector2d(35,-55), Math.toRadians(0))
                        .waitSeconds(2f)

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