package com.example.matthew.rehabfirsttry.Utilities;

import java.util.ArrayList;

/**
 * Created by Matthew on 9/20/2016.
 */

public class JerkScoreAnalysis {
    float accelerationTotal=0;
    float Height=0;
    ArrayList<Float> jerkSave = new ArrayList<Float>();
    public JerkScoreAnalysis(float height){
        Height=height;
    }
    public void jerkAdd(float accX, float accY,float accZ){
        accelerationTotal+=(Math.pow(accX,2)+Math.pow(accY,2)+Math.pow(accZ,2));
    }

    public void jerkCompute(float time){
        jerkSave.add((float)Math.pow(((.5f*(accelerationTotal)*(Math.pow(time,5)/Math.pow(Height,2)))),.5f));
    }

    public ArrayList<Float> getAllJerks(){
        return jerkSave;
    }
    public Float getJerkAverage(){
        Float sum =0f;
        for(Float f: jerkSave){
            sum+=f;
        }
        return sum/(float)jerkSave.size();
    }

}
