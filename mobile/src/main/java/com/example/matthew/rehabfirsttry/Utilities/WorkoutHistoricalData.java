package com.example.matthew.rehabfirsttry.Utilities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Matthew on 7/15/2016.
 */
public class WorkoutHistoricalData implements Serializable {
    ArrayList<WorkoutSession> _history;

    public WorkoutHistoricalData(ArrayList<WorkoutSession> history) {
        _history = history;
    }

    public void addWorkout(WorkoutSession ws) {
        _history.add(ws);
    }

    public ArrayList<WorkoutSession> get_history() {
        return _history;
    }

    public boolean leftHand = false;
    public int grade = 0;

    public static class WorkoutSession implements Serializable {
        public Calendar cal = Calendar.getInstance();
        public int[] shakeList = new int[0];
        public String workoutInfo = "";
        public String workoutName = "";
        public boolean LeftHand = false;
        public int Grade = 0;

        public WorkoutSession(String workoutname, int[] shakelist, String workoutinfo, int grade, boolean leftHand) {
            workoutInfo = workoutinfo;
            shakeList = shakelist;
            workoutName = workoutname;
            LeftHand=leftHand;
            Grade=grade;
        }

        public Calendar get_Cal() {
            return cal;
        }

        public int[] getShakeList() {
            return shakeList;
        }

        public String getWorkoutInfo() {
            return workoutInfo;
        }

        public String getWorkoutName() {
            return workoutName;
        }

        public int getGrade() {
            return Grade;
        }

        public boolean isLeftHand() {
            return LeftHand;
        }
    }

}
