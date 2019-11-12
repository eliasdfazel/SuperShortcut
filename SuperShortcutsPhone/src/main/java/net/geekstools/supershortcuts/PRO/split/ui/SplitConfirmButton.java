/*
 * Copyright © 2019 By Geeks Empire.
 *
 * Created by Elias Fazel on 11/11/19 7:22 PM
 * Last modified 11/11/19 7:21 PM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.supershortcuts.PRO.split.ui;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.AnimationUtils;
import android.widget.Button;

import net.geekstools.supershortcuts.PRO.R;
import net.geekstools.supershortcuts.PRO.Util.Functions.FunctionsClass;
import net.geekstools.supershortcuts.PRO.Util.Functions.PublicVariable;
import net.geekstools.supershortcuts.PRO.Util.SimpleGestureFilter;
import net.geekstools.supershortcuts.PRO.split.SplitShortcuts;

public class SplitConfirmButton extends Button
        implements SimpleGestureFilter.SimpleGestureListener {

    FunctionsClass functionsClass;
    Context context;

    SimpleGestureFilter detector;
    BroadcastReceiver visibilityReceiver;

    public SplitConfirmButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        functionsClass = new FunctionsClass(context, (Activity) getContext());
        initConfirmButton();
    }

    public SplitConfirmButton(Context context) {
        super(context);
    }

    public void initConfirmButton() {
        detector = new SimpleGestureFilter(context, this);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(context.getString(R.string.visibilityActionSplit));
        intentFilter.addAction(context.getString(R.string.animtaionActionSplit));
        visibilityReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(context.getString(R.string.visibilityActionSplit))) {
                    SplitConfirmButton.this.setBackground(context.getDrawable(R.drawable.ripple_effect_confirm));
                    if (!SplitConfirmButton.this.isShown()) {
                        SplitConfirmButton.this.startAnimation(AnimationUtils.loadAnimation(context, android.R.anim.fade_in));
                        SplitConfirmButton.this.setVisibility(VISIBLE);
                    }
                } else if (intent.getAction().equals(context.getString(R.string.animtaionActionSplit))) {
                    SplitConfirmButton.this.startAnimation(AnimationUtils.loadAnimation(context, R.anim.scale_confirm_button));
                }
            }
        };
        context.registerReceiver(visibilityReceiver, intentFilter);
        PublicVariable.confirmButtonX = this.getX();
        PublicVariable.confirmButtonY = this.getY();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        getContext().unregisterReceiver(visibilityReceiver);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        this.detector.onTouchEvent(motionEvent);

        return super.dispatchTouchEvent(motionEvent);
    }

    @Override
    public void onSwipe(int direction) {
        switch (direction) {
            case SimpleGestureFilter.SWIPE_DOWN:
                break;
            case SimpleGestureFilter.SWIPE_LEFT:
                context.sendBroadcast(new Intent(context.getString(R.string.savedActionSplit)));
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (functionsClass.countLine(PublicVariable.categoryName) > 0) {
                            SplitConfirmButton.this.setBackground(context.getDrawable(R.drawable.ic_cancel_stable_dark));
                        }
                    }
                }, 200);
                break;
            case SimpleGestureFilter.SWIPE_RIGHT:
                context.sendBroadcast(new Intent(context.getString(R.string.savedActionSplit)));
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (functionsClass.countLine(PublicVariable.categoryName) > 0) {
                            SplitConfirmButton.this.setBackground(context.getDrawable(R.drawable.ic_cancel_stable_dark));
                        }
                    }
                }, 200);
                break;
            case SimpleGestureFilter.SWIPE_UP:
                context.sendBroadcast(new Intent(context.getString(R.string.savedActionSplit)));
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (functionsClass.countLine(PublicVariable.categoryName) > 0) {
                            SplitConfirmButton.this.setBackground(context.getDrawable(R.drawable.ic_cancel_stable_dark));
                        }
                    }
                }, 200);
                break;
        }
    }

    @Override
    public void onSingleTapUp() {
        try {
            functionsClass.overrideBackPress(SplitShortcuts.class,
                    ActivityOptions.makeCustomAnimation(context, android.R.anim.fade_in, R.anim.go_down));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLongPress() {
        //
    }
}
