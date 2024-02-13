/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 5/7/20 10:16 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */
package net.geekstools.supershortcuts.PRO

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import net.geekstools.supershortcuts.PRO.SecurityServices.SecurityServicesProcess
import net.geekstools.supershortcuts.PRO.Utils.Functions.PublicVariable

class EntryConfigurations : AppCompatActivity() {

    val securityServicesProcess: SecurityServicesProcess by lazy {
        SecurityServicesProcess(this@EntryConfigurations)
    }

    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)

        FirebaseApp.initializeApp(applicationContext)

        loadInstalledCustomIconPackages()

        shortcutModeEntryPoint()

    }



    private fun loadInstalledCustomIconPackages() = CoroutineScope(SupervisorJob() + Dispatchers.IO).async {
        val packageManager = applicationContext.packageManager
        //ACTION: com.novalauncher.THEME
        //CATEGORY: com.novalauncher.category.CUSTOM_ICON_PICKER
        val intentCustomIcons = Intent()
        intentCustomIcons.action = "com.novalauncher.THEME"
        intentCustomIcons.addCategory("com.novalauncher.category.CUSTOM_ICON_PICKER")
        val resolveInfos = packageManager.queryIntentActivities(intentCustomIcons, 0)

        PublicVariable.customIconsPackages.clear()
        for (resolveInfo in resolveInfos) {
            PublicVariable.customIconsPackages.add(resolveInfo.activityInfo.packageName)
        }
    }
}