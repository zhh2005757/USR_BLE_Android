package com.usr.usrsimplebleassistent.views;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Interpolator;

/**
 * Created by Administrator on 2015-07-29.
 */
public class RevealBackgroundView extends View {
    public static final int STATE_NOT_STARTED = 0;
    public static final int STATE_FILL_STARTED = 1;
    public static final int STATE_FINISHED = 2;

    public static final int STATE_END_STARTED = 3;
    public static final int STATE_END_FINISHED = 4;

    private static final Interpolator INTERPOLATOR = new AccelerateInterpolator();
    private static final int FILL_TIME = 400;
    private int state = STATE_NOT_STARTED;

    private Paint fillPaint;
    private int currentRadius;

    ObjectAnimator revealAnimator;
    ObjectAnimator revealEndAnimator;
    private int startLocationX;
    private int startLocationY;
    private OnStateChangeListener onStateChangeListener;

    public RevealBackgroundView(Context context) {
        super(context);
        init();
    }

    public RevealBackgroundView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RevealBackgroundView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public RevealBackgroundView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }


    private void init(){
        fillPaint = new Paint();
        fillPaint.setAntiAlias(true);
        fillPaint.setDither(true);
        fillPaint.setStyle(Paint.Style.FILL);
        fillPaint.setColor(Color.parseColor("#536DFE"));
    }


    public void setFillPaintColor(int color) {
        fillPaint.setColor(color);
    }



    //@Override
    public boolean onTouchEvent(MotionEvent event) {
        getParent().requestDisallowInterceptTouchEvent(true);
        return true;
    }

    public void startFromLocation(int[] location){
        startLocationX = location[0];
        startLocationY = location[1];

        revealAnimator = ObjectAnimator.ofInt(this,"currentRadius",0,getHeight()).setDuration(FILL_TIME);
        revealAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                changeState(STATE_FINISHED);
            }
        });

        changeState(STATE_FILL_STARTED);
        revealAnimator.start();
    }


    public void endFromEdge(){
        revealEndAnimator = ObjectAnimator.ofInt(this,"currentRadius",getHeight(),0).setDuration(FILL_TIME);
        revealEndAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                changeState(STATE_END_FINISHED);
            }
        });

//        fillPaint.setColor(Color.BLUE);

        changeState(STATE_END_STARTED);
        revealEndAnimator.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (state == STATE_FINISHED){
            canvas.drawRect(0,0,getWidth(),getHeight(),fillPaint);
        }else {
            canvas.drawCircle(startLocationX,startLocationY,currentRadius,fillPaint);
        }
    }

    public void setCurrentRadius(int currentRadius) {
        this.currentRadius = currentRadius;
        invalidate();
    }


    public void setToFinishedFrame() {
        changeState(STATE_FINISHED);
        invalidate();
    }


    private void changeState(int state) {
        if (this.state == state) {
            return;
        }

        this.state = state;
        if (onStateChangeListener != null) {
            onStateChangeListener.onStateChange(state);
        }
    }

    public int getState() {
        return state;
    }

    public void setOnStateChangeListener(OnStateChangeListener onStateChangeListener) {
        this.onStateChangeListener = onStateChangeListener;
    }

    public static interface OnStateChangeListener {
        void onStateChange(int state);
    }

}
