package com.example.matthew.rehabfirsttry.UserWorkouts;

import android.content.Context;
import android.text.format.Time;

import com.example.matthew.rehabfirsttry.Utilities.GripAnalysis;
import com.example.matthew.rehabfirsttry.Utilities.SampleAverage;
import com.example.matthew.rehabfirsttry.Utilities.WorkoutShakeTrack;

import java.util.ArrayList;

/**
 * Created by Matthew on 7/27/2016.
 */
public class PickUpPutDown implements WorkoutSession{


    int getPickupCountMax = 20;
    SampleAverage sampleAverage = new SampleAverage();
    float countPickupLastVal = 10;
    float countAccuracyLastVal = 8;
    int pickupCount = 0;
    Time startTime = new Time();
    String whatToSay = "";
    boolean shouldITalk = false;
    double lastSlope = 0;
    double collisionNumber = 0;
    float a = 0;
    boolean imOnLowerSurface=true;
    ArrayList<String> stringsIHaveSaid = new ArrayList<>();
    double totaldatas = 0;
    GripAnalysis gripAnalysis = new GripAnalysis();
    boolean inMotion=false;
    boolean hasStarted=false;
    WorkoutShakeTrack workoutShakeTrack = new WorkoutShakeTrack();
public PickUpPutDown(){
    startTime.setToNow();
}
    @Override
    public void dataIn(float accX, float accY, float accZ, float gravX, float gravY, float gravZ, int walkingCount, Context context) {
        workoutShakeTrack.analyseData(accX, accY, accZ);
        float differenceVAL = Math.abs(accY - countPickupLastVal);
        a = differenceVAL;
        countPickupLastVal = accY;
        sampleAverage.addSmoothAverage(differenceVAL);
        Time nowTime = new Time();
        nowTime.setToNow();
        holdAccuracy(accX, accY, accZ);
        long differenceTime = Math.abs(nowTime.toMillis(true) - startTime.toMillis(true));
        if(sampleAverage.getMedianAverage()<.5&& differenceTime > 2000&&inMotion) {
            startTime.setToNow();
            shouldITalk = true;
            pickupCount++;
           if (imOnLowerSurface){
               whatToSay = "Up";
           }else{
               whatToSay = ".Down";
           }
            inMotion=false;
            imOnLowerSurface=!imOnLowerSurface;
        }else if(sampleAverage.getMedianAverage()>1&&!inMotion){
            inMotion=true;
        }
    }

    @Override
    public void holdAccuracy(float accX, float accY, float accZ) {


        float accTotal = (float) Math.pow((Math.pow(accX, 2) + Math.pow(accY, 2) + Math.pow(accZ, 2)), .5);


        double slope = accTotal - countAccuracyLastVal;

        if (((slope > 0 && lastSlope < 0) || (lastSlope > 0 && slope < 0)) && countAccuracyLastVal <= 8.6) {
            collisionNumber++;
        }
        countAccuracyLastVal = accTotal;
        lastSlope = slope;
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

        return "\n\n\nPut away " + pickupCount + " time(s).";
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
    public int getGrade() {
        return 0;
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
    public String getWorkoutName() {
        return "Pick Up Put Down";
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
        return "Put the cup on the higher point than return it to the lower position.";
    }

}
