package com.example.mytranslationapp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

/**
 * Created by yvonne40335 on 2017/10/18.
 */

public class SideBar extends View {

    private String[] alphabet = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};
    private int currentChoosenAlphabetIndex = -1;

    private TextView textViewDialog;
    // new painter
    private Paint mPaint = new Paint();
    private int alphabetTextSize = 20;
    private int bottomNavigationHeight = 120;
    OnLetterTouchListener onLetterTouchListener;

    public void setOnLetterTouchListener(OnLetterTouchListener onLetterTouchListener) {
        this.onLetterTouchListener = onLetterTouchListener;
    }

    public SideBar(Context context) {
        super(context);
    }

    public SideBar(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public SideBar(Context context, AttributeSet attributeSet, int defStyleAttr) {
        super(context, attributeSet, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int viewHeight = getHeight();
        int viewWidth = getWidth();
        //FavCollection favCollection = new FavCollection();
        int heightPerAlphabet = (viewHeight - bottomNavigationHeight) / alphabet.length; //sidebar字母的高度

        for (int i = 0; i < alphabet.length; i++) {
            mPaint.setColor(Color.BLACK); //color
            mPaint.setTypeface(Typeface.DEFAULT_BOLD); //粗體
            mPaint.setTextSize(alphabetTextSize); //textsize
            mPaint.setAntiAlias(true); //抗鋸齒

            //如果選到該字母 則改變顏色跟字體大小
            if (currentChoosenAlphabetIndex == i) {
                mPaint.setColor(Color.WHITE);
                mPaint.setTextSize(alphabetTextSize);
                mPaint.setFakeBoldText(true);
            }

            //??????
            float xPos = viewWidth / 2 - mPaint.measureText(alphabet[i]) / 2;
            float yPos = heightPerAlphabet * i + heightPerAlphabet;
            canvas.drawText(alphabet[i], xPos, yPos, mPaint);
            mPaint.reset();

        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        int action = event.getAction(); //get touch event
        float touchYPos = event.getY(); //觸摸點的y座標
        int currentTouchIndex = (int) (touchYPos / (getHeight()- bottomNavigationHeight) * alphabet.length); //求出索引字母的index

        switch (action) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                setBackgroundResource(R.color.dark_grey);
                currentChoosenAlphabetIndex = currentTouchIndex;
                if(onLetterTouchListener!=null && currentTouchIndex<alphabet.length && currentTouchIndex>-1){
                    onLetterTouchListener.OnTouchLetterChange(alphabet[currentTouchIndex]);
                    textViewDialog.setText(alphabet[currentTouchIndex]);
                    textViewDialog.setVisibility(VISIBLE);
                }
                invalidate(); //重新執行onDraw
                break;
            case MotionEvent.ACTION_UP:
                setBackgroundResource(R.color.super_light_grey);
                currentChoosenAlphabetIndex=-1;
                invalidate();
                textViewDialog.setVisibility(INVISIBLE);
                break;
            default:
                textViewDialog.setVisibility(INVISIBLE);
                break;
        }

        return true;
    }

    public void setTextViewDialog(TextView textViewDialog) {
        this.textViewDialog = textViewDialog;
    }

    public interface OnLetterTouchListener {
        void OnTouchLetterChange(String letterTouched);
    }
}
