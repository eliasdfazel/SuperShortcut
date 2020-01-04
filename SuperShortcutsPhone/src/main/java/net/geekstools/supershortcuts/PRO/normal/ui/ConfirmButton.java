/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel on 1/3/20 8:24 PM
 * Last modified 1/3/20 7:04 PM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.supershortcuts.PRO.normal.ui;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.AnimationUtils;
import android.widget.Button;

import net.geekstools.supershortcuts.PRO.R;
import net.geekstools.supershortcuts.PRO.Util.Functions.FunctionsClass;
import net.geekstools.supershortcuts.PRO.Util.Functions.PublicVariable;
import net.geekstools.supershortcuts.PRO.Util.SimpleGestureFilter;

public class ConfirmButton extends Button
        implements SimpleGestureFilter.SimpleGestureListener {

    FunctionsClass functionsClass;
    Context context;

    SimpleGestureFilter detector;
    BroadcastReceiver visibilityReceiver;

    public ConfirmButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        functionsClass = new FunctionsClass(context, (Activity) getContext());
        initConfirmButton();
    }

    public ConfirmButton(Context context) {
        super(context);
    }

    public void initConfirmButton() {
        detector = new SimpleGestureFilter(context, this);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(context.getString(R.string.visibilityAction));
        intentFilter.addAction(context.getString(R.string.animtaionAction));
        visibilityReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(context.getString(R.string.visibilityAction))) {
                    ConfirmButton.this.setBackground(context.getDrawable(R.drawable.ripple_effect_confirm));
                    if (!ConfirmButton.this.isShown()) {
                        ConfirmButton.this.startAnimation(AnimationUtils.loadAnimation(context, android.R.anim.fade_in));
                        ConfirmButton.this.setVisibility(VISIBLE);
                    }
                } else if (intent.getAction().equals(context.getString(R.string.animtaionAction))) {
                    ConfirmButton.this.startAnimation(AnimationUtils.loadAnimation(context, R.anim.scale_confirm_button));
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
    public boolean dispatchTouchEvent(MotionEvent me) {
        this.detector.onTouchEvent(me);

        return super.dispatchTouchEvent(me);
    }

    @Override
    public void onSwipe(int direction) {
        switch (direction) {
            case SimpleGestureFilter.SWIPE_DOWN:
                break;
            case SimpleGestureFilter.SWIPE_LEFT:
                context.sendBroadcast(new Intent(context.getString(R.string.savedAction)));
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (functionsClass.countLine(".autoSuper") > 0) {
                            ConfirmButton.this.setBackground(context.getDrawable(R.drawable.ic_cancel_stable_dark));
                        }
                    }
                }, 200);
                break;
            case SimpleGestureFilter.SWIPE_RIGHT:
                context.sendBroadcast(new Intent(context.getString(R.string.savedAction)));

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (functionsClass.countLine(".autoSuper") > 0) {
                            ConfirmButton.this.setBackground(context.getDrawable(R.drawable.ic_cancel_stable_dark));
                        }
                    }
                }, 200);
                break;
            case SimpleGestureFilter.SWIPE_UP:
                context.sendBroadcast(new Intent(context.getString(R.string.savedAction)));
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (functionsClass.countLine(".autoSuper") > 0) {
                            ConfirmButton.this.setBackground(context.getDrawable(R.drawable.ic_cancel_stable_dark));
                        }
                    }
                }, 200);
                break;
        }
    }

    @Override
    public void onSingleTapUp() {
        if (functionsClass.mixShortcuts() == true) {
            functionsClass.addMixAppShortcuts();
        } else {
            functionsClass.addAppShortcuts();
            SharedPreferences sharedPreferences = context.getSharedPreferences(".PopupShortcut", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("PopupShortcutMode", "AppShortcuts");
            editor.apply();
        }
    }

    @Override
    public void onLongPress() {
        try {
            functionsClass.deleteSelectedFiles();

            context.sendBroadcast(new Intent(context.getString(R.string.checkboxAction)));
            context.sendBroadcast(new Intent(context.getString(R.string.counterAction)));
            context.sendBroadcast(new Intent(context.getString(R.string.dynamicShortcuts)));
        } catch (Exception e) {
            e.printStackTrace();
        }

        functionsClass.clearDynamicShortcuts();
    }
}
