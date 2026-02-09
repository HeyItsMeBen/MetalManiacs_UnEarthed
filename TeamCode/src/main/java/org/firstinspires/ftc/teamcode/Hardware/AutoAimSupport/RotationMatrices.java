package org.firstinspires.ftc.teamcode.Hardware.AutoAimSupport;

//Note: This file has been moved from autocode ---> testing to prevent accidental deletion or confusion

public class RotationMatrices {
    public RotationMatrices(){

    }
    public double[] getActualYaw(double givenYaw, double givenPitch, double givenRoll, double cameraPitch){
        //Note: I set givenRoll to 0, and I moved givenPitch to the roll input. I did this cuz it works, when I don't do it, some weird stuff happens
        double [][] tagBodyFrame = {    //defeault frame, relative to the camera.
                {1, 0, 0},
                {0, 1, 0},
                {0, 0, 1}
        };
        tagBodyFrame = eulerAnglesToBodyFrame(givenYaw, 0, givenPitch, tagBodyFrame);  //turn tagBodyFrame (which is currently the default frame) into the body frame of the april tag. Input the tag's yaw, pitch, and roll here.
        tagBodyFrame = eulerAnglesToBodyFrame(0, 0, cameraPitch, tagBodyFrame);  //change the reference frame. From camera coordinate system, to the world coordinate system.
        double [] tagEulerAngles=RMatrixToEulerAngles(tagBodyFrame);    //get angles
        return tagEulerAngles;  //return angles (yaw, pitch, roll)
    }
    public static double[][] transposeMatrix(double [][] m){
        double[][] temp = new double[m[0].length][m.length];
        for (int i = 0; i < m.length; i++)
            for (int j = 0; j < m[0].length; j++)
                temp[j][i] = m[i][j];
        return temp;
    }
    public static double[][] multiplyMatrices(double[][] a, double[][] b) {
        int aRows = a.length;
        int aCols = a[0].length;
        int bCols = b[0].length;
        double[][] result = new double[aRows][bCols];
        for (int i = 0; i < aRows; i++) {
            for (int j = 0; j < bCols; j++) {
                for (int k = 0; k < aCols; k++) {
                    result[i][j] += a[i][k] * b[k][j];
                }
            }
        }
        return result;
    }
    public static void printMatrix(double[][] matrix) {
        for (double[] row : matrix) {
            for (double value : row) {
                System.out.printf("%10.4f ", value);
            }
            System.out.println();
        }
    }
    public static double [] RMatrixToEulerAngles(double [][] givenMatrix){  //turns the rotation matrix into yaw, pitch, and roll
        double yaw = Math.atan2(givenMatrix[1][0], givenMatrix[0][0]);
        double pitch = Math.atan2(-givenMatrix[2][0], Math.sqrt(Math.pow(givenMatrix[2][1], 2)+Math.pow(givenMatrix[2][2], 2)));
        double roll = Math.atan2(givenMatrix[2][1], givenMatrix[2][2]);
        double [] eulerAngles={yaw, pitch, roll};   //MIGHT NEED TO: add Math.PI/2 to the yaw to get a valid number
        return eulerAngles;
    }
    public static double [][] eulerAnglesToBodyFrame(double yaw, double pitch, double roll, double [][] bodyFrame){  //rotates the given body frame by the eulerAngles, returning an updated body fram
        //breaks down the given bodyFrame into x, y, and x axis,  which are all unit vectors.
        double [][] bodyFrameVector1 = new double[3][1];
        bodyFrameVector1[0][0] = bodyFrame[0][0];
        bodyFrameVector1[1][0] = bodyFrame[1][0];
        bodyFrameVector1[2][0] = bodyFrame[2][0];
        double [][] bodyFrameVector2 = new double[3][1];
        bodyFrameVector2[0][0] = bodyFrame[0][1];
        bodyFrameVector2[1][0] = bodyFrame[1][1];
        bodyFrameVector2[2][0] = bodyFrame[2][1];
        double [][] bodyFrameVector3 = new double[3][1];
        bodyFrameVector3[0][0] = bodyFrame[0][2];
        bodyFrameVector3[1][0] = bodyFrame[1][2];
        bodyFrameVector3[2][0] = bodyFrame[2][2];

        //rotates each vector seperately. Rotates them by the given euler angles.
        double [][] changedVector1 = changeReferenceFrame(yaw, pitch, roll, bodyFrameVector1);
        double [][] changedVector2 = changeReferenceFrame(yaw, pitch, roll, bodyFrameVector2);
        double [][] changedVector3 = changeReferenceFrame(yaw, pitch, roll, bodyFrameVector3);

        //puts together the axis (vectors), to make the new body frame
        double [][] changedBodyFrame = new double[3][3];
        changedBodyFrame[0][0] = changedVector1[0][0];
        changedBodyFrame[1][0] = changedVector1[1][0];
        changedBodyFrame[2][0] = changedVector1[2][0];
        changedBodyFrame[0][1] = changedVector2[0][0];
        changedBodyFrame[1][1] = changedVector2[1][0];
        changedBodyFrame[2][1] = changedVector2[2][0];
        changedBodyFrame[0][2] = changedVector3[0][0];
        changedBodyFrame[1][2] = changedVector3[1][0];
        changedBodyFrame[2][2] = changedVector3[2][0];
        return changedBodyFrame;
    }

    public static double [][] changeReferenceFrame(double theta1, double theta2, double theta3, double[][] givenVector){    //I named it kinda bad. It basically rotates the given vector by the euler angles, using rotation matrices.
        double [][]Rtheta1 = new double[3][3];
        double [][]Rtheta2 = new double[3][3];
        double [][]Rtheta3 = new double[3][3];

        //[1, 0, 0]
        //[0, cosθ3, -sinθ3]
        //[0, sinθ3, cosθ3]
        Rtheta3[0][0] = 1;
        Rtheta3[0][1] = 0;
        Rtheta3[0][2] = 0;
        Rtheta3[1][0] = 0;
        Rtheta3[1][1] = Math.cos(theta3);
        Rtheta3[1][2] = -Math.sin(theta3);
        Rtheta3[2][0] = 0;
        Rtheta3[2][1] = Math.sin(theta3);
        Rtheta3[2][2] = Math.cos(theta3);

        //[cosθ2, 0, sinθ2]
        //[0, 1, 0]
        //[ -sinθ2, 0, cosθ2
        Rtheta2[0][0] = Math.cos(theta2);
        Rtheta2[0][1] = 0;
        Rtheta2[0][2] = Math.sin(theta2);
        Rtheta2[1][0] = 0;
        Rtheta2[1][1] = 1;
        Rtheta2[1][2] = 0;
        Rtheta2[2][0] = -Math.sin(theta2);
        Rtheta2[2][1] = 0;
        Rtheta2[2][2] = Math.cos(theta2);

        //[cosθ1, -sinθ1, 0]
        //[sinθ1, cosθ1, 0]
        //[0, 0, 1]
        Rtheta1[0][0] = Math.cos(theta1);
        Rtheta1[0][1] = -Math.sin(theta1);
        Rtheta1[0][2] = 0;
        Rtheta1[1][0] = Math.sin(theta1);
        Rtheta1[1][1] = Math.cos(theta1);
        Rtheta1[1][2] = 0;
        Rtheta1[2][0] = 0;
        Rtheta1[2][1] = 0;
        Rtheta1[2][2] = 1;
        return multiplyMatrices(multiplyMatrices(multiplyMatrices(Rtheta1, Rtheta2), Rtheta3), givenVector);
    }
}
