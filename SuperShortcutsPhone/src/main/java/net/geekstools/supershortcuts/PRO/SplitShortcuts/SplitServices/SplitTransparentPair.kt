/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 5/4/20 1:17 PM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */
package net.geekstools.supershortcuts.PRO.SplitShortcuts.SplitServices

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityManager
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import net.geekstools.supershortcuts.PRO.R
import net.geekstools.supershortcuts.PRO.SecurityServices.Protection
import net.geekstools.supershortcuts.PRO.SecurityServices.SecurityServicesProcess
import net.geekstools.supershortcuts.PRO.Utils.Functions.FunctionsClass
import net.geekstools.supershortcuts.PRO.Utils.Functions.SystemInformation

class SplitTransparentPair : AppCompatActivity() {

    companion object {
        var splitPackageOne = ""
        var splitPackageTwo = ""
    }

    val systemInformation by lazy {
        SystemInformation(applicationContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val securityServicesProcess = SecurityServicesProcess(this@SplitTransparentPair)

        if (securityServicesProcess.securityServiceEnabled()) {

            securityServicesProcess.protectIt(intent.getStringExtra(Intent.EXTRA_TEXT)!!,
                object : Protection {

                    override fun processNotProtected() {
                        splitProcess()
                    }

                    override fun processProtected() {
                        Toast.makeText(applicationContext, getString(R.string.notAuthorized), Toast.LENGTH_LONG).show()

                        this@SplitTransparentPair.finish()

                    }

                })

        } else {

            splitProcess()

        }
    }

    fun splitProcess() {
        try {

            val functionsClass: FunctionsClass = FunctionsClass(applicationContext)

            val window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            window.statusBarColor = Color.TRANSPARENT
            getWindow().navigationBarColor = Color.TRANSPARENT

            if (!functionsClass.AccessibilityServiceEnabled() && !functionsClass!!.SettingServiceRunning(SplitScreenService::class.java)) {

                functionsClass.AccessibilityService(this, true)

            } else {
                val accessibilityManager = getSystemService(ACCESSIBILITY_SERVICE) as AccessibilityManager

                if (intent.action == "load_split_action_pair") {

                    splitPackageOne = intent.getStringArrayExtra("packages")!![0]
                    splitPackageTwo = intent.getStringArrayExtra("packages")!![1]

                } else if (intent.action == "load_split_action_pair_shortcut") {

                    val categoryName = intent.getStringExtra(Intent.EXTRA_TEXT)

                    splitPackageOne = functionsClass.readFileLine(categoryName)!![0]
                    splitPackageTwo = functionsClass.readFileLine(categoryName)!![1]

                }

                val accessibilityEvent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    AccessibilityEvent()
                } else {
                    AccessibilityEvent.obtain()
                }
                accessibilityEvent.setSource(Button(applicationContext))
                accessibilityEvent.eventType = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED
                accessibilityEvent.action = 10296
                accessibilityEvent.className = SplitTransparentPair::class.java.simpleName
                accessibilityEvent.text.add(packageName)

                accessibilityManager.sendAccessibilityEvent(accessibilityEvent)

                val splitIntent = packageManager.getLaunchIntentForPackage(SplitTransparentPair.splitPackageTwo)
                splitIntent?.addFlags(
                    Intent.FLAG_ACTIVITY_LAUNCH_ADJACENT or
                            Intent.FLAG_ACTIVITY_NEW_TASK
                )
                startActivity(splitIntent)
                Log.d(this@SplitTransparentPair.javaClass.simpleName, "Split It: ${SplitTransparentPair.splitPackageTwo}")

            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    public override fun onPause() {
        super.onPause()
    }

    public override fun onDestroy() {
        super.onDestroy()

        this@SplitTransparentPair.finish()

    }

}
