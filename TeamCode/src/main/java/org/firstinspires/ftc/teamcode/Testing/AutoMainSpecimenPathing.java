package org.firstinspires.ftc.teamcode.Testing;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Roadrunner.MecanumDrive;
import org.firstinspires.ftc.teamcode.Roadrunner.tuning.TuningOpModes;
//import org.firstinspires.ftc.teamcode.Hardware.Slides_PID;
//import org.firstinspires.ftc.teamcode.Hardware.outtakeArm;

@Disabled

@Autonomous(name = "QT3 specimenPathing", group = "Linear OpMode")
public final class AutoMainSpecimenPathing extends LinearOpMode {
   double currentTileSize=70.5625/3;//divide by 3 cuz 70 is the size for half a field
    double referenceTileSize=70/3;//(reference tile should be MeepMeep)
    double currentBotLength=17;
    double referenceBotLength=17;
    //Notes: 71.125(ourFieldSize), 70.5in(compFieldSize), 70in(MeepMeep)
    double MeepMeepTileCompensation=currentTileSize/referenceTileSize;   //multiply this by every movement of the wheels (besides rotation ofc)
    //double interactionCompensation=(currentTileSize-referenceTileSize)/2+(currentBotLength/2-referenceBotLength/2);    //applicable when bot interacts with something at it's side, rather than at it's center (eg: starting, scoring(usually), grabbing(usually). This happens cuz when field size changes and bot size stays constant, the field size to bot size ratio changes. Ask me for clarification if needed.
    double currentInteractionDistance=(currentTileSize/2)-(currentBotLength/2);
    double referenceInteractionDistance=(referenceTileSize/2)-(referenceBotLength/2);
    double interactionCompensation=currentInteractionDistance-referenceInteractionDistance;

    //double botSizeWhenGrabFromWall=17+3.375;
    //double botSizeWhenScoreSpecimen=19.86; //placeholder value. Likely NOT ACCURATE
    double botSizeWhenGrabFromWall=17;
    double botSizeWhenScoreSpecimen=17; //placeholder value. Likely NOT ACCURATE


