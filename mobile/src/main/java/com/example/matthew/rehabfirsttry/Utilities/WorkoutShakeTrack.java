package com.example.matthew.rehabfirsttry.Utilities;

/**
 * Created by Matthew on 8/15/2016.
 */
public class WorkoutShakeTrack {

    int[] shakeCount = new int[4];
    float smallShake=.2f, mediumShake=.8f, hardShake =1.2f;
    float lastX=-111,lastY=-111,lastZ=-111;
    public void analyseData(float accX, float accY, float accZ) {
        float diffX=(Math.abs(accX-lastX)),diffY=(Math.abs(accY-lastY)),diffZ=(Math.abs(accZ-lastZ));
        if(lastX==-111) {

        }else {
            double AccMag = Math.pow(Math.pow(diffX, 2) + Math.pow(diffY, 2) + Math.pow(diffZ, 2), .5);
            if (AccMag > hardShake) {
                shakeCount[2] = shakeCount[2] + 1;
            } else if (AccMag > mediumShake) {
                shakeCount[1] = shakeCount[1] + 1;
            } else if (AccMag > smallShake) {
                shakeCount[0] = shakeCount[0] + 1;
            } else {

            }
        }
        lastX=accX;
        lastY=accY;
        lastZ=accZ;
    }

    public int[] getShakeCount() {
        int sum=0;
        for(int i=0;i<shakeCount.length-1;i++){
            sum+=shakeCount[i];
        }
        shakeCount[shakeCount.length-1]=sum;
        return shakeCount;
    }
}
