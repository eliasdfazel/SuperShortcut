/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 5/4/20 9:17 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.supershortcuts.PRO.SplitShortcuts.UI;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Toast;

import net.geekstools.supershortcuts.PRO.R;
import net.geekstools.supershortcuts.PRO.Utils.Functions.FunctionsClass;
import net.geekstools.supershortcuts.PRO.Utils.Functions.PublicVariable;
import net.geekstools.supershortcuts.PRO.Utils.SimpleGestureFilter;

public class SplitApplicationsSelectionConfirmButton extends Button
        implements SimpleGestureFilter.SimpleGestureListener {

    FunctionsClass functionsClass;
    Context context;

    SimpleGestureFilter detector;
    BroadcastReceiver visibilityReceiver;

    public SplitApplicationsSelectionConfirmButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        functionsClass = new FunctionsClass(context);
        initConfirmButton();
    }

    public SplitApplicationsSelectionConfirmButton(Context context) {
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
                    SplitApplicationsSelectionConfirmButton.this.setBackground(context.getDrawable(R.drawable.ripple_effect_confirm));
                    if (!SplitApplicationsSelectionConfirmButton.this.isShown()) {
                        SplitApplicationsSelectionConfirmButton.this.startAnimation(AnimationUtils.loadAnimation(context, android.R.anim.fade_in));
                        SplitApplicationsSelectionConfirmButton.this.setVisibility(VISIBLE);
                    }
                } else if (intent.getAction().equals(context.getString(R.string.animtaionActionSplit))) {
                    SplitApplicationsSelectionConfirmButton.this.startAnimation(AnimationUtils.loadAnimation(context, R.anim.scale_confirm_button));
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
                            SplitApplicationsSelectionConfirmButton.this.setBackground(context.getDrawable(R.drawable.draw_saved_dismiss));
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
                            SplitApplicationsSelectionConfirmButton.this.setBackground(context.getDrawable(R.drawable.draw_saved_dismiss));
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
                            SplitApplicationsSelectionConfirmButton.this.setBackground(context.getDrawable(R.drawable.draw_saved_dismiss));
                        }
                    }
                }, 200);
                break;
        }
    }

    @Override
    public void onSingleTapUp() {

        Toast.makeText(context, "ISSUE", Toast.LENGTH_SHORT).show();
//        try {
//            functionsClass.overrideBackPress(SplitShortcuts.class,
//                    ActivityOptions.makeCustomAnimation(context, android.R.anim.fade_in, R.anim.go_down));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    @Override
    public void onLongPress() {
        //
    }
}
