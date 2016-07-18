package com.example.matthew.rehabfirsttry.UserWorkouts;

/**
 * Created by Matthew on 7/12/2016.
 */
public class WalkWithCup implements WorkoutSession{
    int goodHoldMax =0;
    int numGoodHolds=0;
    @Override
    public void dataIn(float accX, float accY, float accZ, float gravX, float gravY, float gravZ) {

    }

    @Override
    public boolean workoutFinished() {
        return false;
    }

    @Override
    public String result() {
        return null;
    }

    @Override
    public boolean goodHold(float gravX, float gravY, float gravZ) {
        return false;
    }

    @Override
    public void holdAccuracy(float gravX, float gravY, float gravZ) {

    }

    @Override
    public boolean shouldISaySomething() {
        return false;
    }

    @Override
    public double howAccurate() {
        return 0;
    }

    @Override
    public String whatToSay() {
        return null;
    }

    @Override
    public String saveData() {
        return null;
    }

    @Override
    public String getWorkoutName() {
        return null;
    }
}
