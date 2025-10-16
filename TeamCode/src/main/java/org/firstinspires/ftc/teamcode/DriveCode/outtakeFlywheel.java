package org.firstinspires.ftc.teamcode.DriveCode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

public class outtakeFlywheel {
    DcMotorEx flywheel; //creates "flywheel" variable, and sets as a "DcMotorEx"-type variable.
    public flywheelValues outtakeFlywheelValues;

    //double rpm=40;  //change the value to whatever u want

    //setting PID variables for later calculations
    double integralSum=0;
    double Kp=0;
    double Ki=0;
    double Kd=0;
    double Kf=0;
    public double lastError=0;


    final double tickPerRevolution=28;


    //calculation setup
    final double basketY_Og=toMeters(38.75+5.75); //basketY_Og should be equal to basketHeight + distanceFromArmBaseToGround
    double basketY=basketY_Og;
    //double basketLocationX=toMeters(60);
    double H = basketY+toMeters(7.5); //measured in meters. Max height that launched ball will reach. Change as desired.
    double gravity=9.8; //i think this is the right value
    double tagToGoalCenter_Distance=toMeters(5);
    double robotCenterToArmBase_Distance=toMeters(0.25);
    double cameraToRobotCenter_Distance=toMeters(8);

    double [] values= {0, 0};
    double[] armPositions={0, 0, 0};

    public double basketXForTelemetry;
    public double basketYForTelemetry;
    public double ballVelocity;


