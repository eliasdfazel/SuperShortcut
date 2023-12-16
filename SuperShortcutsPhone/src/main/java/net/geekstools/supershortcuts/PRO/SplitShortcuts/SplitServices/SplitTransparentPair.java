/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 5/4/20 1:17 PM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.supershortcuts.PRO.SplitShortcuts.SplitServices;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import net.geekstools.supershortcuts.PRO.R;
import net.geekstools.supershortcuts.PRO.SecurityServices.Protection;
import net.geekstools.supershortcuts.PRO.SecurityServices.SecurityServicesProcess;
import net.geekstools.supershortcuts.PRO.Utils.Functions.FunctionsClass;

public class SplitTransparentPair extends AppCompatActivity {

    FunctionsClass functionsClass;

    String packageNameSplitOne,
            packageNameSplitTwo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SecurityServicesProcess securityServicesProcess = new SecurityServicesProcess(SplitTransparentPair.this);

        if (securityServicesProcess.securityServiceEnabled()) {

            securityServicesProcess.protectIt(getIntent().getStringExtra(Intent.EXTRA_TEXT), new Protection() {

                @Override
                public void processNotProtected() {

                    splitProcess();

                }

                @Override
                public void processProtected() {

                    Toast.makeText(getApplicationContext(), getString(R.string.notAuthorized), Toast.LENGTH_LONG).show();

                    SplitTransparentPair.this.finish();

                }

            });

        } else {

            splitProcess();

        }

    }

    void splitProcess() {

        try {
            functionsClass = new FunctionsClass(getApplicationContext());

            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            window.setStatusBarColor(Color.TRANSPARENT);
            getWindow().setNavigationBarColor(Color.TRANSPARENT);

            final AccessibilityManager accessibilityManager = (AccessibilityManager) getSystemService(ACCESSIBILITY_SERVICE);
            if (!functionsClass.AccessibilityServiceEnabled() && !functionsClass.SettingServiceRunning(SplitScreenService.class)) {
                functionsClass.AccessibilityService(this, true);
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
                BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {

                        if (intent.getAction().equals("Split_Apps_Pair_" + SplitTransparentPair.class.getSimpleName())) {

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {

                                    Intent splitOne = getPackageManager().getLaunchIntentForPackage(packageNameSplitOne);
                                    splitOne.addCategory(Intent.CATEGORY_LAUNCHER);
                                    splitOne.setFlags(
                                            Intent.FLAG_ACTIVITY_LAUNCH_ADJACENT |
                                                    Intent.FLAG_ACTIVITY_NEW_TASK |
                                                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK);

                                    startActivity(splitOne);

                                    functionsClass.Toast(functionsClass.appName(packageNameSplitOne), Gravity.TOP);

                                    new Handler().postDelayed(new Runnable() {

                                        @Override
                                        public void run() {

                                            final Intent splitTwo = getPackageManager().getLaunchIntentForPackage(packageNameSplitTwo);
                                            splitTwo.addCategory(Intent.CATEGORY_LAUNCHER);
                                            splitTwo.setFlags(
                                                    Intent.FLAG_ACTIVITY_LAUNCH_ADJACENT |
                                                            Intent.FLAG_ACTIVITY_NEW_TASK |
                                                            Intent.FLAG_ACTIVITY_MULTIPLE_TASK);

                                            startActivity(splitTwo);

                                            functionsClass.Toast(functionsClass.appName(packageNameSplitTwo), Gravity.BOTTOM);

                                            new Handler().postDelayed(new Runnable() {

                                                @Override
                                                public void run() {

                                                    sendBroadcast(new Intent("split_pair_finish"));

                                                }

                                            }, 500);
                                        }

                                    }, 200);

                                }
                            }, 500);

                        } else if (intent.getAction().equals("split_pair_finish")) {

                            SplitTransparentPair.this.finish();

                        }
                    }
                };

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

                    registerReceiver(broadcastReceiver, intentFilter, Context.RECEIVER_NOT_EXPORTED);

                } else {

                    registerReceiver(broadcastReceiver, intentFilter);

                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
