package net.geekstools.supershortcuts.PRO.split;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.widget.Button;

import net.geekstools.supershortcuts.PRO.Util.Functions.FunctionsClass;

public class SplitTransparentSingle extends Activity {

    FunctionsClass functionsClass;
    BroadcastReceiver broadcastReceiver;

    String packageNameSplit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        functionsClass = new FunctionsClass(getApplicationContext(), this);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        window.setStatusBarColor(Color.TRANSPARENT);
        getWindow().setNavigationBarColor(Color.TRANSPARENT);

        final AccessibilityManager accessibilityManager = (AccessibilityManager) getSystemService(ACCESSIBILITY_SERVICE);
        if (!functionsClass.AccessibilityServiceEnabled()) {
            functionsClass.AccessibilityService(this);
        } else {
            AccessibilityEvent event = AccessibilityEvent.obtain();
            event.setSource(new Button(getApplicationContext()));
            event.setEventType(AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED);
            event.setAction(69201);
            event.setClassName(SplitTransparentSingle.class.getSimpleName());
            event.getText().add(getPackageName());
            accessibilityManager.sendAccessibilityEvent(event);

            packageNameSplit = getIntent().getStringExtra("package");

            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("split_single_finish");
            intentFilter.addAction("Split_Apps_Single_" + SplitTransparentSingle.class.getSimpleName());
            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if (intent.getAction().equals("Split_Apps_Single_" + SplitTransparentSingle.class.getSimpleName())) {

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent spliteOne = getPackageManager().getLaunchIntentForPackage(packageNameSplit);
                                spliteOne.addCategory(Intent.CATEGORY_LAUNCHER);
                                spliteOne.setFlags(
                                        Intent.FLAG_ACTIVITY_LAUNCH_ADJACENT |
                                                Intent.FLAG_ACTIVITY_NEW_TASK |
                                                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);

                                startActivity(spliteOne);

                            }
                        }, 500);
                    } else if (intent.getAction().equals("split_single_finish")) {
                        SplitTransparentSingle.this.finish();
                    }
                }
            };
            registerReceiver(broadcastReceiver, intentFilter);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            unregisterReceiver(broadcastReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
