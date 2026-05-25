package org.firstinspires.ftc.teamcode.Hardware;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;

public class Intake {
    private DcMotorEx intakeWheels;
    final double tickPerRevolution=28*5.2;

    double MAX_POWER = 0.7;

    public Intake(HardwareMap hMap) {
        intakeWheels = hMap.get(DcMotorEx.class, "intake"); //added 7/24/24
        intakeWheels.setDirection(DcMotorSimple.Direction.FORWARD);
    }
    public void setIntakePower(double dblPower){

        intakeWheels.setPower(dblPower);
    }
    public double getIntakePower(){
        return intakeWheels.getPower();
    }

    public double getIntakeMotorAmps() {
        return intakeWheels.getCurrent(CurrentUnit.AMPS);
    }

    public void runIntakeFullPower(){
        intakeWheels.setPower(MAX_POWER);
    }

    public void maintainIntakePower(){
        intakeWheels.setPower(MAX_POWER/2);
    }

    public void reverseIntake(){
        intakeWheels.setPower(-MAX_POWER);
    }

    public void stopIntake() {
        intakeWheels.setPower(0);
    }
    public double getIntakeMotorRPM(){
        return intakeWheels.getVelocity()*60/tickPerRevolution;
    }
}
