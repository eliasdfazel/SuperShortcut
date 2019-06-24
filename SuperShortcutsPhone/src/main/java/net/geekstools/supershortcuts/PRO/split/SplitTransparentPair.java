package net.geekstools.supershortcuts.PRO.split;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.widget.Button;

import net.geekstools.supershortcuts.PRO.Util.Functions.FunctionsClass;

public class SplitTransparentPair extends Activity {

    FunctionsClass functionsClass;
    BroadcastReceiver broadcastReceiver;

    String packageNameSplitOne, packageNameSplitTwo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            functionsClass = new FunctionsClass(getApplicationContext(), this);

            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            window.setStatusBarColor(Color.TRANSPARENT);
            getWindow().setNavigationBarColor(Color.TRANSPARENT);

            final AccessibilityManager accessibilityManager = (AccessibilityManager) getSystemService(ACCESSIBILITY_SERVICE);
            if (!functionsClass.AccessibilityServiceEnabled() && !functionsClass.SettingServiceRunning(SplitScreenService.class)) {
                functionsClass.AccessibilityService(this);
            } else {
                AccessibilityEvent event = AccessibilityEvent.obtain();
                event.setSource(new Button(getApplicationContext()));
                event.setEventType(AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED);
                event.setAction(10296);
                event.setClassName(SplitTransparentPair.class.getSimpleName());
                event.getText().add(getPackageName());
                accessibilityManager.sendAccessibilityEvent(event);

                if (getIntent().getAction().equals("load_split_action_pair")) {
                    packageNameSplitOne = getIntent().getStringArrayExtra("packages")[0];
                    packageNameSplitTwo = getIntent().getStringArrayExtra("packages")[1];
                } else if (getIntent().getAction().equals("load_split_action_pair_shortcut")) {
                    String categoryName = getIntent().getStringExtra(Intent.EXTRA_TEXT);
                    packageNameSplitOne = functionsClass.readFileLine(categoryName)[0];
                    packageNameSplitTwo = functionsClass.readFileLine(categoryName)[1];
                }


                IntentFilter intentFilter = new IntentFilter();
                intentFilter.addAction("split_pair_finish");
                intentFilter.addAction("Split_Apps_Pair_" + SplitTransparentPair.class.getSimpleName());
                broadcastReceiver = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        if (intent.getAction().equals("Split_Apps_Pair_" + SplitTransparentPair.class.getSimpleName())) {

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Intent spliteOne = getPackageManager().getLaunchIntentForPackage(packageNameSplitOne);
                                    spliteOne.addCategory(Intent.CATEGORY_LAUNCHER);
                                    spliteOne.setFlags(
                                            Intent.FLAG_ACTIVITY_LAUNCH_ADJACENT |
                                                    Intent.FLAG_ACTIVITY_NEW_TASK |
                                                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK);

                                    final Intent spliteTwo = getPackageManager().getLaunchIntentForPackage(packageNameSplitTwo);
                                    spliteTwo.addCategory(Intent.CATEGORY_LAUNCHER);
                                    spliteTwo.setFlags(
                                            Intent.FLAG_ACTIVITY_LAUNCH_ADJACENT |
                                                    Intent.FLAG_ACTIVITY_NEW_TASK |
                                                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK);

                                    startActivity(spliteOne);
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            startActivity(spliteTwo);
                                            new Handler().postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    sendBroadcast(new Intent("split_pair_finish"));
                                                }
                                            }, 500);
                                        }
                                    }, 200);

                                    functionsClass.Toast(functionsClass.appName(packageNameSplitOne), Gravity.TOP);
                                    functionsClass.Toast(functionsClass.appName(packageNameSplitTwo), Gravity.BOTTOM);
                                }
                            }, 500);
                        } else if (intent.getAction().equals("split_pair_finish")) {
                            SplitTransparentPair.this.finish();
                        }
                    }
                };
                registerReceiver(broadcastReceiver, intentFilter);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}
