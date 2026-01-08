package org.firstinspires.ftc.teamcode.Old_Code;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
@Deprecated
public class
Intake {
    private DcMotor intakeWheels;
    public Intake(HardwareMap hMap) {
        intakeWheels = hMap.get(DcMotor.class, "intake"); //added 7/24/24
        intakeWheels.setDirection(DcMotorSimple.Direction.REVERSE);
    }
    public void setMotorPower(double dblPower){
        intakeWheels.setPower(dblPower);
    }

    public void runIntakeFullPower() {

    }
}
