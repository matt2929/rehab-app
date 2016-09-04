package com.example.matthew.rehabfirsttry.UserWorkouts;

import android.content.Context;

/**
 * Created by Matthew on 7/9/2016.
 */
public interface WorkoutSession {


    public void dataIn(float accX, float accY, float accZ, float gravX,float gravY,float gravZ,int walkingCount,Context context);

    public boolean workoutFinished();

    public String result();

    public boolean saveGripPosition(float accX, float accY, float accZ);

    public void holdAccuracy(float accX, float accY, float accZ);

    public boolean shouldISaySomething();

    public int[] ShakeNum();
    public String whatToSay();

    public String saveData();

    public String getWorkoutName();

    public int xPostiontDisplay();

    public int yPostiontDisplay();

    public double accuracyProgression();

    public String sayHowToHoldCup();

    public int getGrade();
}
