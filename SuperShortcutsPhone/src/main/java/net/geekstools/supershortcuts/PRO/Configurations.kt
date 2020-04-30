/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 4/30/20 6:15 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */
package net.geekstools.supershortcuts.PRO

import android.R
import android.app.Activity
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import net.geekstools.supershortcuts.PRO.ApplicationsShortcuts.NormalAppShortcutsSelectionList
import net.geekstools.supershortcuts.PRO.FoldersShortcuts.AdvanceShortcuts
import net.geekstools.supershortcuts.PRO.SplitShortcuts.SplitShortcuts
import net.geekstools.supershortcuts.PRO.Utils.Functions.FunctionsClass

class Configurations : Activity() {

    lateinit var firebaseAuthentication: FirebaseAuth

    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        FirebaseApp.initializeApp(applicationContext)

        val functionsClass = FunctionsClass(applicationContext)

        functionsClass.savePreference(".UserInformation", "isBetaTester", functionsClass.appVersionName(packageName).contains("[BETA]"))
        functionsClass.savePreference(".UserInformation", "installedVersionCode", functionsClass.appVersionCode(packageName))
        functionsClass.savePreference(".UserInformation", "installedVersionName", functionsClass.appVersionName(packageName))
        functionsClass.savePreference(".UserInformation", "deviceModel", functionsClass.deviceName)
        functionsClass.savePreference(".UserInformation", "userRegion", functionsClass.countryIso)
        if (functionsClass.appVersionName(packageName).contains("[BETA]")) {
            functionsClass.saveDefaultPreference("JoinedBetaProgrammer", true)
        }

        firebaseAuthentication = FirebaseAuth.getInstance()

        if (firebaseAuthentication.currentUser == null) {


        } else {

            when (getSharedPreferences("ShortcutsModeView", Context.MODE_PRIVATE).getString("TabsView", NormalAppShortcutsSelectionList::class.java.simpleName)) {
                NormalAppShortcutsSelectionList::class.java.simpleName -> {

                    startActivity(Intent(applicationContext, NormalAppShortcutsSelectionList::class.java),
                            ActivityOptions.makeCustomAnimation(applicationContext, R.anim.fade_in, R.anim.fade_out).toBundle())
                }
                SplitShortcuts::class.java.simpleName -> {

                    startActivity(Intent(applicationContext, SplitShortcuts::class.java),
                            ActivityOptions.makeCustomAnimation(applicationContext, R.anim.fade_in, R.anim.fade_out).toBundle())
                }
                AdvanceShortcuts::class.java.simpleName -> {

                    startActivity(Intent(applicationContext, AdvanceShortcuts::class.java),
                            ActivityOptions.makeCustomAnimation(applicationContext, R.anim.fade_in, R.anim.fade_out).toBundle())
                }
                else -> {

                    startActivity(Intent(applicationContext, NormalAppShortcutsSelectionList::class.java),
                            ActivityOptions.makeCustomAnimation(applicationContext, R.anim.fade_in, R.anim.fade_out).toBundle())
                }
            }
        }

    }

    override fun onResume() {
        super.onResume()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)


    }
}