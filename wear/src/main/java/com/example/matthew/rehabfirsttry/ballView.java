package com.example.matthew.rehabfirsttry;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Matthew on 5/22/2016.
 */
public class ballView extends View {
    int width;
    int height;
    Paint YellowCol, GreenCol, RedCol;
    double BallPositionX = 0;
    double BallPositionY = 0;
    double targetPositionX;
    double targetPositionY;
    int circle = 10;
    boolean BallIsInCorrectPosition = false;


    public ballView(Context context) {
        super(context);
        width = this.getWidth();
        height = this.getHeight();
        YellowCol = new Paint();
        YellowCol.setColor(Color.YELLOW);
        GreenCol = new Paint();
        GreenCol.setColor(Color.GREEN);
        RedCol = new Paint();
        RedCol.setColor(Color.RED);
    }

    public ballView(Context context, AttributeSet attrs) {
        super(context, attrs);
        width = this.getWidth();
        height = this.getHeight();
        YellowCol = new Paint();
        YellowCol.setColor(Color.YELLOW);
        GreenCol = new Paint();
        GreenCol.setColor(Color.GREEN);

        RedCol = new Paint();
        RedCol.setColor(Color.RED);
    }

    public ballView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        width = this.getWidth();
        height = this.getHeight();
        YellowCol = new Paint();
        YellowCol.setColor(Color.YELLOW);
        GreenCol = new Paint();
        GreenCol.setColor(Color.GREEN);

        RedCol = new Paint();
        RedCol.setColor(Color.RED);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        height = this.getHeight();
        width = this.getWidth();
        //X
        canvas.drawLine(0, height / 2, width, height / 2, RedCol);
        canvas.drawLine(width / 2, (height / 2) - 5, width / 2, (height / 2) + 5, RedCol);

        //Y
        canvas.drawLine(width / 2, 0, width / 2, height, RedCol);
        canvas.drawLine((width / 2) - 5, height / 2, (width / 2) + 5, height / 2, RedCol);


        canvas.drawCircle((float)(((BallPositionX+10.0)/20.0)*width),(float)(((BallPositionY+10.0)/20)*height), circle, RedCol);
        canvas.drawCircle((float)(((targetPositionX+10.0)/20.0)*width),(float)(((targetPositionY+10.0)/20)*height), circle, RedCol);
        }

    public void updateDrawing(double postionX, double postionY, double putHereX, double putHereY) {

        BallPositionX = -postionX;
        BallPositionY = postionY;
        targetPositionX = -putHereX;
        targetPositionY = putHereY;
        this.invalidate();

    }

    public void isInPostion(boolean a) {
        if (a) {
            RedCol.setColor(Color.GREEN);
        } else {
            RedCol.setColor(Color.RED);
        }
    }
}
