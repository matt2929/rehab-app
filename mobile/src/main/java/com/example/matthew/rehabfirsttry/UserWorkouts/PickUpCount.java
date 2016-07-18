package com.example.matthew.rehabfirsttry.UserWorkouts;

import android.text.format.Time;

import com.example.matthew.rehabfirsttry.Utilities.SampleAverage;

/**
 * Created by Matthew on 7/10/2016.
 */
public class PickUpCount implements WorkoutSession {

    int goodHoldMax = 5;
    int numGoodHolds = 0;
    boolean haveITalked = false;
    SampleAverage sampleAverage = new SampleAverage();
    float lastVal = 10;
    int pickupCount = 0;
    Time startTime = new Time();
    String whatToSay = "";
    boolean shouldITalk = false;
    double lastSlope = 0;
    int collisionNumber = 0;
    float a = 0;

    public PickUpCount() {
        startTime.setToNow();
    }

    @Override
    public void dataIn(float accX, float accY, float accZ, float gravX, float gravY, float gravZ) {
        float accTotal = (float) Math.pow((Math.pow(accX, 2) + Math.pow(accY, 2) + Math.pow(accZ, 2)), .5);
        float differenceVAL = Math.abs(accTotal - lastVal);
        a = differenceVAL;
        lastVal = accTotal;
        sampleAverage.addSmoothAverage(differenceVAL);

        Time nowTime = new Time();
        nowTime.setToNow();
        //holdAccuracy(accX, accY, accZ);
        long differenceTime = Math.abs(nowTime.toMillis(true) - startTime.toMillis(true));
        if (sampleAverage.getMedianAverage() > 1.2 && differenceTime > 1000) {
            startTime.setToNow();
            shouldITalk = true;
            pickupCount++;
            whatToSay = "" + pickupCount;
        }
    }

    @Override
    public void holdAccuracy(float accX, float accY, float accZ) {
        float accTotal = (float) Math.pow((Math.pow(accX, 2) + Math.pow(accY, 2) + Math.pow(accZ, 2)), .5);
        float differenceVAL = Math.abs(lastVal - accTotal);
        double slope = differenceVAL / lastVal;
        if (((slope > 0 && lastSlope < 0) || (lastSlope > 0 && slope < 0)) && lastVal <= 8){
            collisionNumber++;
        }
        lastSlope = slope;
    }

    @Override
    public boolean workoutFinished() {
        if (pickupCount == 10) {
            return true;
        }
        return false;
    }

    @Override
    public String result() {

       return "\n\n\nPickup Count: " + pickupCount;
    }

    @Override
    public boolean goodHold(float gravX, float gravY, float gravZ) {
        if (gravX > -1 && gravX < 2 && gravY > 9 && gravY < 10.5 && gravZ > -.2 && gravZ < 1.5) {
            numGoodHolds++;
        }
        if (numGoodHolds == goodHoldMax) {
            return true;
        }
        return false;
    }


    @Override
    public boolean shouldISaySomething() {
        return shouldITalk;
    }

    @Override
    public double howAccurate() {
        int difference = collisionNumber - pickupCount;
        if ((100 - (difference * 2)) < 0) {
            return 0;
        }
        return (100 - (difference * 2));
    }

    @Override
    public String whatToSay() {
        shouldITalk = false;
        return whatToSay;
    }

    @Override
    public String saveData() {
        String returnMe = "";
        return returnMe;
    }

    @Override
    public String getWorkoutName() {
        return "Pick Up Count";
    }

}
