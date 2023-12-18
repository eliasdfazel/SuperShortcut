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

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
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

    static String splitPackageOne, splitPackageTwo = "";

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

            if (!functionsClass.AccessibilityServiceEnabled() && !functionsClass.SettingServiceRunning(SplitScreenService.class)) {

                functionsClass.AccessibilityService(this, true);

            } else {

                final AccessibilityManager accessibilityManager = (AccessibilityManager) getSystemService(ACCESSIBILITY_SERVICE);

                if (getIntent().getAction().equals("load_split_action_pair")) {

                    splitPackageOne = getIntent().getStringArrayExtra("packages")[0];
                    splitPackageTwo = getIntent().getStringArrayExtra("packages")[1];

                } else if (getIntent().getAction().equals("load_split_action_pair_shortcut")) {

                    String categoryName = getIntent().getStringExtra(Intent.EXTRA_TEXT);

                    splitPackageOne = functionsClass.readFileLine(categoryName)[0];
                    splitPackageTwo = functionsClass.readFileLine(categoryName)[1];

                }

                AccessibilityEvent accessibilityEvent = AccessibilityEvent.obtain();
                accessibilityEvent.setSource(new Button(getApplicationContext()));
                accessibilityEvent.setEventType(AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED);
                accessibilityEvent.setAction(10296);
                accessibilityEvent.setClassName(SplitTransparentPair.class.getSimpleName());
                accessibilityEvent.getText().add(getPackageName());
                accessibilityManager.sendAccessibilityEvent(accessibilityEvent);

                Intent splitOne = getPackageManager().getLaunchIntentForPackage(splitPackageOne);
                splitOne.addFlags(
                        Intent.FLAG_ACTIVITY_LAUNCH_ADJACENT |
                                Intent.FLAG_ACTIVITY_NEW_TASK);

                startActivity(splitOne);

                functionsClass.Toast(functionsClass.appName(splitPackageOne), Gravity.TOP);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        SplitTransparentPair.this.finish();

    }

}
