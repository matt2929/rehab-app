package com.example.matthew.rehabfirsttry.UserWorkouts;

import android.content.Context;

import com.example.matthew.rehabfirsttry.Utilities.GripAnalysis;
import com.example.matthew.rehabfirsttry.Utilities.WorkoutShakeTrack;

/**
 * Created by Matthew on 7/12/2016.
 */
public class PickUpHold implements WorkoutSession {

    float countPickupLastVal = 10;

    Long startTime = Long.valueOf(0);
    String whatToSay = "";
    boolean shouldITalk = false;
    double lastVal = 9;
    long workoutAnnounceCheckpoint = 25000;
    long val = 0;
    boolean workoutStarted = false;
    boolean pickupAnnounce = false;
    int total = 0;
    int errors = 0;
    GripAnalysis gripAnalysis = new GripAnalysis();
    WorkoutShakeTrack workoutShakeTrack = new WorkoutShakeTrack();

    public PickUpHold() {

    }

    @Override
    public void dataIn(float accX, float accY, float accZ, float gravX, float gravY, float gravZ, int walkingCount, Context context) {

        workoutShakeTrack.analyseData(accX, accY, accZ);
        float differenceVAL = Math.abs(accY - countPickupLastVal);
        holdAccuracy(accX, accY, accZ);
    }

    @Override
    public void holdAccuracy(float accX, float accY, float accZ) {

        total++;
        float accT = (float) Math.pow((Math.pow(accX, 2) + Math.pow(accY, 2) + Math.pow(accZ, 2)), .5);
        double Diff = Math.abs(lastVal - accT);
        if (Diff > .3) {

            errors++;
        }
    }

    @Override
    public boolean workoutFinished() {
        Long nowTime = System.currentTimeMillis();

        long differenceTime = Math.abs(nowTime - startTime);

        val = differenceTime;
        if (!workoutStarted) {
            if (pickupAnnounce = false) {
                shouldITalk = true;
                whatToSay = "hold the cup above your head";
                pickupAnnounce = true;
            }
            if (differenceTime > 5000) {
                workoutStarted = true;
                whatToSay = "Countdown Started";
                shouldITalk = true;
                startTime = System.currentTimeMillis();
            }
        } else {
            if (differenceTime > 30000) {
                return true;

            }
            if (((30000 - differenceTime) < workoutAnnounceCheckpoint && workoutAnnounceCheckpoint != 0)) {
                whatToSay = ((workoutAnnounceCheckpoint) / 1000) + " seconds left";
                workoutAnnounceCheckpoint -= 5000;

                shouldITalk = true;
            }
        }
        return false;
    }

    @Override
    public String result() {

        return "\n\n\nPickup Held" + val;
    }

    @Override
    public boolean saveGripPosition(float gravX, float gravY, float gravZ) {
        return gripAnalysis.saveGripPosition(gravX, gravY, gravZ);
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
    public int getGrade() {
        if(workoutShakeTrack.getShakeCount()[0]<=100){
            return 100;
        }else{
            if(100-(Math.abs(workoutShakeTrack.getShakeCount()[0]-100)/2)<=0){
                return 0;
            }else{
                return 100-((workoutShakeTrack.getShakeCount()[0]-100)/2);
            }
        }

    }

    @Override
    public String getWorkoutName() {
        return "Pick Up Hold";
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
        return "hold the cup above your head, hold it as still as possible.";
    }
}
