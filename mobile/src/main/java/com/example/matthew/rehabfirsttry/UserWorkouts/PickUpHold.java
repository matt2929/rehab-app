package com.example.matthew.rehabfirsttry.UserWorkouts;

import android.content.Context;
import android.util.Log;

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
    long StartTime = System.currentTimeMillis();
    int startGoal = 12500;
    boolean startedWork = false;

    public PickUpHold() {

    }

    @Override
    public void dataIn(float accX, float accY, float accZ, float gravX, float gravY, float gravZ, int walkingCount, Context context) {

        if (Math.abs(StartTime - System.currentTimeMillis()) > startGoal) {
            if (startedWork == false) {
                startedWork = true;
                whatToSay = "Please Begin ";
                shouldITalk = true;
        workoutStarted=true;
                Log.e("said", "it");
            } else {

                workoutShakeTrack.analyseData(accX, accY, accZ);
                float differenceVAL = Math.abs(accY - countPickupLastVal);
                holdAccuracy(accX, accY, accZ);
            }
        }else{
            startTime=System.currentTimeMillis();
        }
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
        } else {
            if (differenceTime > 25000) {
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

        return "\n\n\nHeld " + val /1000+" sec";
    }

    @Override
    public boolean saveGripPosition(float gravX, float gravY, float gravZ) {
        return true;
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
        if (workoutShakeTrack.getShakeCount()[0] <= 100) {
            return 100;
        } else {
            if (100 - (Math.abs(workoutShakeTrack.getShakeCount()[0] - 100) / 2) <= 0) {
                return 0;
            } else {
                return 100 - ((workoutShakeTrack.getShakeCount()[0] - 100) / 2);
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
        return "In this workout you will hold the cup above your head and try to move as little as possible. Start when I say Please Begin";
    }
}
