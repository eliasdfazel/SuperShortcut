/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 6/11/20 10:50 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */
package net.geekstools.supershortcuts.PRO.ApplicationsShortcuts.Utils

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import net.geekstools.supershortcuts.PRO.R
import net.geekstools.supershortcuts.PRO.SecurityServices.Protection
import net.geekstools.supershortcuts.PRO.SecurityServices.SecurityServicesProcess

class AppShortcutsMediatedActivity : AppCompatActivity() {

    private val securityServicesProcess: SecurityServicesProcess by lazy {
        SecurityServicesProcess(this@AppShortcutsMediatedActivity)
    }

    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)

        if (securityServicesProcess.securityServiceEnabled()) {

            securityServicesProcess.protectIt(object : Protection {

                override fun processProtected() {

                    openApplication()

                }

                override fun processNotProtected() {

                    Toast.makeText(applicationContext, getString(R.string.notAuthorized), Toast.LENGTH_LONG).show()

                    this@AppShortcutsMediatedActivity.finish()

                }

            })

        } else {

            openApplication()

        }

    }

    fun openApplication() {

        intent?.let {
            val packageName = it.getStringExtra("PackageName")
            val className = it.getStringExtra("ClassName")

            packageName?.let {

                Intent().apply {
                    setPackage(packageName)
                    if (className != null) {
                        setClassName(packageName, className)
                    }
                    action = Intent.ACTION_MAIN
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    addCategory(Intent.CATEGORY_DEFAULT)

                    try {
                        startActivity(this@apply)
                    } catch (e: ActivityNotFoundException) {
                        e.printStackTrace()
                    }
                }

            }
        }

        this@AppShortcutsMediatedActivity.finish()

    }
    
}