    Servo intakeClaw;
    Servo outtakeClaw;
    //Slides_PID slides;
    //outtakeArm outtakeArmServos;
    //Servo servo=hardwareMap.get(Servo.class, "servo");
    @Override
    public void runOpMode() throws InterruptedException {
        Pose2d beginPose = new Pose2d((13.93500-0.1875006) * MeepMeepTileCompensation+interactionCompensation, (-62+0.1875006) * MeepMeepTileCompensation-interactionCompensation, Math.toRadians(270));
        intakeClaw= hardwareMap.get(Servo.class, "intakeClawServo");
        outtakeClaw= hardwareMap.get(Servo.class, "outtakeServo");
        //slides = new Slides_PID(hardwareMap);
        //outtakeArmServos = new outtakeArm(hardwareMap);
        if (TuningOpModes.DRIVE_CLASS.equals(MecanumDrive.class)) {
            MecanumDrive drive = new MecanumDrive(hardwareMap, beginPose);
            //outtakeClaw.setPosition(0);
            waitForStart();
            Actions.runBlocking(
                    drive.actionBuilder(beginPose)
                            //go to scoring position
                            .stopAndAdd(new setServos(hardwareMap, 0, 0, 0.8))
                            //hang preloaded sample
                            .strafeTo(new Vector2d((5) * MeepMeepTileCompensation, (-39.5) * MeepMeepTileCompensation))

                            .stopAndAdd(new scoreSpecimenPart1(hardwareMap))
                            .stopAndAdd(new setServos(hardwareMap, 0, 0.035, 0.5))
                            .waitSeconds(1)
                            //.stopAndAdd(new setServos(hardwareMap, 0, 0, outtakeArmServos.prepSpecimen))
                            .waitSeconds(1)
                            .strafeTo( new Vector2d((5) * MeepMeepTileCompensation, (-32.75-0.1875006) * MeepMeepTileCompensation+getNewInteractionvalue(botSizeWhenScoreSpecimen)))    //scores
                            .stopAndAdd(new scoreSpecimenPart2(hardwareMap))                                                //releases
                            .strafeTo( new Vector2d((5) * MeepMeepTileCompensation, (-46.5) * MeepMeepTileCompensation))

                            .stopAndAdd(new scoreSpecimenPart3(hardwareMap))

                            .splineTo(new Vector2d((20)* MeepMeepTileCompensation, (-53.5)* MeepMeepTileCompensation), Math.toRadians(Math.PI/2))
                            .splineTo(new Vector2d((35)* MeepMeepTileCompensation, (-12.5)* MeepMeepTileCompensation), Math.toRadians(90))


                            //push first sample
                            .strafeTo(new Vector2d((45) * MeepMeepTileCompensation, (-12.5) * MeepMeepTileCompensation))
                            .strafeTo(new Vector2d((45) * MeepMeepTileCompensation, (-50.5) * MeepMeepTileCompensation))

                            //push second sample

                            //score specimen
                            //grab specimen from wall 1
                            .stopAndAdd(new grabSpecimenFromWallPart1(hardwareMap))
                            .waitSeconds(1) //Human get ready!
                            .strafeTo(new Vector2d((45) * MeepMeepTileCompensation, (-55.25+0.1875006) * MeepMeepTileCompensation-getNewInteractionvalue(botSizeWhenGrabFromWall)))  //-70+11.5-->-50-->
                            .stopAndAdd(new grabSpecimenFromWallPart2(hardwareMap))
                            .strafeTo(new Vector2d((45) * MeepMeepTileCompensation, (-49.5) * MeepMeepTileCompensation))  //-70+11.5-->-50-->

                            //move to bar and score
                            .turnTo(Math.toRadians(0))//180-->0
                            .setTangent(Math.toRadians(180))
                            .splineTo( new Vector2d((5-4) * MeepMeepTileCompensation, (-39.5) * MeepMeepTileCompensation), Math.toRadians(90))
                            .stopAndAdd(new scoreSpecimenPart1(hardwareMap))
                            .stopAndAdd(new setServos(hardwareMap, 0, 0.035, 0.5))
                            .waitSeconds(0.75)
                            //.stopAndAdd(new setServos(hardwareMap, 0, 0, outtakeArmServos.prepSpecimen))
                            .strafeTo( new Vector2d((5-4) * MeepMeepTileCompensation, (-31.25-0.1875006) * MeepMeepTileCompensation+getNewInteractionvalue(botSizeWhenScoreSpecimen)))     //scores //-4-->0
                            .stopAndAdd(new scoreSpecimenPart2(hardwareMap))                                                //releases
                            .strafeTo( new Vector2d((5-4) * MeepMeepTileCompensation, (-46.5) * MeepMeepTileCompensation))

                            .waitSeconds(30)

                            //grab specimen from wall 2
                            .strafeToLinearHeading(new Vector2d((45) * MeepMeepTileCompensation, (-45) * MeepMeepTileCompensation), Math.toRadians(90))  //get out of observation zone    //-47
                            .waitSeconds(1) //Human get ready!
                            .strafeTo(new Vector2d((45) * MeepMeepTileCompensation, (-55.25+0.1875006) * MeepMeepTileCompensation-getNewInteractionvalue(botSizeWhenGrabFromWall)))  //-70+11.5-->-50-->
                            .stopAndAdd(new grabSpecimenFromWallPart2(hardwareMap))
                            .waitSeconds(4) //(temporary until i get arm working)
                            .strafeTo(new Vector2d((45) * MeepMeepTileCompensation, (-49.5) * MeepMeepTileCompensation))  //-70+11.5-->-50-->

                            //move to bar and score
                            .turnTo(Math.toRadians(0))//180-->0
                            .setTangent(Math.toRadians(180))
                            .splineTo( new Vector2d((5-8) * MeepMeepTileCompensation, (-39.5) * MeepMeepTileCompensation), Math.toRadians(90))
                            .stopAndAdd(new scoreSpecimenPart1(hardwareMap))
                            .waitSeconds(4)
                            .stopAndAdd(new setServos(hardwareMap, 0, 0.035, 0.5))
                            .waitSeconds(1)
                            //.stopAndAdd(new setServos(hardwareMap, 0, 0, outtakeArmServos.prepSpecimen))
                            .strafeTo( new Vector2d((5-8) * MeepMeepTileCompensation, (-31.25-0.1875006) * MeepMeepTileCompensation+getNewInteractionvalue(botSizeWhenScoreSpecimen)))     //scores //-4-->0
                            .stopAndAdd(new scoreSpecimenPart2(hardwareMap))                                                //releases
                            .strafeTo( new Vector2d((5-8) * MeepMeepTileCompensation, (-46.5) * MeepMeepTileCompensation))
                            //end
                            .build());

        } else {
            throw new RuntimeException();
        }
    }
    public class scoreSpecimenPart1 implements Action {
        public scoreSpecimenPart1(HardwareMap hMap) {

        }
        //@Override
        public boolean run(@NonNull TelemetryPacket telemetryPacket) {
            //setOuttakeArmPosition(outtakeArmServos.prepSpecimen);
            setSlidesTarget(875, 0.75);    //875-->1000
            return false;
        }
    }
    public class scoreSpecimenPart2 implements Action {
        public scoreSpecimenPart2(HardwareMap hMap) {}
        //@Override
        public boolean run(@NonNull TelemetryPacket telemetryPacket) {
            setOuttakeClawPosition(0.2);         //release specimen
            return false;
        }
    }
    public class scoreSpecimenPart3 implements Action {
        public scoreSpecimenPart3(HardwareMap hMap) {}
        public boolean run(@NonNull TelemetryPacket telemetryPacket) {
            setSlidesTarget(0, 0.75);   //retract slides downward.
            //setOuttakeArmPosition(outtakeArmServos.standby);

            return false;
        }
    }
    public class setStandby implements Action {

