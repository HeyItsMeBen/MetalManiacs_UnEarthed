package org.firstinspires.ftc.teamcode.Hardware;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class OuttakeHood {
    private Servo hoodServo;
    private double zeroPosition=0;
    double rangeOfMotion=0.75;  //0.75 means that the servo can travel 3/4 of a whole circle. 0.75 is a temporary theoretical value.
    public OuttakeHood(HardwareMap hMap) {
        hoodServo = hMap.get(Servo.class, "hood"); //added 7/24/24
    }
    public void setAngle(double radians){
        double hoodServoPosition=radians/(Math.PI*2)/rangeOfMotion+zeroPosition;    //translates radian into a number between 0 and 1. Then it translates that into a servo position using the rangeOfMotion and zeroPosition.
        if (hoodServoPosition>=0 && hoodServoPosition<=1){
            hoodServo.setPosition(hoodServoPosition);
        }
    }
    public void setServoPosition(double position){
        hoodServo.setPosition(position);
    }
    public void resetZeroPosition(){  //sets the variable "zeroPosition" to the position that the servo is currently at.
        zeroPosition=hoodServo.getPosition();   //zeroPosition is the position that the servo is at, when the hood is all the way down. It should be 0, but it might not be in some scenarios.
    }
}
