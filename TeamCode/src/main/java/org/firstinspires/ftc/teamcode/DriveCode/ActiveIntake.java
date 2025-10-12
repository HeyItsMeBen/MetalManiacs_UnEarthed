package org.firstinspires.ftc.teamcode.DriveCode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class
ActiveIntake{
    private DcMotor intakeWheels;
    public ActiveIntake(HardwareMap hMap) {
        intakeWheels = hMap.get(DcMotor.class, "intake"); //added 7/24/24
        intakeWheels.setDirection(DcMotorSimple.Direction.REVERSE);
    }
    public void setMotorPower(double dblPower){
        intakeWheels.setPower(dblPower);
    }
}
