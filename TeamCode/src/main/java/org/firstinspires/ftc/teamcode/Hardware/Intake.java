package org.firstinspires.ftc.teamcode.Hardware;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Intake {
    private DcMotor intakeWheels;

    public Intake(HardwareMap hMap) {
        intakeWheels = hMap.get(DcMotor.class, "intake"); //added 7/24/24
        intakeWheels.setDirection(DcMotorSimple.Direction.REVERSE);
    }
    public void setIntakePower(double dblPower){

        intakeWheels.setPower(dblPower);
    }

    public void runIntakeFullPower(){
        intakeWheels.setPower(1.0);
    }

    public void maintainIntakePower(){
        intakeWheels.setPower(0.5);
    }

    public void reverseIntake(){
        intakeWheels.setPower(-0.75);
    }

    public void stopIntake() {
        intakeWheels.setPower(0);
    }
}
