package org.firstinspires.ftc.teamcode.DriveCode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class ActiveIntake{
    private CRServo intakeWheels;
    public ActiveIntake(HardwareMap hMap) {
        intakeWheels = hMap.get(CRServo.class, "intakeWheels"); //added 7/24/24
    }
    public void setMotorPower(double dblPower){
        intakeWheels.setPower(dblPower);
    }
}
