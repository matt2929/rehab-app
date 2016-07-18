package com.example.matthew.rehabfirsttry.UserWorkouts;

/**
 * Created by Matthew on 7/9/2016.
 */
public interface WorkoutSession {

    public void dataIn(float accX, float accY, float accZ, float gravX,float gravY,float gravZ);

    public boolean workoutFinished();

    public String result();

    public boolean goodHold(float gravX, float gravY, float gravZ);
    public void holdAccuracy(float accX, float accY, float accZ);
    public boolean shouldISaySomething();
    public double howAccurate();
    public String whatToSay();

    public String saveData();

    public String getWorkoutName();
}
