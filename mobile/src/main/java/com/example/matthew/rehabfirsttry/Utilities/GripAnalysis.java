package com.example.matthew.rehabfirsttry.Utilities;

import android.util.Log;

/**
 * Created by Matthew on 7/26/2016.
 */
public class GripAnalysis {

    float gripXGravSum = 0, gripYGravSum = 0, gripZGravSum = 0;
    float lastValGravX = 0, lastValGravY = 0, lastValGravZ = 0;
    float gripXGravMean, gripYGravMean, gripZGravMean;
    int gripCount = 0, gripCountMax = 4;

    public GripAnalysis() {

    }

    public boolean saveGripPosition(float gravX, float gravY, float gravZ) {
        float diffX = Math.abs(gravX - lastValGravX);
        float diffY = Math.abs(gravY - lastValGravY);
        float diffZ = Math.abs(gravZ - lastValGravZ);
        lastValGravX = gravX;
        lastValGravY = gravY;
        lastValGravZ = gravZ;
        double magDiff = Math.pow((Math.pow(diffX, 2) + Math.pow(diffY, 2) + Math.pow(diffZ, 2)), .5);
        Log.e("magDiff:", "X: " + diffX + "Y: " + diffY + "Z: " + diffZ + "mag: " + magDiff);

        if (magDiff < .4) {
            gripCount++;
            gripXGravSum += gravX;
            gripYGravSum += gravY;
            gripZGravSum += gravZ;
        } else {
            gripCount = 0;
            gripXGravSum += 0;
            gripYGravSum += 0;
            gripZGravSum += 0;
        }

        if (gripCount == gripCountMax) {
            gripXGravMean = gripXGravSum / (float) gripCount;
            gripYGravMean = gripYGravSum / (float) gripCount;
            gripZGravMean = gripZGravSum / (float) gripCount;
            return true;
        }
        return false;
    }

    public float getGripXGravMean() {
        return gripXGravMean;
    }

    public float getGripYGravMean() {
        return gripYGravMean;
    }

    public float getGripZGravMean() {
        return gripZGravMean;
    }

    public double getGripAnalysisProgression() {
        return (double)gripCount/(double)gripCountMax;
    }

}
