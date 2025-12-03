package org.firstinspires.ftc.teamcode.Hardware;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Turret {
    private DcMotorEx turretMotor;
    public Turret(HardwareMap hMap) {
        turretMotor = hMap.get(DcMotorEx.class, "turret"); //added 7/24/24
        turretMotor.setDirection(DcMotorSimple.Direction.FORWARD);
    }
    public void setMotorPower(double dblPower){
        turretMotor.setPower(dblPower);
    }
}
