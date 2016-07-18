package com.example.matthew.rehabfirsttry.Utilities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Matthew on 7/15/2016.
 */
public class WorkoutHistoricalData implements Serializable {
    ArrayList<WorkoutSession> _history;
    public WorkoutHistoricalData(ArrayList<WorkoutSession> history){
        _history=history;
    }
    public void addWorkout(WorkoutSession ws){
        _history.add(0,ws);
    }
    public ArrayList<WorkoutSession> get_history() {
        return _history;
    }

    public static class WorkoutSession implements Serializable {
        public Calendar cal = Calendar.getInstance();
        public double Accuracy = 100;
        public String workoutInfo = "";

        public WorkoutSession(double accuracy, String workoutinfo) {
            workoutInfo = workoutinfo;
            Accuracy = accuracy;
        }

        public Calendar get_Cal() {
            return cal;
        }

        public double getAccuracy() {
            return Accuracy;
        }

        public String getWorkoutInfo() {
            return workoutInfo;
        }
    }
}
