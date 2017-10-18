package com.example.mytranslationapp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.view.View;

/**
 * Created by yvonne40335 on 2017/10/18.
 */

public class SideBar extends View {

    private String[] alphabet = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};
    private int currentChoosenAlphabetIndex = -1;

    // new painter
    private Paint mPaint = new Paint();
    private int alphabetTextSize = 20;

    public SideBar(Context context){
        super(context);
    }

    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);

        int viewHeight = getHeight();
        int viewWidth = getWidth();
        int heightPerAlphabet = viewHeight/alphabet.length; //sidebar字母的高度

        for(int i=0;i<alphabet.length;i++){
            mPaint.setColor(Color.BLACK); //color
            mPaint.setTypeface(Typeface.DEFAULT_BOLD); //粗體
            mPaint.setTextSize(alphabetTextSize); //textsize
            mPaint.setAntiAlias(true); //抗鋸齒

            //如果選到該字母 則改變顏色跟字體大小
            if(currentChoosenAlphabetIndex==i){
                mPaint.setColor(Color.WHITE);
                mPaint.setTextSize(alphabetTextSize);
                mPaint.setFakeBoldText(true);
            }

            //??????
            float xPos = viewWidth/2 - mPaint.measureText(alphabet[i])/2;
            float yPos = heightPerAlphabet*i+heightPerAlphabet;
            canvas.drawText(alphabet[i],xPos,yPos,mPaint);
            mPaint.reset();

        }
    }
}
