/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 5/4/20 12:51 PM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.supershortcuts.PRO.SplitShortcuts.SplitServices;

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

                    final Intent splitTwo = getPackageManager().getLaunchIntentForPackage(SplitTransparentPair.splitPackageTwo);
                    splitTwo.addFlags(
                            Intent.FLAG_ACTIVITY_LAUNCH_ADJACENT |
                                    Intent.FLAG_ACTIVITY_NEW_TASK);

                    startActivity(splitTwo);

                } else if (event.getAction() == 69201) {

                    className = (String) event.getClassName();

                    performGlobalAction(GLOBAL_ACTION_TOGGLE_SPLIT_SCREEN);

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
