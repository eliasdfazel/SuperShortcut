/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 5/6/20 9:05 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */
package net.geekstools.supershortcuts.PRO.ApplicationsShortcuts.Utils

import android.app.Activity
import android.content.Intent
import android.os.Bundle

class AppShortcutsMediatedActivity : Activity() {

    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)

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

                    startActivity(this)
                }

            }
        }

        this@AppShortcutsMediatedActivity.finish()
    }
}