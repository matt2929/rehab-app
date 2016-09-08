package com.example.matthew.rehabfirsttry.UserWorkouts;

import android.content.Context;

import com.example.matthew.rehabfirsttry.Utilities.GripAnalysis;
import com.example.matthew.rehabfirsttry.Utilities.WorkoutShakeTrack;

/**
 * Created by Matthew on 7/27/2016.
 */
public class TwistCup implements WorkoutSession {

    int getPickupCountMax = 10;
    float lastDifference = 0;
    float countAccuracyLastVal = 8;
    int pickupCount = 0;
    long startTime = System.currentTimeMillis();
    String whatToSay = "";
    boolean shouldITalk = false;
    double lastSlope = 0;
    double collisionNumber = 0;

    float lastGyroZ = 0;
    GripAnalysis gripAnalysis = new GripAnalysis();
    WorkoutShakeTrack workoutShakeTrack = new WorkoutShakeTrack();
    public TwistCup() {

    }



    @Override
    public void dataIn(float accX, float accY, float accZ, float gravX, float gravY, float gravZ, int walkingCount, Context context) {
        workoutShakeTrack.analyseData(accX, accY, accZ);
        float differenceVAL = Math.abs(gravX - lastGyroZ);
        lastGyroZ = gravX;
        long nowTime = System.currentTimeMillis();

        holdAccuracy(accX, accY, accZ);

        if (differenceVAL < -3 && lastDifference > -3&&(Math.abs(nowTime-startTime))>1000) {
            shouldITalk = true;
            pickupCount++;
            whatToSay = "" + pickupCount;
            startTime=System.currentTimeMillis();
        }
        lastDifference = differenceVAL;
    }

    @Override
    public boolean workoutFinished() {
        if (pickupCount == getPickupCountMax) {
            return true;
        }
        return false;
    }

    @Override
    public String result() {
        return "\n\n\nTwisted up " + pickupCount + " time(s).";
    }

    @Override
    public boolean saveGripPosition(float accX, float accY, float accZ) {
        return gripAnalysis.saveGripPosition(accX, accY, accZ);

    }

    @Override
    public void holdAccuracy(float accX, float accY, float accZ) {

        float accTotal = (float) Math.pow((Math.pow(accX, 2) + Math.pow(accY, 2) + Math.pow(accZ, 2)), .5);


        double slope = accTotal - countAccuracyLastVal;

        if (((lastSlope > 0 && slope < 0)) && countAccuracyLastVal >= 10) {
            collisionNumber++;
        }
        countAccuracyLastVal = accTotal;
        lastSlope = slope;
    }

    @Override
    public boolean shouldISaySomething() {
        return shouldITalk;
    }

    @Override
    public int[] ShakeNum() {
        return workoutShakeTrack.getShakeCount();
    }


    @Override
    public String whatToSay() {

        shouldITalk = false;
        return whatToSay;
    }

    @Override
    public int getGrade() {
        return 0;
    }

    @Override
    public String saveData() {
        String returnMe = "\n" +
                "---Watch Positional Average---\n" +
                "-X: " + gripAnalysis.getGripXGravMean() + "-\n" +
                "-Y: " + gripAnalysis.getGripYGravMean() + "-\n" +
                "-Z: " + gripAnalysis.getGripZGravMean() + "-\n" +
                "-------------------------------";
        return returnMe;
    }

    @Override
    public String getWorkoutName() {
        return "Twist Count";
    }

    @Override
    public int xPostiontDisplay() {
        return 0;
    }

    @Override
    public int yPostiontDisplay() {
        return 10;
    }

    @Override
    public double accuracyProgression() {
        return gripAnalysis.getGripAnalysisProgression();
    }

    @Override
    public String sayHowToHoldCup() {
        return "Hold the cup in your left hand. Twist your wrist clockwise than return.";

    }
}
