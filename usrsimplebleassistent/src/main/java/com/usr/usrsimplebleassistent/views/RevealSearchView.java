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
import android.view.animation.LinearInterpolator;


/**
 * Created by Administrator on 2015-07-29.
 */
public class RevealSearchView extends View {
    public static final int STATE_NOT_STARTED = 0;
    public static final int STATE_FILL_STARTED = 1;
    public static final int STATE_FINISHED = 2;

    private static final Interpolator INTERPOLATOR = new AccelerateInterpolator();
    private static final int FILL_TIME = 11000;
    private int state = STATE_NOT_STARTED;

    private Paint fillPaint;
    private int currentRadius;

    ObjectAnimator revealAnimator;
    private int startLocationX;
    private int startLocationY;
    private OnStateChangeListener onStateChangeListener;

    private float finalRadius;
    //圆形的条数
    private int count = 11;

    //圆形之间的间隔像素
    private int intervalPix = 350;

    public RevealSearchView(Context context) {
        super(context);
        init();
    }

    public RevealSearchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RevealSearchView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public RevealSearchView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }


    private void init() {
        fillPaint = new Paint();
        fillPaint.setStyle(Paint.Style.STROKE);
        fillPaint.setStrokeWidth(6f);
        fillPaint.setColor(Color.WHITE);
        fillPaint.setDither(true);
        fillPaint.setAntiAlias(true);
    }


    public void setFillPaintColor(int color) {
        fillPaint.setColor(color);
    }


    public void startFromLocation(int[] location) {
        finalRadius = getWidth() *0.7f;
        changeState(STATE_FILL_STARTED);
        startLocationX = location[0];
        startLocationY = location[1];

        int allLengthRadius = (int)((count-1)*intervalPix+finalRadius);
        revealAnimator = ObjectAnimator.ofInt(this, "currentRadius", 0, allLengthRadius).setDuration(FILL_TIME);
        revealAnimator.setInterpolator(new LinearInterpolator());
        revealAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                changeState(STATE_FINISHED);
            }
        });
        revealAnimator.start();
    }


    public void stopAnimate(){
        if (revealAnimator != null)
            revealAnimator.cancel();
    }


    //    @Override
    public boolean onTouchEvent(MotionEvent event) {
        getParent().requestDisallowInterceptTouchEvent(true);
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (state == STATE_FINISHED) {
            canvas.drawCircle(startLocationX, startLocationY, 0, fillPaint);
        } else {

            for (int i=0;i<count;i++){
                float radius = currentRadius - intervalPix*i;
                if (radius <finalRadius){
                    calculatePaintAlpha(radius);
                    canvas.drawCircle(startLocationX, startLocationY, radius, fillPaint);
                }
            }


//            float radius2 = currentRadius - intervalPix*3;
//            float radius3 = currentRadius - intervalPix*2;
//            float radius4 = currentRadius - intervalPix;
//            float radius0 = currentRadius - 0;
//
//            if (radius0 < finalRadius){
//                calculatePaintAlpha(radius0);
//                canvas.drawCircle(startLocationX, startLocationY, radius0, fillPaint);
//            }
//
//            if (radius2 < finalRadius){
//                calculatePaintAlpha(radius2);
//                canvas.drawCircle(startLocationX, startLocationY, radius2, fillPaint);
//            }
//
//            if (radius3 < finalRadius){
//                calculatePaintAlpha(radius3);
//                canvas.drawCircle(startLocationX, startLocationY, radius3, fillPaint);
//            }
//
//            if (radius4 < finalRadius){
//                calculatePaintAlpha(radius4);
//                canvas.drawCircle(startLocationX, startLocationY, radius4, fillPaint);
//            }

        }
    }


    private void calculatePaintAlpha(float radius){
        int alpha = (int) (255 - radius/finalRadius*185);
        fillPaint.setAlpha(alpha);
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
