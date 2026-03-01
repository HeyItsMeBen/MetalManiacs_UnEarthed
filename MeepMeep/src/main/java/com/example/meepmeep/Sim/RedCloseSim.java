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

public class RedCloseSim {
    public static void main(String[] args) {

        MeepMeep meepMeep = new MeepMeep(800);

        RoadRunnerBotEntity myBot = new DefaultBotBuilder(meepMeep)
                .setConstraints(60, 30, Math.toRadians(180), Math.toRadians(180), 18)

                .followTrajectorySequence(drive -> drive.trajectorySequenceBuilder(new Pose2d(52, 52, Math.toRadians(40)))

                        .setReversed(true)
                        .splineToSplineHeading(new Pose2d(15, 10, Math.toRadians(0)), Math.toRadians(270))
                        .waitSeconds(0.5f)

                        //picks up from middle row of balls
                        .setReversed(false)
                        .splineToConstantHeading(new Vector2d(30,-12), Math.toRadians(0))
                        .splineToConstantHeading(new Vector2d(50, -12), Math.toRadians(0))
                        .waitSeconds(0.25f)

                        //get in position to shoot
                        .setReversed(true)
                        .splineToSplineHeading(new Pose2d(15, 10, Math.toRadians(0)), Math.toRadians(90))
                        .waitSeconds(0.5f)

                        //get balls from gate
                        .setReversed(false)
                        .splineToSplineHeading(new Pose2d(58,5, Math.toRadians(90)), Math.toRadians(0))
                        .waitSeconds(0.5f)

                        //get in position to shoot
                        .setReversed(true)
                        .splineToSplineHeading(new Pose2d(15, 10, Math.toRadians(0)), Math.toRadians(90))
                        .waitSeconds(0.5f)

                        //picks up balls from the top
                        .setReversed(false)
                        .splineToSplineHeading(new Pose2d(50,12, Math.toRadians(0)), Math.toRadians(0))
                        .waitSeconds(0.25f)

                        //get in position to shoot
                        .setReversed(true)
                        .splineToSplineHeading(new Pose2d(15, 10, Math.toRadians(0)), Math.toRadians(90))
                        .waitSeconds(0.5f)

                        //pick up balls from the bottom
                        .setReversed(false)
                        .splineToConstantHeading(new Vector2d(37, -35), Math.toRadians(0))
                        .splineToConstantHeading(new Vector2d(50, -35), Math.toRadians(0))
                        .waitSeconds(0.5f)

                        //get in position to shoot
                        .setReversed(true)
                        .splineToSplineHeading(new Pose2d(15, 10, Math.toRadians(0)), Math.toRadians(90))
                        .waitSeconds(0.5f)

                        //park
                        .setReversed(false)
                        .splineTo(new Vector2d(45,6), Math.toRadians(0))
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