package com.example.matthew.rehabfirsttry.Activities;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.example.matthew.rehabfirsttry.R;

import java.util.ArrayList;

/**
 * Created by Matthew on 9/17/2016.
 */
public class CustomViewGraph extends RelativeLayout {
    Integer Width = 0, Height = 0, HalfWidth = 0, HalfHeight = 0, OneQuarterWidth = 0, OneQuarterHeight = 0, ThreeQuarterWidth = 0, ThreeQuarterHeight = 0;
    Float GraphDiff = Float.valueOf(0);
    Float GraphMin = Float.valueOf(0);
    Float GraphMax = Float.valueOf(0);
    Float StartLine = 10f;
    ArrayList<Float> Values = new ArrayList<>();
    Paint BlackPaint = new Paint(), WhitePaint = new Paint(), GreenPaint = new Paint(), YellowPaint = new Paint(), RedPaint = new Paint();
    int UserSelectedPoint = -1;
    Boolean bool = false;
    int Goal = 0;

    public CustomViewGraph(Context context) {
        super(context);

        firstInitialValues();
    }

    public CustomViewGraph(Context context, AttributeSet attrs) {
        super(context, attrs);

        firstInitialValues();
    }

    public CustomViewGraph(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        firstInitialValues();
    }

    public CustomViewGraph(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        firstInitialValues();
    }

    public void firstInitialValues() {
    
        Width = getWidth();
        Height = getHeight();
        HalfWidth = (int) (((float) Width) * (float) (.5));
        HalfHeight = (int) (((float) Width) * (float) (.5));
        OneQuarterWidth = (int) (((float) Width) * (float) (.25));
        OneQuarterHeight = (int) (((float) Width) * (float) (.25));
        ThreeQuarterWidth = (int) (((float) Width) * (float) (.75));
        ThreeQuarterHeight = (int) (((float) Width) * (float) (.75));
        BlackPaint.setColor(Color.BLACK);
        WhitePaint.setColor(Color.WHITE);
        GreenPaint.setColor(Color.GREEN);
        YellowPaint.setColor(Color.YELLOW);
        GreenPaint.setTextSize(40);
        RedPaint.setTextSize(40);
        RedPaint.setColor(Color.RED);
        GreenPaint.setStrokeWidth(5);
        WhitePaint.setStrokeWidth(4);
    }

    public void onDrawIntitialValues() {
        Width = getWidth();
        Height = getHeight();
        HalfWidth = (int) (((float) Width) * (float) (.5));
        HalfHeight = (int) (((float) Width) * (float) (.5));
        OneQuarterWidth = (int) (((float) Width) * (float) (.25));
        OneQuarterHeight = (int) (((float) Width) * (float) (.25));
        ThreeQuarterWidth = (int) (((float) Width) * (float) (.75));
        ThreeQuarterHeight = (int) (((float) Width) * (float) (.75));
    }


    public void setValues(ArrayList<Float> data) {
        Values.clear();
        for(int i =0;i<data.size();i++){
            Values.add(0,data.get(i));
        }
    }

    public void addValues(Float data) {
        Values.add(data);
    }

    public void clearValues() {
        Values.clear();
    }

    public int numValues() {
        return Values.size();
    }

    public void setGoal(int goal) {
        Goal = goal;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        onDrawIntitialValues();
        canvas = drawTheData(canvas);
        canvas = drawSingleIncrementLines(canvas);
        // canvas = drawHalfLines(canvas);
        // canvas = drawQuarterLines(canvas);

        canvas = drawGoalLine(canvas);
    }

    public Canvas drawHalfLines(Canvas canvas) {
        canvas.drawLine(StartLine, HalfHeight, Width, HalfHeight, YellowPaint);
        canvas.drawText("" + (GraphDiff * (float) .5), 0, HalfHeight, GreenPaint);
        canvas.drawLine(HalfWidth, 0, HalfWidth, Height, YellowPaint);
        return canvas;
    }

    public Canvas drawQuarterLines(Canvas canvas) {
        canvas.drawLine(StartLine, OneQuarterHeight, Width, OneQuarterHeight, YellowPaint);
        canvas.drawLine(OneQuarterWidth, 0, OneQuarterWidth, Height, YellowPaint);
        canvas.drawLine(StartLine, ThreeQuarterHeight, Width, ThreeQuarterHeight, YellowPaint);
        canvas.drawLine(ThreeQuarterWidth, 0, ThreeQuarterWidth, Height, YellowPaint);
        return canvas;
    }

    public Canvas drawSingleIncrementLines(Canvas canvas) {
        for (int i = 0; i < (GraphDiff); i++) {
            if (i % findClosestDivToZero() == 0) {
                canvas.drawLine(0, ((GraphDiff - i) / GraphDiff) * Height, Width, ((GraphDiff - i) / GraphDiff) * Height, WhitePaint);
                canvas.drawText("" + (GraphMin + i), 0, ((GraphDiff - i) / GraphDiff) * Height, RedPaint);
            }
        }
        return canvas;
    }
    public Canvas drawGoalLine(Canvas canvas){
        canvas.drawLine(0,((GraphDiff-Goal)/GraphDiff)*Height,Width,((GraphDiff-Goal)/GraphDiff)*Height,YellowPaint);
        return canvas;
    }

    public Canvas drawTheData(Canvas canvas) {
        float spaceBetweenValuesX = ((float) Width / (float) numValues());
        float incrementX = 0;
        getDiff();

        for (int i = 0; i < numValues(); i++) {
            float val = Values.get(i);
            if (i == 0) {

            } else {
                incrementX += spaceBetweenValuesX;
            }
            float poportionalVal = ((GraphDiff - (val - GraphMin)) * Height) / (GraphDiff);
            //this is what the user clicked
            if (i == UserSelectedPoint) {
                canvas.drawCircle(incrementX, poportionalVal, 29, YellowPaint);
            }
            if (bool) {
                if ((i + 1) == numValues()) {

                } else {
                    float poportionalVal2 = ((GraphDiff - (Values.get(i + 1) - GraphMin)) * Height) / (GraphDiff);
                    canvas.drawLine(incrementX, poportionalVal, incrementX + spaceBetweenValuesX, poportionalVal2, GreenPaint);
                }
            } else {
                canvas.drawCircle(incrementX, poportionalVal, 22, GreenPaint);
            }
        }
        return canvas;
    }

    public void selectHighlightedData(int i) {
        UserSelectedPoint = i;
        this.invalidate();
    }

    public void getDiff() {
        float small = Float.MAX_VALUE;
        float large = Float.MIN_VALUE;
        for (int i = 0; i < numValues(); i++) {
            float val = Values.get(i);
            if (val < small) {
                small = val;
            }
            if (val > large) {
                large = val;
            }
        }
        GraphDiff = Math.abs(large - small);
        GraphMin = small;
        GraphMax = large;
    }

    public void dotOrLine(boolean b) {
        bool = b;
        invalidate();
    }

    public int findClosestDivToZero() {
        for (int i = 0; i < GraphDiff; i++) {
            if ((GraphDiff / i) < 10 && (GraphDiff / i) > 5) {
                return i;
            }
        }
        return 2;
    }}

