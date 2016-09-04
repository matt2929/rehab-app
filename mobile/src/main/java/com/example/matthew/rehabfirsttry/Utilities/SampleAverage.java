package com.example.matthew.rehabfirsttry.Utilities;

import java.util.ArrayList;

/**
 * Created by Matthew on 7/6/2016.
 */
public class SampleAverage {
    public ArrayList<Float> blockingData = new ArrayList<Float>();
    public ArrayList<Float> smoothData = new ArrayList<Float>();
    public float BlockingAverage = 0;
    public float SmoothAverage = 0;
    public int BlockingDataSize = 5;
    public int SmoothingDataSize = 5;

    public void addBlockingAverage(float a) {
        if (blockingData.size() < BlockingDataSize) {
            blockingData.add(a);
        } else {
            BlockingAverage = averageArr(blockingData);
            blockingData.clear();
        }
    }

    public float getBlockingAverage() {
        return BlockingAverage;
    }


    public void addSmoothAverage(float a) {
        smoothData.add(a);
        if (smoothData.size() > SmoothingDataSize) {
            smoothData.remove(0);
        } else {
            }
        SmoothAverage = averageArr(smoothData);

    }

    public float getMedianAverage() {
        return SmoothAverage;
    }

    public float averageArr(ArrayList<Float> f) {
        float sum = 0;
        for (Float d : f
                ) {
            sum += d;
        }
        return sum / ((float) f.size());
    }
}
