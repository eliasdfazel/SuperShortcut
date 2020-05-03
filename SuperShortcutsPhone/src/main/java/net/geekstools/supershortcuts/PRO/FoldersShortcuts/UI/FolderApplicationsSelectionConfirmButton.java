/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 5/3/20 7:31 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.supershortcuts.PRO.FoldersShortcuts.UI;

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

public class FolderApplicationsSelectionConfirmButton extends Button
        implements SimpleGestureFilter.SimpleGestureListener {

    FunctionsClass functionsClass;
    Context context;

    SimpleGestureFilter detector;
    BroadcastReceiver visibilityReceiver;

    public FolderApplicationsSelectionConfirmButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        functionsClass = new FunctionsClass(context);
        initConfirmButton();
    }

    public FolderApplicationsSelectionConfirmButton(Context context) {
        super(context);
    }

    public void initConfirmButton() {
        detector = new SimpleGestureFilter(context, this);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(context.getString(R.string.visibilityActionAdvance));
        intentFilter.addAction(context.getString(R.string.animtaionActionAdvance));
        visibilityReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(context.getString(R.string.visibilityActionAdvance))) {
                    FolderApplicationsSelectionConfirmButton.this.setBackground(context.getDrawable(R.drawable.ripple_effect_confirm));
                    if (!FolderApplicationsSelectionConfirmButton.this.isShown()) {
                        FolderApplicationsSelectionConfirmButton.this.startAnimation(AnimationUtils.loadAnimation(context, android.R.anim.fade_in));
                        FolderApplicationsSelectionConfirmButton.this.setVisibility(VISIBLE);
                    }
                } else if (intent.getAction().equals(context.getString(R.string.animtaionActionAdvance))) {
                    FolderApplicationsSelectionConfirmButton.this.startAnimation(AnimationUtils.loadAnimation(context, R.anim.scale_confirm_button));
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
                context.sendBroadcast(new Intent(context.getString(R.string.savedActionAdvance)));
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (functionsClass.countLine(PublicVariable.categoryName) > 0) {
                            FolderApplicationsSelectionConfirmButton.this.setBackground(context.getDrawable(R.drawable.draw_saved_dismiss));
                        }
                    }
                }, 200);
                break;
            case SimpleGestureFilter.SWIPE_RIGHT:
                context.sendBroadcast(new Intent(context.getString(R.string.savedActionAdvance)));
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (functionsClass.countLine(PublicVariable.categoryName) > 0) {
                            FolderApplicationsSelectionConfirmButton.this.setBackground(context.getDrawable(R.drawable.draw_saved_dismiss));
                        }
                    }
                }, 200);
                break;
            case SimpleGestureFilter.SWIPE_UP:
                context.sendBroadcast(new Intent(context.getString(R.string.savedActionAdvance)));
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (functionsClass.countLine(PublicVariable.categoryName) > 0) {
                            FolderApplicationsSelectionConfirmButton.this.setBackground(context.getDrawable(R.drawable.draw_saved_dismiss));
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
//            functionsClass.overrideBackPress(AdvanceShortcuts.class,
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
