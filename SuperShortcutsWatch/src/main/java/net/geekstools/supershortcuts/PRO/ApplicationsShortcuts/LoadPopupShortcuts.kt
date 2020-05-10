/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 5/10/20 10:11 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */
package net.geekstools.supershortcuts.PRO.ApplicationsShortcuts

import android.content.Intent
import android.os.Bundle
import android.support.wearable.activity.WearableActivity
import android.widget.Toast
import com.google.firebase.FirebaseApp
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import net.geekstools.supershortcuts.PRO.R
import net.geekstools.supershortcuts.PRO.Utils.Functions.FunctionsClass
import net.geekstools.supershortcuts.PRO.databinding.EntryViewBinding

class LoadPopupShortcuts : WearableActivity() {

    private val functionsClass: FunctionsClass by lazy {
        FunctionsClass(applicationContext)
    }

    private lateinit var entryViewBinding: EntryViewBinding

    override fun onCreate(saved: Bundle?) {
        super.onCreate(saved)
        entryViewBinding = EntryViewBinding.inflate(layoutInflater)
        setContentView(entryViewBinding.root)

        FirebaseApp.initializeApp(applicationContext)

        if (functionsClass.countLine(NormalAppShortcutsSelectionListWatch.NormalApplicationsShortcutsFile) > 0) {

            entryViewBinding.rootView.post {

                functionsClass.showPopupItem(this@LoadPopupShortcuts,
                        entryViewBinding.popupAnchorView,
                        getString(R.string.app_name),
                        functionsClass.readFileLine(NormalAppShortcutsSelectionListWatch.NormalApplicationsShortcutsFile))
            }

            val firebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
            val configSettings = FirebaseRemoteConfigSettings.Builder()
                    .setFetchTimeoutInSeconds(0)
                    .build()
            firebaseRemoteConfig.setConfigSettingsAsync(configSettings)
            firebaseRemoteConfig.setDefaultsAsync(R.xml.remote_config_default)
            firebaseRemoteConfig.fetch()
                    .addOnCompleteListener(this@LoadPopupShortcuts) { task ->

                        if (task.isSuccessful) {

                            firebaseRemoteConfig.activate().addOnSuccessListener {

                                if (firebaseRemoteConfig.getLong(functionsClass.versionCodeRemoteConfigKey()) > functionsClass.appVersionCode(packageName)) {
                                    Toast.makeText(applicationContext, getString(R.string.updateAvailable), Toast.LENGTH_LONG).show()
                                }
                            }
                        }
                    }
        } else {

            startActivity(
                    Intent(applicationContext, NormalAppShortcutsSelectionListWatch::class.java))


            this@LoadPopupShortcuts.finish()
        }

        setAmbientEnabled()
    }

    public override fun onPause() {
        super.onPause()

        this@LoadPopupShortcuts.finish()
    }
}