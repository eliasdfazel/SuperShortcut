/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 5/4/20 1:10 PM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */
package net.geekstools.supershortcuts.PRO.SplitShortcuts.SplitServices

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityManager
import android.widget.Button
import net.geekstools.supershortcuts.PRO.Utils.Functions.FunctionsClass

class SplitTransparentSingle : Activity() {

    companion object {
        var splitPackageOne: String? = ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val functionsClass = FunctionsClass(applicationContext)

        val window = window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        window.statusBarColor = Color.TRANSPARENT
        getWindow().navigationBarColor = Color.TRANSPARENT

        if (!functionsClass.AccessibilityServiceEnabled()) {

            functionsClass.AccessibilityService(this, true)

        } else {


            splitPackageOne = intent.getStringExtra("package")
            val splitOne = packageManager.getLaunchIntentForPackage(splitPackageOne!!)
            splitOne!!.addFlags(Intent.FLAG_ACTIVITY_LAUNCH_ADJACENT or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(splitOne)

            val accessibilityManager = getSystemService(ACCESSIBILITY_SERVICE) as AccessibilityManager

            val accessibilityEvent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                AccessibilityEvent()
            } else {
                AccessibilityEvent.obtain()
            }
            accessibilityEvent.setSource(Button(applicationContext))
            accessibilityEvent.eventType = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED
            accessibilityEvent.action = 69201
            accessibilityEvent.className = SplitTransparentSingle::class.java.simpleName
            accessibilityEvent.text.add(packageName)

            accessibilityManager.sendAccessibilityEvent(accessibilityEvent)
        }
    }

    public override fun onPause() {
        super.onPause()
    }

    public override fun onDestroy() {
        super.onDestroy()

       this@SplitTransparentSingle.finish()
    }

}
