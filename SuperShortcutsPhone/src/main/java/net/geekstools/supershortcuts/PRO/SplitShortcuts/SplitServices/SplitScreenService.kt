/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 5/4/20 12:51 PM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */
package net.geekstools.supershortcuts.PRO.SplitShortcuts.SplitServices

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.util.Log
import android.view.accessibility.AccessibilityEvent

class SplitScreenService : AccessibilityService() {

    var className: String? = "Default"

    override fun onServiceConnected() {}

    override fun onAccessibilityEvent(event: AccessibilityEvent) {

        when (event.eventType) {
            AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED -> if (event.action == 10296) {

                className = event.className as String?

                performGlobalAction(GLOBAL_ACTION_TOGGLE_SPLIT_SCREEN)

                val splitIntent = packageManager.getLaunchIntentForPackage(SplitTransparentPair.splitPackageOne)
                splitIntent?.addFlags(Intent.FLAG_ACTIVITY_LAUNCH_ADJACENT or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(splitIntent)
                Log.d(this@SplitScreenService.javaClass.simpleName, "Split It: ${SplitTransparentPair.splitPackageOne}")

            } else if (event.action == 69201) {

                className = event.className as String?

                performGlobalAction(GLOBAL_ACTION_TOGGLE_SPLIT_SCREEN)

            }
        }

    }

    override fun onInterrupt() {
        startService(Intent(applicationContext, SplitScreenService::class.java))
    }

    override fun onCreate() {
        super.onCreate()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

}
