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

    public void getTurretPosition(){
        turretMotor.getCurrentPosition();
    }

    public void resetPosition(){
        int targetPosition = 0;
        turretMotor.setTargetPosition(targetPosition);
        turretMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        turretMotor.setPower(0.5);
    }

    public void resetInitial(){
        turretMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        turretMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

    }
}