        @Override
        public boolean run(@NonNull TelemetryPacket telemetryPacket) {
            //outtakeArmServos.setArmTarget(outtakeArmServos.standby);
            return false;
        }
    }
    public class setServos implements Action {
        double intakeClawPos;
        double outtakeClawPos;
        double outtakeArmPos;
        public setServos(HardwareMap hMap, double intakeClawPosition, double outtakeClawPosition, double outtakeArmPosition) {
            intakeClawPos=intakeClawPosition;
            outtakeClawPos=outtakeClawPosition;
            outtakeArmPos=outtakeArmPosition;
        }

        @Override
        public boolean run(@NonNull TelemetryPacket telemetryPacket) {
            intakeClaw.setPosition(intakeClawPos);
            outtakeClaw.setPosition(outtakeClawPos);
            //outtakeArmServos.setArmTarget(outtakeArmPos);
            return false;
        }
    }
    //WARNING commented method is not finished and is not ready for use. DO NOT USE
    public class grabSpecimenFromWallPart1 implements Action {
        public grabSpecimenFromWallPart1(HardwareMap hMap) {}
        @Override
        public boolean run(@NonNull TelemetryPacket telemetryPacket) {
            //setOuttakeArmPosition(outtakeArmServos.grabFromWall);
            return false;
        }
    }
    public class grabSpecimenFromWallPart2 implements Action {
        public grabSpecimenFromWallPart2(HardwareMap hMap) {}
        @Override
        public boolean run(@NonNull TelemetryPacket telemetryPacket) {
            setOuttakeClawPosition(0);
            return false;
        }
    }

    public void setSlidesTarget(double target, double seconds){
        ElapsedTime timer;
        timer=new ElapsedTime();

        while (timer.seconds()<seconds && opModeIsActive()) {
            //slides.setSlidesTarget(target);
        }
    }
    public void setOuttakeArmPosition(double position){
        ElapsedTime timer;
        timer=new ElapsedTime();
        //double estimatedTime=Math.abs((outtakeArmServos.getArmPosition()-position))*0.8;//intake claw took 0.35 seconds (roughly) to open all the way (by all the way i mean from 0 to 0.5). So, i rounded up to 0.4 and divided by half becuase we are not using full range of motion
        //outtakeArmServos.setArmTarget(position);
        //while (timer.seconds()<estimatedTime && opModeIsActive()){
            //empty loop. Keeps running until actual position reaches target position
        //}
    }
    public void setOuttakeClawPosition(double position){
        ElapsedTime timer;
        timer=new ElapsedTime();
        double estimatedTime=Math.abs((outtakeClaw.getPosition()-position))*0.8;//intake claw took 0.35 seconds (roughly) to open all the way (by all the way i mean from 0 to 0.5). So, i rounded up to 0.4 and multiplied by 2 becuase we are not using full range of motion
        outtakeClaw.setPosition(position);
        while (timer.seconds()<estimatedTime && opModeIsActive()){
            //empty loop. Keeps running until actual position reaches target position
        }
    }
    public void setIntakeClawPosition(double position){
        ElapsedTime timer;
        timer=new ElapsedTime();
        double estimatedTime=Math.abs((intakeClaw.getPosition()-position))*0.8;//intake claw took 0.35 seconds (roughly) to open all the way (by all the way i mean from 0 to 0.5). So, i rounded up to 0.4 and divided by half becuase we are not using full range of motion
        intakeClaw.setPosition(position);
        while (timer.seconds()<estimatedTime && opModeIsActive()){
            //empty loop. Keeps running until actual position reaches target position
        }
    }
    double getNewInteractionvalue(double newRefBotLength){
        double newCurrentBotLength=newRefBotLength*(currentBotLength/referenceBotLength);
        double newCurrentInteractionDistance=(currentTileSize/2)-(newCurrentBotLength-currentBotLength/2); //calculates distance from the center of the bot to the edge of the claw(or whatever “interact
        double newReferenceInteractionDistance=(referenceTileSize/2)-(newRefBotLength-referenceBotLength/2);
        double newInteractionCompensation=newCurrentInteractionDistance-newReferenceInteractionDistance;
        return newInteractionCompensation;
    }
}
