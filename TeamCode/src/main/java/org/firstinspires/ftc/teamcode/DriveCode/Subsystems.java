package org.firstinspires.ftc.teamcode.DriveCode;

import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.arcrobotics.ftclib.gamepad.GamepadKeys;

import org.firstinspires.ftc.teamcode.Hardware.Intake;
import org.firstinspires.ftc.teamcode.Hardware.Transfer;

public class Subsystems {

    private Intake intaker;
    private Transfer belt;

    private Transfer trapdoor;

    public Subsystems(Intake intaker, Transfer belt, Transfer trapdoor) {

        this.intaker = intaker;
        this.belt = belt;
        this.trapdoor = trapdoor;

    }

    public void updateSubsystems(GamepadEx driver, GamepadEx operator) {

        if (driver.getButton(GamepadKeys.Button.LEFT_BUMPER)) {
            intaker.setIntakePower(0.5);
        }

        if (driver.getButton(GamepadKeys.Button.RIGHT_BUMPER)) {
            intaker.setIntakePower(-0.5);

        } else {
            intaker.setIntakePower(0);
        }

    }
}

