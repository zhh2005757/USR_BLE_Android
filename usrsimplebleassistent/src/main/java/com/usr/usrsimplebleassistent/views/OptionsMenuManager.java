package com.usr.usrsimplebleassistent.views;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.OvershootInterpolator;

import com.usr.usrsimplebleassistent.adapter.OptionsSelectAdapter;
import com.usr.usrsimplebleassistent.bean.Option;

import java.util.List;

/**
 * Created by liu on 15/8/9.
 */
public class OptionsMenuManager implements View.OnAttachStateChangeListener{
    private static OptionsMenuManager instance;
    private OptionsMenu optionsMenu;
    private boolean isShowing;
    private boolean isDismissing;


    public static OptionsMenuManager getInstance() {
        if (instance == null) {
            instance = new OptionsMenuManager();
        }
        return instance;
    }

    private OptionsMenuManager() {
    }

    public void toggleContextMenuFromView(List<Option> list,View openingView,OptionsSelectAdapter.OptionsOnItemSelectedListener listener) {
        if (optionsMenu == null) {
            showConnectionsMenu(list, openingView, listener);
        } else {
            dismissConfigDialog();
        }
    }





    private void showConnectionsMenu(List<Option> list,View openingView,OptionsSelectAdapter.OptionsOnItemSelectedListener listener) {
        if (!isShowing) {
            isShowing = true;
            optionsMenu = new OptionsMenu(openingView.getContext(),list,listener);
            initConfigDialog(openingView);
        }
    }




    private void initConfigDialog(final View openingView){
        optionsMenu.addOnAttachStateChangeListener(this);
        ((ViewGroup) openingView.getRootView().findViewById(android.R.id.content)).addView(optionsMenu);
        optionsMenu.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                optionsMenu.getViewTreeObserver().removeOnPreDrawListener(this);
                setupContextMenuInitialPosition(openingView);
                performShowAnimation();
                return false;
            }
        });
    }


    private void setupContextMenuInitialPosition(View openingView) {
        final int[] location = new int[2];
        openingView.getLocationOnScreen(location);

        optionsMenu.setTranslationX(location[0]);
        optionsMenu.setTranslationY(location[1] -optionsMenu.getHeight());
    }

    private void performShowAnimation() {
        optionsMenu.setPivotX(0);
        optionsMenu.setPivotY(optionsMenu.getHeight());
        optionsMenu.setScaleX(0.1f);
        optionsMenu.setScaleY(0.1f);
        optionsMenu.setAlpha(0f);
        optionsMenu.animate()
                .scaleX(1f).scaleY(1f).alpha(1.0f)
                .setDuration(150)
                .setInterpolator(new OvershootInterpolator())
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        isShowing = false;
                    }
                });
    }


    private void dismissConfigDialog() {
        if (!isDismissing) {
            isDismissing = true;
            performDismissAnimation();
        }
    }


    private void performDismissAnimation() {
        optionsMenu.setPivotX(0);
        optionsMenu.setPivotY(optionsMenu.getHeight());
        optionsMenu.animate()
                .scaleX(0.1f).scaleY(0.1f).alpha(0f)
                .setDuration(150)
                .setInterpolator(new AccelerateInterpolator())
                .setStartDelay(100)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if (optionsMenu != null) {
                            optionsMenu.dismiss();
                        }
                        isDismissing = false;
                    }
                });
    }

    @Override
    public void onViewAttachedToWindow(View v) {

    }

    @Override
    public void onViewDetachedFromWindow(View v) {
        optionsMenu = null;
    }

    public OptionsMenu getOptionsMenu() {
        return optionsMenu;
    }
}