    ElapsedTime timer = new ElapsedTime();  //keeps track of time. Used for PID calculations
    public outtakeFlywheel(HardwareMap hMap){
        flywheel = hMap.get(DcMotorEx.class, "leftFlyWheel");    //connects the flywheel variable with the actual motor in the control hub
        flywheel.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);      //The video told me to type it...
        outtakeFlywheelValues = new flywheelValues();
    }
    //returns true when at full velocity
    /*public void flyWheelFullPower(float targetRPM, float targetTime) {
        ElapsedTime timer;
        timer = new ElapsedTime();
        while(true){
            flywheel.setPower(PIDControl(tickPerRevolution*(targetRPM/60), flywheel.getVelocity()));  //calculates and sets the needed power with PID. "tickPerRevolution*(rpm/60)" turns the input rpm into ticksPerSecond.
            if(flywheel.getVelocity() >= tickPerRevolution*(targetRPM/60) && timer.seconds() >= targetTime){
                break;
            }
        }
    }
    public double getPIDPower(double targetRPM){
        return PIDControl(tickPerRevolution*(targetRPM/60), flywheel.getVelocity());  //calculates and returns the needed power with PID. "tickPerRevolution*(rpm/60)" turns the input rpm into ticksPerSecond.
    }
    private double PIDControl(double reference, double state){  //This is where the magic happens. It does some weird math
        double error=reference-state;
        integralSum+=error*timer.seconds();
        double derivitive = (error-lastError) / timer.seconds();
        lastError=error;

        timer.reset();

        double output = (error*Kp)+(derivitive*Kd)+(integralSum*Ki)+(reference*Kf);
        return output;
    }*/
    public void setOuttakeVelocity(float givenRpm){
        flywheel.setVelocity(tickPerRevolution*(givenRpm/60));
    }
    private double toMeters(double inches){
        return inches/39.3700787;
    }


    public void calculateEverything(double givenX, double tagTilt, double tagElevation, double cameraPitch){ //this function changes the goalLocation from the AprilTag to the goalCenter. It also translates robotCenter into armBase so the rest of this file can calculate properly.
        double robotBaseX=givenX*Math.cos(tagElevation+cameraPitch);
        double newX=Math.sqrt(Math.pow(robotBaseX, 2)+Math.pow(tagToGoalCenter_Distance, 2)-2*robotBaseX*tagToGoalCenter_Distance*Math.cos(Math.PI-tagTilt));   //law of cosines. New X is equal to the distance from the robotBase to the goalCenter
        outtakeFlywheelValues.angleDeviation=Math.asin(tagToGoalCenter_Distance*Math.sin(Math.PI-tagTilt)/newX);    //law of sines
        getValues(newX-robotCenterToArmBase_Distance+cameraToRobotCenter_Distance);  //this input is equal to the distance from the armBase to goalCenter
    }
    public void getValues(double givenX){
        values=calculateValues(givenX);
        armPositions=findArmPosition(values);

        outtakeFlywheelValues.basketXTelemetryOg=givenX;
        outtakeFlywheelValues.basketYTelemetryOg=basketY;
        outtakeFlywheelValues.vertexHeightTelemetryOg=Math.pow(values[0], 2)*Math.pow(Math.sin(values[1]), 2)/(2*gravity);
        outtakeFlywheelValues.ballVelocityOg=values[0];
        outtakeFlywheelValues.launchAngleOg=values[1];

        basketY=basketY_Og-armPositions[1];
        values=calculateValues(givenX-armPositions[0]);

        double ballWeight=2;
        double wheelInertia=1;
        double neededEnergy=0.5*(2*wheelInertia+ballWeight)*Math.pow(values[0], 2);
        double wheelVelocity=Math.sqrt(neededEnergy/wheelInertia); //measured in meters per second (m/s)

        //double [] finalValues={wheelVelocity, values[1], armPositions[2]};  //wheelVelocity is the velocity the wheels need to be at. Values[1] is the theta angle needed for for launch. ArmPositions[2] is the distance the robot needs to move away from basket before launching.
        //these three values, in addition to angleDeviation, are the ones we will be directly using to launch the balls
        outtakeFlywheelValues.wheelVelocity=wheelVelocity;
        outtakeFlywheelValues.launchAngle=values[1];
        outtakeFlywheelValues.moveBackValue=armPositions[2];

        outtakeFlywheelValues.basketXTelemetry=givenX-armPositions[0];
        outtakeFlywheelValues.basketYTelemetry=basketY;
        outtakeFlywheelValues.vertexHeightTelemetry=Math.pow(values[0], 2)*Math.pow(Math.sin(values[1]), 2)/(2*gravity);
        outtakeFlywheelValues.ballVelocity=values[0];
    }
    private double [] calculateValues(double givenX){
        double ballRadius=toMeters(2.5);
        double basketX=givenX;      //basketX and basketY are calculating assuming origin (0, 0) is at the base of the launch arm. Basket Y will need to be adjusted based on robot height.
        double theta1=Math.atan((2*H/basketX)*(1+Math.sqrt(1-basketY/H)));
        double theta2=Math.atan((2*H/basketX)*(1-Math.sqrt(1-basketY/H)));
        double theta1Distance=2*H*(1/Math.tan(theta1));
        double theta2Distance=2*H*(1/Math.tan(theta2));
        double theta=0; //radians
        double [] values1={0, 0};
        boolean err=false;

        //Inertia calculations stuff. Currently unused
        /*double R1=0;
        double R2=0;
        double T1=0;
        double T2=0;
        double T3=0;
        double h1=0;
        double n=8;
        double rubberArea=0;
        double plasticArea=0;
        double rubberRotationInertia=0; //spokes and rings use DIFFERENT EQUATIONS. Add em together.
        double plasticRotationInertia=0;
        double wheelInertia=rubberRotationInertia+plasticRotationInertia;
         */

        if (theta1Distance<basketX){
            theta=theta1;
        }
        else if (theta2Distance<basketX){
            theta=theta2;
        }
        else {
            err=true;
        }

        double ballVelocity=Math.sqrt(2*gravity*H)/Math.abs(Math.sin(theta));
        //double neededEnergy = Math.pow(ballVelocity, 2)*(2*flywheelWeight+ballWeight);
        //double wheelVelocity=Math.sqrt(neededEnergy/(2*flywheelWeight)); //measured in meters per second (m/s)
        //double neededEnergy=0.5*(2*wheelInertia+ballWeight)*Math.pow(ballVelocity, 2);
        //double wheelVelocity=Math.sqrt(neededEnergy/wheelInertia); //measured in meters per second (m/s)

        if (!err && theta!=0) {
            values1[0]=ballVelocity;    //the velocity that wheels will need to be spinning at. CHANGE TO WHEEL_VELOCITY
            values1[1]=theta;            //the angle that the ball will need to be launched at
        }
        return values1;
    }









    //bisection stuff
    final double L_desired = 1720.0/39.3700787/39.3700787;
    double tanTheta = 0;
    double cosTheta = 0;
    double A = 0;
    private double[] findArmPosition(double[] values1){
        // Constants
        tanTheta = Math.tan(values1[1]);
        cosTheta = Math.cos(values1[1]);
        A = gravity / (2 * values1[0] * values1[0] * cosTheta * cosTheta);

        // Find vertex x
        double xVertex = tanTheta / (2 * A);
        //telemetry.addData("Vertex x_v =", xVertex.toFixed(4));

        // We’ll search on the left of the vertex
        // Adjusted until we find a root with positive slope
        double xLow = -0.01;
        double xHigh = xVertex - 0.01;
        double x_t = bisection(xLow, xHigh, 1e-6, 1000);

        double y_t = y(x_t);
        double m = mOf(x_t);
        double x0 = x_t - y_t / m;
        double L = Math.hypot(x_t - x0, y_t);

        //if (m <= 0){telemetry.addLine("Warning: tangent slope not positive; adjust search range!");}

        //telemetry.addLine("Tangent point (x_t, y_t): "+x_t+", "+y_t);
        //telemetry.addLine("Endpoint (x0, 0). x0: "+ x0+", 0");
        //telemetry.addData("Slope m: ", m);
        //telemetry.addData("Segment length: ", L);
        //telemetry.update();
        double [] armPosInfo={x_t, y_t, x0};
        return armPosInfo;
    }







    // Parabola
    double y(double x) {
        return x * tanTheta - A * x * x;
    }

    // Derivative (slope of tangent)
    double mOf(double x) {
        return tanTheta - 2 * A * x;
    }

    // Function to solve: f(x) = segmentLength - desiredLength
    double f(double x) {
        double y_t = y(x);
        double m = mOf(x);
        if (Math.abs(m) < 1e-9) {return 9999999;} // avoid division by 0
        double x0 = x - y_t / m;
        double L = Math.hypot(x - x0, y_t);
        return L - L_desired;
    }

    // Bisection method
    double bisection(double xLow, double xHigh, double tol, int maxIter) {
        double fLow = f(xLow);
        double fHigh = f(xHigh);
        if (fLow * fHigh > 0)
            throw new Error("Function has same signs at interval ends.");

        for (int i = 0; i < maxIter; i++) {
            double mid = 0.5 * (xLow + xHigh);
            double fMid = f(mid);
            if (Math.abs(fMid) < tol) return mid;
            if (fLow * fMid < 0) {
                xHigh = mid;
                fHigh = fMid;
            } else {
                xLow = mid;
                fLow = fMid;
            }
        }
        return 0.5 * (xLow + xHigh);
    }
    public class flywheelValues{
        //main 4 values
        public double wheelVelocity=0;
        public double launchAngle=0;
        public double moveBackValue=0;
        public double angleDeviation=0;

        //other values (for telemetry)
        public double basketXTelemetry=0;
        public double basketYTelemetry=0;
        public double vertexHeightTelemetry=0;
        public double ballVelocity=0;

        //the og's (values from first usage of calculateValues)
        public double basketXTelemetryOg=0;
        public double basketYTelemetryOg=0;
        public double vertexHeightTelemetryOg=0;
        public double ballVelocityOg=0;
        public double launchAngleOg=0;

        public flywheelValues(){

        }
    }
}