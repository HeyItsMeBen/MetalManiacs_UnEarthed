package org.firstinspires.ftc.teamcode.AutoCode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class AutoIntake {
    private DcMotor intakeWheels;
    public AutoIntake(HardwareMap hMap) {
        intakeWheels = hMap.get(DcMotor.class, "intake"); //added 7/24/24
        intakeWheels.setDirection(DcMotorSimple.Direction.REVERSE);
    }
    public void setMotorPower(double dblPower){
        intakeWheels.setPower(dblPower);
    }
}
