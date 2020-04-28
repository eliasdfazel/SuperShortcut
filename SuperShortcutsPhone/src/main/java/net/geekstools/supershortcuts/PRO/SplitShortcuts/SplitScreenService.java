/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 4/28/20 10:18 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.supershortcuts.PRO.SplitShortcuts;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.view.accessibility.AccessibilityEvent;

public class SplitScreenService extends AccessibilityService {

    String className = "Default";

    @Override
    protected void onServiceConnected() {
    }

    @Override
    public void onAccessibilityEvent(final AccessibilityEvent event) {
        switch (event.getEventType()) {
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                if (event.getAction() == 10296) {
                    className = (String) event.getClassName();

                    performGlobalAction(GLOBAL_ACTION_TOGGLE_SPLIT_SCREEN);
                    sendBroadcast(new Intent("Split_Apps_Pair_" + className));
                } else if (event.getAction() == 69201) {
                    className = (String) event.getClassName();

                    performGlobalAction(GLOBAL_ACTION_TOGGLE_SPLIT_SCREEN);
                    sendBroadcast(new Intent("Split_Apps_Single_" + className));
                }

                break;
        }
    }

    @Override
    public void onInterrupt() {
        startService(new Intent(getApplicationContext(), SplitScreenService.class));
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
