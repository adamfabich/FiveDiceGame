package com.example.fivedicegame.myDice;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

public class One extends View {

    Paint paint;
    View diceButton;
    float radius;

    public One(Context context, View diceButton, float dotWidth) {
        super(context);

        this.diceButton = diceButton;
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.BLACK);
        this.radius = dotWidth;
    }

    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int buttonCenterX = diceButton.getLeft() + diceButton.getWidth()/2;
        int buttonCenterY = diceButton.getTop() + diceButton.getHeight()/2;

        canvas.drawCircle(buttonCenterX, buttonCenterY, radius, paint);
    }

}

