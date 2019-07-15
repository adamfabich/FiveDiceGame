package com.example.fivedicegame.myDice;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

public class Two extends View {

    Paint paint;
    View diceButton;
    float radius;

    public Two(Context context, View diceButton, float dotWidth) {
        super(context);

        this.diceButton = diceButton;
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.BLACK);
        this.radius = dotWidth;
    }

    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int firstX = diceButton.getLeft() + diceButton.getWidth()/3;
        int firstY = diceButton.getTop() + diceButton.getHeight()/3;

        int secondX = diceButton.getRight() - diceButton.getWidth()/3;
        int secondY = diceButton.getBottom() - diceButton.getHeight()/3;

        canvas.drawCircle(firstX, firstY, radius, paint);
        canvas.drawCircle(secondX, secondY, radius, paint);
    }

}
