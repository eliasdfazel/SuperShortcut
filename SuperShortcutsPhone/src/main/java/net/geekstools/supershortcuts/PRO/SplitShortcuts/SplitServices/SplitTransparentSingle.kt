/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 5/4/20 1:10 PM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.supershortcuts.PRO.SplitShortcuts.SplitServices;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.widget.Button;

import net.geekstools.supershortcuts.PRO.Utils.Functions.FunctionsClass;

public class SplitTransparentSingle extends Activity {

    FunctionsClass functionsClass;

    static String splitPackageOne = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        functionsClass = new FunctionsClass(getApplicationContext());

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        window.setStatusBarColor(Color.TRANSPARENT);
        getWindow().setNavigationBarColor(Color.TRANSPARENT);

        if (!functionsClass.AccessibilityServiceEnabled()) {

            functionsClass.AccessibilityService(this, true);

        } else {

            final AccessibilityManager accessibilityManager = (AccessibilityManager) getSystemService(ACCESSIBILITY_SERVICE);

            splitPackageOne = getIntent().getStringExtra("package");

            Intent splitOne = getPackageManager().getLaunchIntentForPackage(splitPackageOne);
            splitOne.addFlags(
                    Intent.FLAG_ACTIVITY_LAUNCH_ADJACENT |
                            Intent.FLAG_ACTIVITY_NEW_TASK);

            startActivity(splitOne);

            AccessibilityEvent event = AccessibilityEvent.obtain();
            event.setSource(new Button(getApplicationContext()));
            event.setEventType(AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED);
            event.setAction(69201);
            event.setClassName(SplitTransparentSingle.class.getSimpleName());
            event.getText().add(getPackageName());
            accessibilityManager.sendAccessibilityEvent(event);

        }

    }


    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        SplitTransparentSingle.this.finish();

    }
}
