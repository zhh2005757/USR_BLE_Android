package com.usr.usrsimplebleassistent.Utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;

import com.usr.usrsimplebleassistent.R;


/**
 * Created by Administrator on 2015-07-29.
 */
public class AnimateUtils {

    public static void translationY(final View view,int translationY,int duration,int startDelay){
        view.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        view.animate()
                .translationY(translationY)
                .setDuration(duration)
                .setStartDelay(startDelay)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        view.setLayerType(View.LAYER_TYPE_NONE, null);
                    }
                });

    }

    public static void translationY(View view,int translationY,int duration,int startDelay,Animator.AnimatorListener listener){
        view.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        view.animate()
                .translationY(translationY)
                .setDuration(duration)
                .setStartDelay(startDelay)
                .setInterpolator(new DecelerateInterpolator())
                .setListener(listener);

    }


    public static void translationYBy(final View view,int distance,int duration,int startDelay){
        view.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        view.animate()
                .translationYBy(distance)
                .setDuration(duration)
                .setStartDelay(startDelay)
                .setInterpolator(new DecelerateInterpolator())
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        view.setLayerType(View.LAYER_TYPE_NONE, null);
                    }
                });

    }


    public static void translationX(final View view,int translationX,int duration,int startDelay){
        view.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        view.animate()
                .translationX(translationX)
                .setDuration(duration)
                .setInterpolator(new DecelerateInterpolator())
                .setStartDelay(startDelay)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        view.setLayerType(View.LAYER_TYPE_NONE, null);
                    }
                });

    }

    public static void translationX(View view,int translationX,int duration,int startDelay,Animator.AnimatorListener listener){
        view.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        view.animate()
                .translationX(translationX)
                .setDuration(duration)
                .setStartDelay(startDelay)
                .setListener(listener);
    }

    public static void alpha(final View view,float alpha,int duration,int startDelay){
        view.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        view.animate()
                .alpha(alpha)
                .setInterpolator(new DecelerateInterpolator())
                .setDuration(duration)
                .setStartDelay(startDelay)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        view.setLayerType(View.LAYER_TYPE_NONE, null);
                    }
                });

    }

    public static void alpha(View view,float alpha,int duration,int startDelay,Animator.AnimatorListener listener){
        view.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        view.animate()
                .alpha(alpha)
                .setDuration(duration)
                .setStartDelay(startDelay)
                .setInterpolator(new DecelerateInterpolator())
                .setListener(listener);
    }


    public static void rotation(View view,float rotation,int duration,int startDelay,Animator.AnimatorListener listener){
        view.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        view.animate()
                .rotation(rotation)
                .setDuration(duration)
                .setStartDelay(startDelay)
                .setInterpolator(new DecelerateInterpolator())
                .setListener(listener);
    }



    public static void shake(View view){
        view.startAnimation(AnimationUtils.loadAnimation(view.getContext(), R.anim.shake));
    }
}
