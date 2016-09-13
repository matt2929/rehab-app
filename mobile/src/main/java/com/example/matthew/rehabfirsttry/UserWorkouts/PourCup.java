package com.example.matthew.rehabfirsttry.UserWorkouts;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;

import com.example.matthew.rehabfirsttry.R;
import com.example.matthew.rehabfirsttry.Utilities.WorkoutShakeTrack;

/**
 * Created by Matthew on 8/10/2016.
 */
public class PourCup implements WorkoutSession {
    int sizeOfQuarters = 40;
    int[] pitcher = {sizeOfQuarters, sizeOfQuarters, sizeOfQuarters, sizeOfQuarters};
    float deadzone = (float) 9.2, firstMark = (float) 7.275, secondMark = (float) 4.85, thirdMark = (float) 2.425;
    long StartTime = System.currentTimeMillis();
    MediaPlayer mediaPlayer = null;
    boolean mediaChecked = false;
    boolean startedWork = false;
    boolean outOfSpace = false;
    int startGoal=13500;
    double AccX = 0, AccY = 0;
    WorkoutShakeTrack workoutShakeTrack = new WorkoutShakeTrack();
    boolean shouldISpeak = false;
    String whatToSay = "";
    int grade = 100;

    @Override
    public int getGrade() {
        if (grade <= 0) {
            return 0;
        } else {
            return grade;
        }
    }

    @Override
    public void dataIn(float accX, float accY, float accZ, float gravX, float gravY, float gravZ, int walkingCount, Context context) {
        //Log.e("said",""+Math.abs(StartTime - System.currentTimeMillis()));

        if (Math.abs(StartTime - System.currentTimeMillis()) > startGoal) {
            if (startedWork == false) {
                startedWork = true;
                whatToSay="Please Begin Pouring";
                shouldISpeak=true;
                Log.e("said","it");
            } else {
                workoutShakeTrack.analyseData(accX, accY, accZ);
                AccX = accX;
                AccY = accY;
                //has mediaplayer been instantiated
                if (!mediaChecked) {
                    mediaPlayer = MediaPlayer.create(context, R.raw.pouring_water);
                    mediaChecked = true;
                    mediaPlayer.start();
                    mediaPlayer.setLooping(true);
                }
                int positionCup;
                //determine how far cup is tipped  based on Y Grav  []-> to []V
                if (accY > deadzone) {
                    positionCup = -1;
                } else if (accY > firstMark) {
                    positionCup = 1;

                } else if (accY > secondMark) {
                    positionCup = 2;
                } else if (accY > thirdMark) {
                    positionCup = 3;
                } else if (accY <= thirdMark) {
                    positionCup = 4;
                } else {
                    positionCup = -1;
                }

                if (positionCup == -1) {
                    if (mediaPlayer.isPlaying()) {

                        mediaPlayer.pause();
                    }
                } else {

                    for (int cupSearch = 0; cupSearch < positionCup; cupSearch++) {
                        if (pitcher[cupSearch] != 0) {
                            pitcher[cupSearch] = pitcher[cupSearch] - 1;
                            int totalLiquidCanBePoured = 0;
                            int totalLiquidArea = 0;
                            for (int sum = 0; sum < positionCup; sum++) {
                                totalLiquidCanBePoured += pitcher[sum];
                                totalLiquidArea += sizeOfQuarters;
                            }
                            if (totalLiquidCanBePoured >= sizeOfQuarters * 2) {
                                if (!outOfSpace) {
                                    outOfSpace = true;
                                    outOfSpace = false;
                                    pitcher[cupSearch] = pitcher[cupSearch] + 1;
                                    whatToSay = "Pouring too quick!";
                                    grade = grade - 5;
                                    Log.e("Grade", grade + "%");
                                    shouldISpeak = true;
                                    mediaPlayer.pause();
                                    outOfSpace = true;

                                } else {

                                    pitcher[cupSearch] = pitcher[cupSearch] + 1;
                                }
                            } else {
                                outOfSpace = false;
                                float poportionWater = (float) totalLiquidCanBePoured / (float) totalLiquidArea;
                                Log.e("Volume", "[" + poportionWater + "]");
                                mediaPlayer.setVolume(poportionWater, poportionWater);
                                if (!mediaPlayer.isPlaying()) {
                                    Log.e("media", "started");
                                    mediaPlayer.start();
                                    mediaPlayer.setLooping(true);
                                } else {
                                    Log.e("media", "continue");

                                }
                            }
                            break;

                        } else {

                            mediaPlayer.pause();
                        }
                    }
                }

            }
        }
    }

    @Override
    public boolean workoutFinished() {

        for (int i = 0; i < pitcher.length; i++) {
            if (pitcher[i] != 0) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String result() {
        return (((((double) (pitcher[0] + pitcher[1] + pitcher[2] + pitcher[3]) / (sizeOfQuarters * 4)) * 100.0) + "%"));
    }

    @Override
    public boolean saveGripPosition(float accX, float accY, float accZ) {
        return true;
    }

    @Override
    public void holdAccuracy(float accX, float accY, float accZ) {

    }

    @Override
    public boolean shouldISaySomething() {
        return shouldISpeak;
    }

    @Override
    public int[] ShakeNum() {
        return workoutShakeTrack.getShakeCount();
    }


    @Override
    public String whatToSay() {
        shouldISpeak = false;
        return whatToSay;
    }

    @Override
    public String saveData() {
        return null;
    }

    @Override
    public String getWorkoutName() {
        return "Pour Water";
    }

    @Override
    public int xPostiontDisplay() {
        return 0;
    }

    @Override
    public int yPostiontDisplay() {
        return 0;
    }

    @Override
    public double accuracyProgression() {
        return 0;
    }

    @Override
    public String sayHowToHoldCup() {
        long StartTime = System.currentTimeMillis();
        return "In this workout you will hold the cup up and pretend to pour out water in front of you. Do not pour too quick or to slow. Please start when I say please begin pouring.";
    }
}
